package cn.edu.scu.dke.idsp.vo;

import java.util.ArrayList;

import cn.edu.scu.dke.idsp.util.PatternListUtil;

/**
 * @ClassName: Result
 * @Description: TODO
 * @author yanli
 * @date 2015/9/6/ 22:11:32
 * 
 */

public class Results {
	/** Result list */
	private ArrayList<PatternInterval> pList;
	/** top-k */
	private int topk;
	/** minimal cScore of result list*/
	private double min_cScore = 0;
	public double avgCRatio;//the bigger the better
	public double avgPatternCount;//the bigger the better
	public double avgWindowSize;//the smaller the better
	public double avgPatternSize;//the bigger the better
	
	public Integer count=0;

	public Results(int topk) {
		this.pList = new ArrayList<PatternInterval>();
		this.topk = topk;
	}

	public boolean addPattern(PatternInterval p) {
		if (count < topk) {
			this.pList.add(p);
			count++;
			PatternListUtil.sortResultPatternIntervalList(pList);
			min_cScore = pList.get(pList.size()-1).getcRatio();
		}else {
			pList.add(p);
			PatternListUtil.sortResultPatternIntervalList(pList);
			pList.remove(topk);
			min_cScore = pList.get(topk-1).getcRatio();
		}
		return true;
	}

	public ArrayList<PatternInterval> getpList() {
		return pList;
	}

	public int getSize() {
		return pList.size();
	}
	/** 
	 * @Title: statistics 
	 * @Description: generate statistical information
	 * @return void   
	 * @throws 
	 */ 
	public void statistics() {
		int amount = pList.size();
		double amountCRatio = 0;
		double amountWindowSize = 0;
		double amountPatternSize = 0;
		for (int i = 0; i < pList.size(); i++) {
			PatternInterval pi = pList.get(i);
			amountCRatio+=pi.getcRatio();
			amountWindowSize+=pi.getRegions().toString().split(",").length;
			amountPatternSize+=pi.getPattern().length();
		}
		if (amount == 0) {
			avgCRatio = 0;
			avgPatternCount = amount;
			avgWindowSize = 0;
			avgPatternSize = 0;
		}else {
			avgCRatio = amountCRatio/amount;
			avgPatternCount = amount;
			avgWindowSize = amountWindowSize/amount;
			avgPatternSize = amountPatternSize/amount;
		}
		
		
	}

	@Override
	public String toString() {
		return "Results [topk=" + topk + ", avgCRatio=" + avgCRatio
				+ ", avgPatternCount=" + avgPatternCount + ", avgWindowSize="
				+ avgWindowSize + ", avgPatternSize=" + avgPatternSize + "]";
	}

	public int getTopk() {
		return topk;
	}

	public void setTopk(int topk) {
		this.topk = topk;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public double getMin_cScore() {
		return min_cScore;
	}

	public void setMin_cScore(double min_cScore) {
		this.min_cScore = min_cScore;
	}
}
