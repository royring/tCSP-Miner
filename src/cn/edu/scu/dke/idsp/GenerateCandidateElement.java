package cn.edu.scu.dke.idsp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.edu.scu.dke.idsp.util.DateUtil;
import cn.edu.scu.dke.idsp.util.SubBitSetList;
import cn.edu.scu.dke.idsp.vo.Element;
import cn.edu.scu.dke.idsp.vo.ElementsInfo;
import cn.edu.scu.dke.idsp.vo.Window;

public enum GenerateCandidateElement {
	INSTANCE;
	public void genCE2(List<String> posData, List<String> negData,
			ElementsInfo posEI, ElementsInfo negEI, int l) {
		long minTime = Long.MAX_VALUE;// �������ݼ�����С��ʱ��
		long maxTime = Long.MIN_VALUE;// �������ݼ�������ʱ��
		long interval = 0; // ���������������ʱ������
		long remainder = 0; // ����
		long[][] timelist = new long[l][2];// ʱ�����仮��Сʱlong����������ÿ��Ŀ�ʼʱ�䣬�����������¼�
		HashMap<String, Element> pos_eMap = new HashMap<String, Element>();
		HashMap<Integer, List<Long>> pos_timeMap = new HashMap<Integer, List<Long>>();
		HashMap<String, Element> neg_eMap = new HashMap<String, Element>();
		HashMap<Integer, List<Long>> neg_timeMap = new HashMap<Integer, List<Long>>();
		List<Integer> pos_lenList = new ArrayList<Integer>();
		List<Integer> neg_lenList = new ArrayList<Integer>();
		List<String> posdataSeqList = new ArrayList<String>();
		List<String> negdataSeqList = new ArrayList<String>();
		// ʱ�����仮��
		for (String seq : posData) {
			if (!seq.trim().equals("")) {
				posdataSeqList.add(seq.trim());
				String[] tempArrChar = seq.trim().split(",");
				long min = Long.valueOf(tempArrChar[0].split(":")[1]);
				long max = Long.valueOf(tempArrChar[tempArrChar.length - 1]
						.split(":")[1]);
				min = DateUtil.date2timeFromZero(min);
				max = DateUtil.date2timeFromZero(max);
				if (minTime > min) {
					minTime = min;
				}
				if (maxTime < max) {
					maxTime = max;
				}
			}
		}
		for (String seq : negData) {
			if (!seq.trim().equals("")) {
				negdataSeqList.add(seq.trim());
				String[] tempArrChar = seq.trim().split(",");
				long min = Long.valueOf(tempArrChar[0].split(":")[1]);
				long max = Long.valueOf(tempArrChar[tempArrChar.length - 1]
						.split(":")[1]);
				min = DateUtil.date2timeFromZero(min);
				max = DateUtil.date2timeFromZero(max);
				if (minTime > min) {
					minTime = min;
				}
				if (maxTime < max) {
					maxTime = max;
				}
			}
		}
		interval = (maxTime - minTime + 1) / l;
		remainder = (maxTime - minTime + 1) % l;
		remainder = l - remainder;
		timelist[0][0] = minTime;
		timelist[0][1] = minTime + interval - 1;
		for (int i = 1; i < l; i++) {
			if (i < remainder) {
				timelist[i][0] = timelist[i - 1][1] + 1;
				timelist[i][1] = timelist[i][0] + interval - 1;
			} else {
				timelist[i][0] = timelist[i - 1][1] + 1;
				timelist[i][1] = timelist[i][0] + interval;
			}
		}

		// ******************************����**********************************
		Window poswindow = GenerateWindows(posdataSeqList, timelist, l);
		/** collect the position occurrence information **/
		for (int seqId = 0; seqId < posdataSeqList.size(); seqId++) {
			String tmpStr = posdataSeqList.get(seqId);
			String[] tempArrChar = tmpStr.split(",");
			List<Long> timelonglist = new ArrayList<>();
			for (int position = 0; position < tempArrChar.length; position++) {
				String value = tempArrChar[position];
				String[] values = value.split(":");
				Element e = pos_eMap.get(values[0]);
				if (e == null) {
					e = new Element(values[0]);
					pos_eMap.put(values[0], e);
				}
				timelonglist.add(Long.valueOf(values[1]));
				e.addPOccurrence(seqId, position);
				// e.addSubintOcur2element(seqId, position, poswindow);

				e.setIntervalsBitSet(seqId, position, poswindow);
			}
			pos_timeMap.put(seqId, timelonglist);
			/** collect the length information **/
			pos_lenList.add(tempArrChar.length);
		}

		posEI.seteMap(pos_eMap);
		posEI.setLenList(pos_lenList);
		posEI.setTimeMap(pos_timeMap);
		posEI.setWindow(poswindow);

		// ******************************����**********************************
		Window negwindow = GenerateWindows(negdataSeqList, timelist, l);
		/** collect the negition occurrence information **/
		// If an event e just happens at D-, then the event e can be removed from E.
		// So we use the positive event to initialize the neg_map, and later, we remove the unvalued event.
		for (String character : posEI.geteMap().keySet()) { 
			neg_eMap.put(character, new Element(character));
		}
		for (int seqId = 0; seqId < negdataSeqList.size(); seqId++) {
			String tmpStr = negdataSeqList.get(seqId);
			String[] tempArrChar = tmpStr.split(",");
			List<Long> timelonglist = new ArrayList<>();
			for (int position = 0; position < tempArrChar.length; position++) {
				String value = tempArrChar[position];
				String[] values = value.split(":");
				Element e = neg_eMap.get(values[0]);
				if (e == null) {// pruning rule 1
					continue;
				}
				timelonglist.add(Long.valueOf(values[1]));
				e.addPOccurrence(seqId, position);
				// e.addSubintOcur2element(seqId, position, negwindow);
				e.setIntervalsBitSet(seqId, position, negwindow);
			}
			neg_timeMap.put(seqId, timelonglist);
			/** collect the length information **/
			neg_lenList.add(tempArrChar.length);
		}
		//remove the unvalued event
		 Iterator<Entry<String, Element>> it =neg_eMap.entrySet().iterator();  
		while (it.hasNext()) {
			Entry<String, Element> entry = it.next();
			Element e = entry.getValue();
			if (e.getpOccurrence().size() == 0 ) {
				it.remove();
			}
		}
		negEI.seteMap(neg_eMap);
		negEI.setLenList(neg_lenList);
		negEI.setTimeMap(neg_timeMap);
		negEI.setWindow(negwindow);
	}

	/**
	 * @Title: GenerateWindows
	 * @Description: ����window��ʼλ����Ϣ
	 * @param posLenList
	 *            ���������г���
	 * @param negLenList
	 *            ���������г���
	 * @param l
	 *            window�ĳ���
	 * @return Window ��������
	 * @throws
	 */
	public Window GenerateWindows(List<String> dataSeqList, long[][] timearr,
			int l) {
		Window window = new Window();
		window.setL(l);
		BitSet region = new BitSet(l);
		region.set(0, l);
		window.setRegion(region);
		ArrayList<BitSet> regionsList = SubBitSetList.INSTANCE.getSub(region);
		for (BitSet bitSet : regionsList) {
			window.getWindows().add(bitSet);
			// if (BitSetUtils.checkContinue(bitSet)) {
			// window.getContinueWindows().add(bitSet);
			// }
			if (bitSet.cardinality() == 1) {
				window.getWindowlist().add(bitSet);
			}
		}
		window.sortWindowlist();
		window.sortWindows();
		// window.sortContinueWindows();
		computeWindowScope(dataSeqList, timearr, l, window);// �����������window��ʼ��Ϣ
		return window;
	};

	/**
	 * @Title: computeWindowScope
	 * @Description: ����sequence
	 *               list��sequence�ĳ��ȣ���window�������Ȼ���sequence���ȣ���ȡwindow��window��ϵ���ʼλ��
	 * @param lenList
	 *            ������ĳ����б�
	 * @param l
	 *            window������
	 * @param window
	 *            ������Ϣ��window����
	 * @param pos
	 *            ���л���
	 * @return void ��������
	 * @throws
	 */
	private void computeWindowScope(List<String> dataSeqList, long[][] timearr,
			int l, Window window) {
		Map<String, Map<Integer, Integer[]>> windowScope;
		windowScope = window.getWindowScope();
		for (int i = 0; i < dataSeqList.size(); i++) {
			String seq = dataSeqList.get(i);
			String[] tempArrChar = seq.split(",");
			long[][] curTimeArr = DateUtil.time2DateTimeIntervals(timearr,
					Long.valueOf(tempArrChar[0].split(":")[1]));
			// ���window�������Ϣ����window��window�������ʼλ�ü���������浽window������
			for (BitSet bitSet : window.getWindows()) {
				int cardinality = bitSet.cardinality();
				if (cardinality == 1) {// ����window����ʼλ����Ϣ
					int windex = bitSet.nextSetBit(0);
					int countOfInterval = 0;
					Integer[] scope = { -1, -1 };
					long startTime = curTimeArr[windex][0];
					long endTime = curTimeArr[windex][1];
					int j = 0;
					for (; j < tempArrChar.length; j++) {
						String[] value = tempArrChar[j].split(":");
						long tmpTime = Long.valueOf(value[1]);
						if (startTime <= tmpTime && tmpTime <= endTime) {
							if (countOfInterval == 0) {
								scope[0] = j;
							}
							countOfInterval++;
						} else {
							if (countOfInterval > 0) {
								scope[1] = j - 1;
								break;
							}
						}
					}
					if (countOfInterval > 0 && scope[1] == -1) {
						scope[1] = j - 1;
					}
					Map<Integer, Integer[]> map = windowScope.get(bitSet
							.toString());
					if (map == null) {
						map = new HashMap<Integer, Integer[]>();
						windowScope.put(bitSet.toString(), map);
					}
					map.put(i, scope);
				} else if (cardinality > 1) {// window��ϵ���ʼλ����Ϣ
					// �ϲ�window��ϵ���Ϣ
					List<Integer> indexlist = new ArrayList<Integer>();// ����window�����Ϣ�ϲ������ʼλ�ã��ԳɶԷ�ʽ����list��
					List<Long> timeIndexlist = new ArrayList<Long>();// ����window�����Ϣ�ϲ������ʼλ��ʱ����Ϣ
					int windex = bitSet.nextSetBit(0);
					int preWindex = bitSet.nextSetBit(0);
					long startTime = curTimeArr[windex][0];
					long endTime = curTimeArr[windex][1];
					timeIndexlist.add(startTime);
					for (int j = 1; j < cardinality; j++) {
						windex = bitSet.nextSetBit(windex + 1);
						if (windex - preWindex == 1) {
							preWindex = windex;
							startTime = curTimeArr[windex][0];
							endTime = curTimeArr[windex][1];
						} else {
							timeIndexlist.add(endTime);
							startTime = curTimeArr[windex][0];
							endTime = curTimeArr[windex][1];
							timeIndexlist.add(startTime);
							preWindex = windex;
						}
					}
					timeIndexlist.add(endTime);
					for (int x = 0; x < timeIndexlist.size(); x++) {
						long start = timeIndexlist.get(x);
						long end = timeIndexlist.get(++x);
						int count = 0;
						int z = 0;
						for (; z < tempArrChar.length; z++) {
							String[] value = tempArrChar[z].split(":");
							long tmpTime = Long.valueOf(value[1]);
							if (start <= tmpTime && tmpTime <= end) {
								if (count == 0) {
									indexlist.add(z);
								}
								count++;
							} else {
								if (count > 0) {
									indexlist.add(z - 1);
									break;
								}
							}
						}
						if (count > 0 && indexlist.size() % 2 != 0) {
							indexlist.add(z - 1);
						}
						if (count == 0) {
							indexlist.add(-1);
							indexlist.add(-1);
						}
					}

					Integer[] scope = (Integer[]) indexlist
							.toArray(new Integer[indexlist.size()]);
					Map<Integer, Integer[]> map = windowScope.get(bitSet
							.toString());
					if (map == null) {
						map = new HashMap<Integer, Integer[]>();
						windowScope.put(bitSet.toString(), map);
					}
					map.put(i, scope);
				}
			}
		}
	}
}
