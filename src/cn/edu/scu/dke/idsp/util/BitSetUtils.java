package cn.edu.scu.dke.idsp.util;

import java.util.BitSet;
import java.util.List;
import java.util.Queue;

/** 
 * @ClassName: BitSet_StringUtils 
 * @Description: TODO
 * @author yanli
 * @date 2015年10月7日 下午1:06:00 
 *  
 */
public class BitSetUtils {
	public static BitSet String2BitSet(String regions){
		if (regions ==null) {
			return null;
		}
		BitSet bs = new BitSet();
		regions = regions.substring(1, regions.length()-1);
		String[] as = regions.split(",");
		for (String string : as) {
			string = string.trim();
			if (!string.isEmpty()) {
				Integer tmp =  Integer.valueOf(string.trim());
				bs.set(tmp);
			}
		}
		return bs;
	}
	/** 
	 * @Title: sortWindowlist 
	 * @Description: 从大到小
	 * @return void    返回类型 
	 * @throws 
	 */ 
	public static void sortBitSetList(List<BitSet> windows) {
		for (int i = 1; i < windows.size(); i++) {
			BitSet tmp = windows.get(i);
			int j = i-1;
			for (; j >=0 ; j--) {
				BitSet tmp1 = windows.get(j);
				if (compare(tmp,tmp1)) {
					break;
				}else {
					windows.set(j+1, tmp1);
				}
			}
			windows.set(j+1, tmp);
		}
	}

	/** 
	 * @Title: compare 
	 * @Description: TODO 
	 * @param tmp
	 * @param tmp1
	 * @return    设定文件 
	 * @return boolean    返回类型 
	 * @throws 
	 */ 
	private static boolean compare(BitSet tmp, BitSet tmp1) {
		String str1 = tmp.toString();
		String str2 = tmp1.toString();
		String[] str1s = str1.split(",");
		String[] str2s = str2.split(",");
		if (str1s.length>str2s.length) {
			return false;
		}
		if (str1s.length<str2s.length) {
			return true;
		}
		if (str1s.length==str2s.length) {
			return str1.compareTo(str2) >0?false:true;
		}
		return false;
	}
	
	public static int BitSet2Index(BitSet bs){
		int index = 0;
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
		     index += Math.pow(2, i);
		 }
		return index;
	}
	/** 
	 * @Title: checkContinue 
	 * @Description: 判断窗口是否连续
	 * @param bitSet
	 * @return    设定文件 
	 * @return boolean    返回类型 
	 * @throws 
	 */ 
	public static boolean checkContinue(BitSet bs) {
		int cur = bs.nextSetBit(0);
		int pre = cur;
		for (cur = bs.nextSetBit(cur+1); cur >= 0; cur = bs.nextSetBit(cur+1)) {
			if (cur-pre >1) {
				return false;
			}
			pre = cur;
		 }
		return true;
	}
	/** 
	 * @Title: obtainContinuousTimeInterval 
	 * @Description: TODO 
	 * @param queue
	 * @param upperbound    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */ 
	public static void obtainContinuousTimeInterval(Queue<BitSet> queue,
			BitSet upperbound) {
		if (upperbound == null) return;
		int cur = upperbound.nextSetBit(0);
		int pre = cur;
		int start = cur;
		for (cur = upperbound.nextSetBit(cur+1); cur >= 0; cur = upperbound.nextSetBit(cur+1)) {
			if (cur-pre >1) {
				BitSet bs = new BitSet();
				bs.set(start, pre+1);
				queue.add(bs);
				start = cur;
			}
			pre = cur;
		 }
		BitSet bs = new BitSet();
		bs.set(start, pre+1);
		queue.add(bs);
	}
	/** 
	 * @Title: queueContinuousTimeInterval 
	 * @Description: TODO 
	 * @param queue
	 * @param region     
	 * @return void     
	 * @throws 
	 */ 
	public static void queueContinuousTimeInterval(Queue<BitSet> queue,
			BitSet region) {
		if (region == null) return;
		if (region.cardinality() > 1) {
			BitSet startBitSet = (BitSet)region.clone();
			int startIndex = startBitSet.nextSetBit(0);
			startBitSet.set(startIndex,false);
			queue.add(startBitSet);
			BitSet endBitSet = (BitSet)region.clone();
			int endIndex = endBitSet.previousSetBit(region.length());
			endBitSet.set(endIndex,false);
			queue.add(endBitSet);
		}
	}
}
