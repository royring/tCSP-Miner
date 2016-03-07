package cn.edu.scu.dke.idsp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import cn.edu.scu.dke.idsp.util.BitSetUtils;
import cn.edu.scu.dke.idsp.util.PatternListUtil;
import cn.edu.scu.dke.idsp.util.SubBitSetList;
import cn.edu.scu.dke.idsp.vo.Element;
import cn.edu.scu.dke.idsp.vo.ElementsInfo;
import cn.edu.scu.dke.idsp.vo.Pattern;
import cn.edu.scu.dke.idsp.vo.PatternInterval;
import cn.edu.scu.dke.idsp.vo.Results;
import cn.edu.scu.dke.idsp.vo.Window;

public class GenerateCandidatePattern1 {
	/** 结果 */
	private Results results;
	/** 正列 */
	private ElementsInfo posEI;
	/** 负列 */
	private ElementsInfo negEI;
	/** 等长度区间划分块数 */
	private int l;
	/** 正列总条数 */
	private double posSize;
	/** 负列总条数 */
	private double negSize;
	/** top-k */
	private int topk;
	/** gap起始位置 */
	private long minGap;
	/** gap结束位置 */
	private long maxGap;
	private Window posWindow;
	private Window negWindow;
	/** my map index */
	private Map<BitSet, Integer> windowsIndexMap;
	/**
	 * Pruning rule 5:if |R|=topk and Sup(D+,(e,U(e)))<min(cScore), then pruning
	 * the e from E
	 */
	private Set<String> improperESet = new HashSet<String>();

	public int trycount = 0;

	public GenerateCandidatePattern1(ElementsInfo posEI, ElementsInfo negEI,
			int l, int topk, long minGap, long maxGap) {
		this.results = new Results(topk);
		this.posEI = posEI;
		this.negEI = negEI;
		this.posSize = posEI.getLenList().size();
		this.negSize = negEI.getLenList().size();
		this.topk = topk;
		this.l = l;
		this.minGap = minGap;
		this.maxGap = maxGap;
		posWindow = posEI.getWindow();
		negWindow = negEI.getWindow();
		windowsIndexMap = new HashMap<BitSet, Integer>();
		for (int i = 0; i < posWindow.getWindows().size(); i++) {
			windowsIndexMap.put(posWindow.getWindows().get(i), i);
		}
	}

	/** generate candidate patterns **/
	public void genCP() {

		Set<String> elements = posEI.geteMap().keySet();
		// window.print();
		// for (Pattern pattern : patterns) {
		// pattern.print();
		// }
		for (String e : elements) {
			/** Pruning rule 5 **/
			if (isImproperElement(e)) {
				continue;
			}
			Pattern pattern = Generate_Candidate_Pattern(e, posEI, negEI);
			if (pattern == null) {
				continue;
			}
			grows(pattern, elements, 1);
		}
	}

	public Pattern Generate_Candidate_Pattern(String element,
			ElementsInfo posEI, ElementsInfo negEI) {
		HashMap<String, Element> posEMap = posEI.geteMap();
		HashMap<String, Element> negEMap = negEI.geteMap();
		double posSup = posEMap.get(element).getpOccurrence().size()
				/ this.posSize;
		/** Pruning rule 4 */
		if (results.getCount() == topk && posSup < results.getMin_cScore()) {
			improperESet.add(element);
			return null;
		}
		HashMap<Integer, BitSet> pos_occurrence = new HashMap<Integer, BitSet>(); // 保存正列窗口组合的出现信息
		HashMap<Integer, BitSet> neg_occurrence = new HashMap<Integer, BitSet>(); // 保存负列窗口组合的出现信息
		BitSet upperbound = posEMap.get(element).getUpperboundBitSet();// element的upperbound
		BitSet negupperbound = negEMap.get(element).getUpperboundBitSet();// element的upperbound
		upperbound.or(negupperbound);// 合并正列和负例的上界；
		List<BitSet> ignoreList = new ArrayList<BitSet>();// 如果父区间不满足，那么可以直接忽略子区间的数据
		Pattern pattern = new Pattern(this.l);
		pattern.setPattern(element);
		// 设置各个window和window组合的bitset值
		pattern.setRegions(upperbound);
		PatternInterval patternInterval = new PatternInterval();
		patternInterval.setRegions(upperbound);
		patternInterval.setPattern(element);
		pos_occurrence = getOccurrenceByRegion(upperbound, posEMap.get(element)
				.getpOccurrence(), posWindow, true);
		patternInterval.setPosOccurrence(pos_occurrence);
		Element negElement = negEMap.get(element);
		if (negElement != null) {
			neg_occurrence = getOccurrenceByRegion(upperbound,
					negEMap.get(element).getpOccurrence(), negWindow, false);
			patternInterval.setNegOccurrence(neg_occurrence);
		}
		patternInterval.setPosSup(posSup);
		patternInterval.setNegSup(patternInterval.getNegOccurrence().size()
				/ negSize);
		patternInterval.setcRatio();
		pattern.setUpperboundPI(patternInterval);
		Queue<BitSet> queue = new LinkedList<BitSet>();
		BitSetUtils.obtainContinuousTimeInterval(queue, upperbound);
		while (queue.peek() != null) {// if the Upper bound is continue, then the map contain the upper bound
			BitSet region = queue.poll();
			BitSetUtils.queueContinuousTimeInterval(queue,region);
			if (isIgnoreListContainRegion(ignoreList, region)) {
				continue;
			}
			pos_occurrence = getOccurrenceByRegion(region, posEMap.get(element)
					.getpOccurrence(), posWindow, true);
			double posSup2 = pos_occurrence.size() / posSize;
			/** Pruning 6 */
			if (posSup2 == 0 || results.getCount() == topk
					&& posSup2 < results.getMin_cScore()) {
				ignoreList.add(region);
				continue;
			}
			patternInterval = new PatternInterval();
			patternInterval.setRegions(region);
			patternInterval.setPattern(element);
			patternInterval.setPosOccurrence(pos_occurrence);
			negElement = negEMap.get(element);
			if (negElement != null) {
				neg_occurrence = getOccurrenceByRegion(region,
						negEMap.get(element).getpOccurrence(), negWindow, false);
				patternInterval.setNegOccurrence(neg_occurrence);
			}
			patternInterval.setPosSup(posSup2);
			patternInterval.setNegSup(patternInterval.getNegOccurrence().size()
					/ negSize);
			patternInterval.setcRatio();
			pattern.addPatternInterval(BitSetUtils.BitSet2Index(region),
					patternInterval);
		}
		// 用element的上界来定义长度为一的pattern的上界
		pattern.setUpperboundBitSet(upperbound);
		// PatternListUtil.sortPatternIntervalList(pattern.getPatternlist());
		return pattern;
	}

	/**
	 * @Title: isImproperElement
	 * @param element
	 * @return boolean
	 * @throws
	 */
	private boolean isImproperElement(String element) {
		for (String str : improperESet) {
			if (str.equals(element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @Title: getOccurrenceByRegion
	 * @Description: 根据window和window组合的 起始位置信息，获取相应的bitset
	 * @param region
	 *            window和window组合信息
	 * @param occurrence
	 *            全局信息
	 * @param window
	 *            window对象
	 * @param pos
	 *            正列或负列
	 * @return HashMap<Integer,BitSet> 返回类型
	 * @throws
	 */
	private HashMap<Integer, BitSet> getOccurrenceByRegion(BitSet region,
			HashMap<Integer, BitSet> occurrence, Window window, boolean pos) {
		HashMap<Integer, BitSet> result_occurrence = new HashMap<Integer, BitSet>();
		Map<Integer, Integer[]> windowScope;// 相应的window起始信息
		if (pos) {
			windowScope = window.getWindowScope().get(region.toString());
		} else {
			windowScope = window.getWindowScope().get(region.toString());
		}
		for (Integer key : occurrence.keySet()) {
			BitSet cur = (BitSet) occurrence.get(key).clone();
			Integer[] windex = windowScope.get(key);
			regionClear(cur, windex);
			if (cur.cardinality() > 0) {
				result_occurrence.put(key, cur);
			}
		}
		return result_occurrence;
	}

	private void regionClear(BitSet cur, Integer[] windex) {
		BitSet mask = new BitSet(cur.size());
		int size = windex.length / 2;
		for (int i = 0, index = 0; i < size; i++) {
			if (windex[index] >= 0) {
				mask.set(windex[index++], windex[index++] + 1);
			} else {
				index += 2;
			}
		}
		cur.and(mask);
	}

	// /**
	// * if the support of an element in positve data sets is less than the
	// Top-k
	// * threshold in Results set, this element is impossible
	// **/
	// private boolean checkImpossible(String e) {
	// Element element = posEI.geteMap().get(e);
	// if (element.getpOccurrence().size() / posSize < results.getA()) {
	// System.out.println("Impossible");
	// return true;
	// } else
	// return false;
	// }

	/**
	 * 
	 * @Title: grows
	 * @Description: TODO
	 * @param p
	 * @param elements
	 * @param layer
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void grows(Pattern p, Set<String> elements, int layer) {
		/** check the pattern p in different condition of regions **/
		boolean continueFlag = checkPattern(p);
		if (!continueFlag) {
			/** pruning rule 6 **/
			/** this pattern is not existed, so does the super=pattern of it **/
			return;
		}

		/** add a new element to pattern p to generate a new pattern **/
		for (String newE : elements) {
			/** pruning 1 **/
			// TODO pruning rule 1
			trycount++;
			System.out.println(trycount);

			/** Pruning rule 5 **/
			if (isImproperElement(newE)) {
				continue;
			}

			// System.out.println("In? "+layer +
			// " size:"+results.getpList().size());
			/** create new pattern **/
			Pattern newPattern = addNewElement(p.clone(), newE);
			if (newPattern == null) {
				continue;
			}
			grows(newPattern, elements, layer + 1);
		}
	}

	/**
	 * @Title: addNewElement
	 * @Description: 增加element生成新的pattern，并根据gap计算出新pattern的位置信息。
	 * @param p
	 * @param newE
	 * @param posEI2
	 * @param negEI2
	 * @param minGap2
	 * @param maxGap2
	 * @return 设定文件
	 * @return Pattern 返回类型
	 * @throws
	 */
	private Pattern addNewElement(Pattern p, String newE) {
		Element posElement = posEI.geteMap().get(newE);
		Element negElement = negEI.geteMap().get(newE);
		double newE_posSup = posElement.getpOccurrence().size() / this.posSize;
		/** Pruning rule 5 */
		if (results.getCount() == topk && newE_posSup < results.getMin_cScore()) {
			improperESet.add(newE);
			return null;
		}
		String patternStr = p.getPattern() + "," + newE;
		p.setPattern(patternStr);
		if (patternStr.equals("A,A")) {
			System.out.println(patternStr);
		}
		PatternInterval[] newPatternIntervalArr = new PatternInterval[p
				.getPatternInterArr().length];// 保存新pattern的新的patternInterval
		int index = 0;// patternInterval 数组的索引
		List<Integer> piList = new ArrayList<Integer>();// 保存索引号
		// *******************upperboard generation****************
		BitSet oldUpperbound = (BitSet) p.getUpperboundBitSet().clone();
		BitSet newUpperbound = (BitSet) p.getUpperboundBitSet().clone();
		BitSet tmpbitset = (BitSet) posElement.getUpperboundBitSet().clone();
		int firstTrue = newUpperbound.nextSetBit(0);// Since the e must follow
													// the U(E'), the front of
													// U(E') need remove from
													// U(e).
		tmpbitset.set(0, firstTrue, false);
		newUpperbound.or(tmpbitset);
		List<BitSet> ignoreList = new ArrayList<BitSet>();// 如果在时间区间树中父区间不满足，那么可以直接忽略父节点的子区间的数据
		HashMap<Integer, BitSet> pos_last_element = posElement.getpOccurrence(); // 正列中最后一个element出现的BitSet集
		HashMap<Integer, BitSet> neg_last_element = null; // 负列中最后一个element出现的BitSet集
		if (negElement != null) {
			neg_last_element = negElement.getpOccurrence();
		}
		/** 首先判断新upperboard sequence的数据 */
		PatternInterval pi = p.getUpperboundPI();
		double posSup = getSupByWindow(pi.getPosOccurrence(), pos_last_element,
				posEI.getTimeMap(), newUpperbound.toString(), true) / posSize;
		/** pruning 2 pattern全局剪枝 */
		if (posSup == 0 || results.getCount() == topk
				&& posSup < results.getMin_cScore()) {
			return null;
		}
		double negSup = getSupByWindow(pi.getNegOccurrence(), neg_last_element,
				negEI.getTimeMap(), newUpperbound.toString(), false) / negSize;
		if (negSup == 0) {
			pi.getNegOccurrence().clear();
		}
		pi.setPattern(p.getPattern());
		pi.setPosSup(posSup);
		pi.setNegSup(negSup);
		pi.setcRatio();
		pi.setRegions(newUpperbound);

		// start at upperboard to enumerate the window tree
		Queue<BitSet> queue = new LinkedList<BitSet>();
		BitSetUtils.obtainContinuousTimeInterval(queue, newUpperbound);
		while (queue.peek() != null) {// if the Upper bound is continue, then the map contain the upper bound// 处理余下的window组合,余下是upperbound的真子集,余下的组合需要去掉多余的数据
			BitSet newregion = queue.poll();
			PatternInterval pitmp = null;
			String newregions = newregion.toString();
			// if (newregions.equals("{1, 3, 7, 8}")) {
			// System.out.println();
			// }

			BitSet region = (BitSet) newregion.clone();// 需要利用的父节点的窗口编号
			region.and(oldUpperbound);
			/** pruning 6 window从大到小计算，当一个w需要被剪掉，那么w所有的真子集也必须剪掉 */
			if (region.cardinality() == 0) {// 父节点中没有保存的window编号，需要剪掉
				ignoreList.add(newregion);// 保存需要剪掉的节点
				continue;
			}
			if (isIgnoreListContainRegion(ignoreList, newregion)) {
				continue;
			}
			pi = p.getPatternInterval(BitSetUtils.BitSet2Index(region));
			if (pi == null) {// 因为父节点中该window<a，没有保存的window，需要pruning
				ignoreList.add(newregion);// 保存需要剪掉的节点
				continue;
			}
			pitmp = pi.clone();
			posSup = getSupByWindow(pitmp.getPosOccurrence(), pos_last_element,
					posEI.getTimeMap(), newregions, true) / posSize;
			/** pruning 4 pattern区域剪枝 */
			if (results.getCount() == topk && posSup < results.getMin_cScore()) {
				ignoreList.add(newregion);// 保存需要剪掉的节点
				continue;
			}
			negSup = getSupByWindow(pitmp.getNegOccurrence(), neg_last_element,
					negEI.getTimeMap(), newregions, false) / negSize;
			if (negSup == 0) {
				pitmp.getNegOccurrence().clear();
			}
			pitmp.setPattern(p.getPattern());
			pitmp.setPosSup(posSup);
			pitmp.setNegSup(negSup);
			pitmp.setcRatio();
			pitmp.setRegions(newregion);
			index = BitSetUtils.BitSet2Index(newregion);
			newPatternIntervalArr[index] = pitmp;// 添加满足要求的pi
			piList.add(index);
			/**
			 * pruning 4 patternInterval
			 * 更小的区间的负列支持度大于b的时候，包含这个区间的大区间本轮可以不用计算了，但不删除该对象
			 */
			// if (pi.getNegSup() > results.getB()) {
			// pList.add(pi);
			// }
		}
		p.setPatternInterArr(newPatternIntervalArr);
		p.setPiIndex(piList);
		// 生成新p的上界
		p.setUpperboundBitSet(newUpperbound);
		return p;
	}

	/**
	 * @Title: checkInvalidWindow
	 * @Description: TODO
	 * @param newUpperbound
	 * @param clone
	 * @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	private boolean checkInvalidWindow(BitSet newUpperbound, BitSet clone) {
		clone.or(newUpperbound);
		clone.xor(newUpperbound);
		return clone.cardinality() >= 1 ? true : false;
	}

	/**
	 * @Title: isIgnoreMapContainRegion
	 * @Description: 验证是region中的所有区间都在ignoremap中，如果都在，这直接可以忽略改区间组合的计算
	 * @param ignoreMap
	 * @param regions
	 * @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	private boolean isIgnoreListContainRegion(List<BitSet> ignoreList,
			BitSet regions) {
		for (int i = 0; i < ignoreList.size(); i++) {
			BitSet tmp = ignoreList.get(i);
			BitSet tmpRegion = (BitSet) tmp.clone();
			tmpRegion.and(regions);
			if (tmpRegion.cardinality() == regions.cardinality()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Title: getSupByWindow
	 * @Description: 计算支持度，并且根据bitset的延长到其他window时，自动更新Pattern中相应window的bitset值
	 * @param occurrence
	 * @param last_element
	 *            最后的一个元素的bitset组信息
	 * @param pattern
	 *            对象
	 * @param pos
	 *            正列或负列
	 * @return int 支持度
	 * @throws
	 */
	private int getSupByWindow(HashMap<Integer, BitSet> occurrence,
			HashMap<Integer, BitSet> last_element,
			HashMap<Integer, List<Long>> timeMap, String regions, boolean pos) {
		if (last_element == null) {
			return 0;
		}
		Map<Integer, Integer[]> windowScope;
		if (pos) {
			windowScope = this.posWindow.getWindowScope().get(regions);
		} else {
			windowScope = this.negWindow.getWindowScope().get(regions);
		}
		BitSet temp;
		BitSet origin;
		List<Integer> removelist = new ArrayList<Integer>();// 需要remove的list
		for (Integer seqId : occurrence.keySet()) {
			Integer[] windex = windowScope.get(seqId);
			BitSet b = occurrence.get(seqId);
			BitSet element = last_element.get(seqId);
			List<Long> timelist = timeMap.get(seqId);
			if (element == null) {
				removelist.add(seqId);
				continue;
			}
			temp = (BitSet) b.clone();
			origin = (BitSet) b.clone();
			b.xor(b);
			boolean flag = true;
			do {
				flag = right_shift(origin, temp, minGap, maxGap, 1, timelist);
				b.or(temp);
			} while (flag);
			b.and(element);
			regionClear(b, windex);
			if (b.cardinality() == 0) {
				removelist.add(seqId);
				continue;
			}
		}
		for (Integer key : removelist) {// 删除不在window中bitset值
			occurrence.remove(key);
		}
		return occurrence.size();
	}

	/**
	 * 
	 * @Title: checkPattern
	 * @Description: 检查pattern在各个windows中的对比度
	 * @param p
	 * @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	private boolean checkPattern(Pattern p) {
		// if (p.getPattern().equals("B,I")) {
		// System.out.println();
		// System.out.println(p.getPatternMap().get("{2}").getNegOccurrence().get(14));
		// }
		ArrayList<PatternInterval> pList = new ArrayList<PatternInterval>();// 用于保存该pattern的所有windows中的对比度对象patterninterval
		PatternInterval pi = p.getUpperboundPI();
		// if(pi == null){
		// System.out.println();
		// }
		/** pruning rule 4 */
		if (pi.getPosSup() == 0 || results.getCount() == topk
				&& pi.getPosSup() < results.getMin_cScore()) {
			return false;
		}
		for (int i = 0; i < p.getPiIndex().size(); i++) {
			int index = p.getPiIndex().get(i);
			pi = p.getPatternInterArr()[index];
			if (pi.getPosSup() == 0 || pi.getcRatio() <= 0) {
				continue;
			}
			if (results.getCount() < topk || results.getCount() == topk
					&& pi.getcRatio() >= results.getMin_cScore()) {
				pList.add(pi);
			}
		}
		// if the length of plist is zero, then it state that in this iteration
		// we cannot find the appropriate pattern,
		// but the pattern can continuous increase.
		if (pList.size() == 0) {
			return true;
		}
		/** only add pattern p with the most contrast regions **/
		PatternListUtil.sortPatternIntervalList(pList);
		results.addPattern(pList.get(0));
		return true;
	}

	/**
	 * 
	 * @Title: support_Region_Count
	 * @Description: TODO
	 * @param p
	 * @param posEI
	 * @param negEI
	 * @param minGap
	 * @param maxGap
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void support_Region_Count(PatternInterval pi) {
		/** get the elements from ElementsInfo according the eList **/

		/** compute the support information in the region condition **/
		double posSup = pi.getPosOccurrence().size();
		double negSup = pi.getNegOccurrence().size();
		pi.setPosSup(posSup / posSize);
		pi.setNegSup(negSup / negSize);
		pi.setcRatio();
	}

	/**
	 * 
	 * @Title: right_shift
	 * @Description: 右移操作
	 * @param b
	 *            右移的BitSet
	 * @param num
	 *            右移步数
	 * @return void 返回类型
	 * @throws
	 */
	public boolean right_shift(BitSet origin, BitSet b, long minGap,
			long maxGap, int num, List<Long> timelist) {
		int count = b.cardinality();
		int length = b.length();
		int index = length - 1;
		int indexOfOrigin = origin.length();
		for (int i = count; i > 0; i--) {
			indexOfOrigin = origin.previousSetBit(indexOfOrigin - 1);
			index = b.previousSetBit(index);
			long mintime = timelist.get(indexOfOrigin);// 获取当前位置的时间
			long maxtime = 0;
			if (index + num < timelist.size()) {
				maxtime = timelist.get(index + num);// 获取要移动到新位置的时间
			}
			long gap = maxtime - mintime;
			if (minGap <= gap && gap <= maxGap) {
				b.set(index + num);
			} else {
				origin.clear(indexOfOrigin);// 清除原始数据位，为了保持与temp移位的一致性
			}
			b.clear(index);
		}
		return b.cardinality() > 0;
	}

	public Results getResults() {
		return results;
	}

}
