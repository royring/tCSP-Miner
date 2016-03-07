/**   
* @Title: Pattern.java 
* @Package cn.edu.scu.dke.vo 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author A18ccms A18ccms_gmail_com   
* @date 2015��9��6�� ����4:20:18 
* @version V1.0   
*/
package cn.edu.scu.dke.idsp.vo;

import java.util.BitSet;
import java.util.HashMap;


/** 
 * @ClassName: Pattern 
 * @Description: TODO
 * @author yanli
 * @date 2015��9��6�� ����4:20:18 
 *  
 */
public class PatternInterval {
	/** �Աȶ�*/
	private double cRatio;
	/** ����֧�ֶ�*/
	private double posSup;
	/** ����֧�ֶ�*/
	private double negSup;
	/** ģʽ�ַ���*/
	private String pattern;
	/** ģʽ���ֵ�����*/
	private BitSet regions;
	/** ģʽ�����е�֧�ֶ�*/
	private HashMap<Integer, BitSet> posOccurrence;
	/** ģʽ�����е�֧�ֶ�*/
	private HashMap<Integer, BitSet> negOccurrence;
	
	public PatternInterval() {
		this.cRatio = 0;
		this.posSup = 0;
		this.negSup = 0;
		this.posOccurrence = new HashMap<Integer, BitSet>();
		this.negOccurrence = new HashMap<Integer, BitSet>();
	}
	
	public PatternInterval(String e, int interval) {
		this.cRatio = 0;
		this.posSup = 0;
		this.negSup = 0;
		this.regions = new BitSet();
		this.posOccurrence = new HashMap<Integer, BitSet>();
		this.negOccurrence = new HashMap<Integer, BitSet>();
	}
	
	@Override
	public PatternInterval clone() {
		PatternInterval clone = new PatternInterval();
		clone.pattern = this.pattern;
		clone.cRatio = this.cRatio;
		clone.posSup = this.posSup;
		clone.negSup = this.negSup;
		clone.regions = (BitSet)this.regions.clone();
		for (Integer key : posOccurrence.keySet()) {
			clone.posOccurrence.put(key, (BitSet)posOccurrence.get(key).clone());
		}
		for (Integer key : negOccurrence.keySet()) {
			clone.negOccurrence.put(key, (BitSet)negOccurrence.get(key).clone());
		}
		return clone;
	}
	
	public String print() {
		String[] s =pattern.split(",");
		String tmp = "[";
		for (int i = 0; i < s.length; i++) {
			tmp+=s[i]+", ";
		}
		tmp = tmp.substring(0, tmp.length()-2)+"]";
		String result = "PatternInterval CR:"+cRatio+" value:"+tmp+" regions:"+regions
				+" pos:"+posSup+" neg:"+negSup;
		System.out.println(result);
		return result;
//		for (Integer key : posOccurrence.keySet()) {
//			System.out.println(key+":"+posOccurrence.get(key).toString());
//		}
//		for (Integer key : negOccurrence.keySet()) {
//			System.out.println(key+":"+negOccurrence.get(key).toString());
//		}
	}
	public void print2() {
		System.out.println("PatternInterval CR:"+cRatio+" value:"+pattern+" regions:"+regions
				+" pos:"+posSup+" neg:"+negSup);
		for (Integer key : posOccurrence.keySet()) {
			System.out.println(key+":"+posOccurrence.get(key).toString());
		}
//		for (Integer key : negOccurrence.keySet()) {
//			System.out.println(key+":"+negOccurrence.get(key).toString());
//		}
	}
	
	public double getcRatio() {
		return cRatio;
	}

	public void setcRatio() {
		this.cRatio = posSup - negSup;
	}

	public double getPosSup() {
		return posSup;
	}

	public void setPosSup(double posSup) {
		this.posSup = posSup;
	}

	public double getNegSup() {
		return negSup;
	}

	public void setNegSup(double negSup) {
		this.negSup = negSup;
	}

	public BitSet getRegions() {
		return regions;
	}

	public void setRegions(BitSet regions) {
		this.regions = regions;
	}

	public HashMap<Integer, BitSet> getPosOccurrence() {
		return posOccurrence;
	}

	public void setPosOccurrence(HashMap<Integer, BitSet> posOccurrence) {
		this.posOccurrence = posOccurrence;
	}

	public HashMap<Integer, BitSet> getNegOccurrence() {
		return negOccurrence;
	}

	public void setNegOccurrence(HashMap<Integer, BitSet> negOccurrence) {
		this.negOccurrence = negOccurrence;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

}
