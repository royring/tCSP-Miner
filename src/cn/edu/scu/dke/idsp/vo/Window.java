package cn.edu.scu.dke.idsp.vo;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: Window
 * @Description: TODO
 * @author yanli
 * @date 2015��9��29�� ����3:28:33
 * 
 */
public class Window {
	/** num. of window */
	private int l;
	/** window��region��Ϣ */
	private BitSet region;
	/** window��window��ϵ���Ϣ */
	private List<BitSet> windows = new ArrayList<BitSet>();
	/** window����Ϣ */
	private List<BitSet> windowlist = new ArrayList<BitSet>();
	/** window��window��ϵ���ʼλ����Ϣ��map��keyֵ�������window ��bitset��String��Ϣ */
	private Map<String, Map<Integer, Integer[]>> windowScope;

	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */ 
	public Window() {
		super();
		windowScope = new HashMap<String, Map<Integer,Integer[]>>();
	}

	public void print() {
//		for (BitSet bitSet : windows) {
//			System.out.println(bitSet);
//		}
		for (Map.Entry<String, Map<Integer,Integer[]>> entry : windowScope.entrySet()) {
			if (entry.getKey().equals("{7}") ||entry.getKey().equals("{9}") || entry.getKey().equals("{8}")) {
				System.out.print(entry.getKey()+"::::");
				for (Map.Entry<Integer, Integer[]> entrysub : entry.getValue().entrySet()) {
					Integer [] values =  entrysub.getValue();
					System.out.print("Key = " + entrysub.getKey()+":");
					for (Integer integer : values) {
						System.out.print(integer+",");
					}
					System.out.println();
				}
			}
		}
//		for (Map.Entry<String, Map<Integer,Integer[]>> entry : negWindowScope.entrySet()) {
//			System.out.print(entry.getKey()+"::::");
//			for (Map.Entry<Integer, Integer[]> entrysub : entry.getValue().entrySet()) {
//				Integer [] values =  entrysub.getValue();
//				 System.out.print("Key = " + entrysub.getKey()+":");
//				 for (Integer integer : values) {
//					 System.out.print(integer+",");
//				}
//				 System.out.println();
//			}
//		}
	}
	
	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public List<BitSet> getWindows() {
		return windows;
	}

	public void setWindows(List<BitSet> windows) {
		this.windows = windows;
	}

	public BitSet getRegion() {
		return region;
	}

	public void setRegion(BitSet region) {
		this.region = region;
	}

	public List<BitSet> getWindowlist() {
		return windowlist;
	}

	public void setWindowlist(List<BitSet> windowlist) {
		this.windowlist = windowlist;
	}

	/** 
	 * @Title: sortWindowlist 
	 * @Description: �Ӵ�С
	 * @return void    �������� 
	 * @throws 
	 */ 
	public void sortWindows() {
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
	 * @Description: ���� ����false
	 * @param tmp
	 * @param tmp1
	 * @return    �趨�ļ� 
	 * @return boolean    �������� 
	 * @throws 
	 */ 
	private boolean compare(BitSet tmp, BitSet tmp1) {
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

	/** 
	 * @Title: compare 
	 * @Description:  ��С���� 
	 * �������������򣬲�ͬ�����д������������ tmp < tmp1 ����true
	 * @param tmp
	 * @param tmp1
	 * @return    �趨�ļ� 
	 * @return boolean    �������� 
	 * @throws 
	 */ 
	private boolean compare2(BitSet tmp, BitSet tmp1) {
		String tmpstr = tmp.toString();
		String tmpstr1 = tmp1.toString();
		int tmpint = Integer.valueOf(tmpstr.substring(1,tmpstr.length()-1));
		int tmpint1 = Integer.valueOf(tmpstr1.toString().substring(1,tmpstr1.length()-1));
		return tmpint > tmpint1;
	}
	/** 
	 * @Title: sortWindowlist 
	 * @Description: ��С����
	 * @return void    �������� 
	 * @throws 
	 */ 
	public void sortWindowlist() {
		for (int i = 1; i < windowlist.size(); i++) {
			BitSet tmp = windowlist.get(i);
			int j = i-1;
			for (; j >=0 ; j--) {
				BitSet tmp1 = windowlist.get(j);
				if (compare2(tmp,tmp1)) {
					break;
				}else {
					windowlist.set(j+1, tmp1);
				}
			}
			windowlist.set(j+1, tmp);
		}
	}
	public Map<String, Map<Integer, Integer[]>> getWindowScope() {
		return windowScope;
	}

	public void setWindowScope(Map<String, Map<Integer, Integer[]>> windowScope) {
		this.windowScope = windowScope;
	}

}
