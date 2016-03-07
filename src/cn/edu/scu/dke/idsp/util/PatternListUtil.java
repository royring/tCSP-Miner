package cn.edu.scu.dke.idsp.util;

import java.util.List;

import cn.edu.scu.dke.idsp.vo.PatternInterval;

/**
 * @ClassName: PatternListUtil
 * @Description: TODO
 * @author yanli
 * @date 2016年3月7日 下午4:16:25
 *
 */

public class PatternListUtil {
	/**
	 * @Title: sortPatternIntervalList
	 * @Description: insertion sort, sort the pattern list as the rule on paper
	 *               with Definition 2.
	 * @return
	 * @throws
	 */
	public static void sortPatternIntervalList(List<PatternInterval> pList) {
		for (int i = 1; i < pList.size(); i++) {
			PatternInterval cur = pList.get(i);
			int j = i - 1;
			for (; j >= 0; j--) {
				PatternInterval pre = pList.get(j);
				if (compare(cur, pre)) {
					pList.set(j + 1, pre);
				}else {
					break;
				}  
			}
			pList.set(j + 1, cur);
		}
	}

	/**
	 * @Title: sortResultPatternIntervalList
	 * @Description: insertion sort, sort the patter list as the rule on paper.
	 * @return
	 * @throws
	 */
	public static void sortResultPatternIntervalList(List<PatternInterval> pList) {
		for (int i = 1; i < pList.size(); i++) {
			PatternInterval cur = pList.get(i);
			int j = i - 1;
			for (; j >= 0; j--) {
				PatternInterval pre = pList.get(j);
				if (compareResult(cur, pre)) {
					pList.set(j + 1, pre);
				}else {
					break;
				} 
			}
			pList.set(j + 1, cur);
		}
	}
	

	/**
	 * @Title: compare
	 * @Description: TODO
	 * @param tmp
	 * @param tmp1
	 * @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean compare(PatternInterval cur, PatternInterval pre) {
		String str1 = cur.getRegions().toString();
		String str2 = pre.getRegions().toString();
		String[] str1s = str1.split(",");
		String[] str2s = str2.split(",");
		if (cur.getcRatio() > pre.getcRatio()) {
			return true;
		}
		if (cur.getcRatio() < pre.getcRatio()) {
			return false;
		}
		if (str1s.length < str2s.length) {
			return true;
		}
		if (str1s.length > str2s.length) {
			return false;
		}
		return str1.compareTo(str2)<0?true:false;
	}
	
	/** 
	 * @Title: compareResult 
	 * @Description: TODO 
	 * @param cur
	 * @param pre
	 * @return    设定文件 
	 * @return boolean    返回类型 
	 * @throws 
	 */ 
	private static boolean compareResult(PatternInterval cur,
			PatternInterval pre) {
		String str1 = cur.getPattern();
		String str2 = pre.getPattern();
		String[] str1s = str1.split(",");
		String[] str2s = str2.split(",");
		if (cur.getcRatio() > pre.getcRatio()) {
			return true;
		}
		if (cur.getcRatio() < pre.getcRatio()) {
			return false;
		}
		if (str1s.length < str2s.length) {
			return true;
		}
		if (str1s.length > str2s.length) {
			return false;
		}
		return str1.compareTo(str2)<0?true:false;
	}
}
