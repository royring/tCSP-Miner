package cn.edu.scu.dke.idsp.vo;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: Pattern
 * @Description: TODO
 * @author yanli
 * @date 2015��9��24�� ����9:46:07
 * 
 */
public class Pattern {
	/** ����sequence�ĶԱȶ� */
	private double cRatio;
	/** ����sequence������֧�ֶ� */
	private double posSup;
	/** ����sequence�ĸ���֧�ֶ� */
	private double negSup;
	/** ����sequence��ģʽ�ַ��� */
	private String pattern;
	/** ģʽ���ֵ����� */
	private BitSet regions;
	/** window��window��ϵ�ģʽ���� */
	private PatternInterval[] patternInterArr;

	private List<Integer> piIndex = new ArrayList<Integer>();
	/**
	 * ��¼���п��ܵ�patternInterval��bitset�Ĳ���
	 */
	private BitSet upperboundBitSet = new BitSet();
	private PatternInterval upperboundPI = new PatternInterval();
	
	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */
	private Pattern() {
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */
	public Pattern(int l) {
		int size = (int) (Math.pow(2, l));
		patternInterArr = new PatternInterval[size];
	}

	public void print() {
		System.out.println("Pattern CR:" + cRatio + " value:" + pattern
				+ " pos:" + posSup + " neg:" + negSup);
		// for (PatternInterval patternInterval : patternlist) {
		// patternInterval.print();

		// }
	}

	@Override
	public Pattern clone() {
		Pattern clone = new Pattern();
		clone.pattern = this.pattern;
		clone.cRatio = this.cRatio;
		clone.posSup = this.posSup;
		clone.negSup = this.negSup;
		clone.regions = (BitSet) this.regions.clone();
		PatternInterval[] tmpArr = new  PatternInterval[this.patternInterArr.length];
		PatternInterval pi = null;
		for (int i = 0; i < patternInterArr.length; i++) {
			pi = patternInterArr[i];
			if (pi!=null) {
				tmpArr[i] = pi;
			}
		}
		clone.setPatternInterArr(tmpArr);
		clone.upperboundBitSet = (BitSet) this.upperboundBitSet.clone();
		clone.upperboundPI = this.upperboundPI.clone();
		return clone;
	}

	public void addPatternInterval(int index, PatternInterval pi) {
		this.piIndex.add(index);
		this.patternInterArr[index] = pi;
	}
	public PatternInterval getPatternInterval(int index) {
		return this.patternInterArr[index];
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public double getcRatio() {
		return cRatio;
	}

	public void setcRatio(double cRatio) {
		this.cRatio = cRatio;
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

	public void setUpperboundBitSet(int index) {
		upperboundBitSet.set(index);
	}

	public BitSet getUpperboundBitSet() {
		return upperboundBitSet;
	}

	public void setUpperboundBitSet(BitSet upperboundBitSet) {
		this.upperboundBitSet = upperboundBitSet;
	}

	public PatternInterval[] getPatternInterArr() {
		return patternInterArr;
	}

	public void setPatternInterArr(PatternInterval[] patternInterArr) {
		this.patternInterArr = patternInterArr;
	}

	public List<Integer> getPiIndex() {
		return piIndex;
	}

	public void setPiIndex(List<Integer> piIndex) {
		this.piIndex = piIndex;
	}

	public PatternInterval getUpperboundPI() {
		return upperboundPI;
	}

	public void setUpperboundPI(PatternInterval upperboundPI) {
		this.upperboundPI = upperboundPI;
	}

}
