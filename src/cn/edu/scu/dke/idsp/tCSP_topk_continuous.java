/**   
 * @Title: IDSP.java 
 * @Package cn.edu.scu.dke.idsp 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author A18ccms A18ccms_gmail_com   
 * @date 2015��9��6�� ����10:16:09 
 * @version V1.0   
 */
package cn.edu.scu.dke.idsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cn.edu.scu.dke.idsp.util.FileUtils;
import cn.edu.scu.dke.idsp.vo.ElementsInfo;
import cn.edu.scu.dke.idsp.vo.PatternInterval;
import cn.edu.scu.dke.idsp.vo.Results;

/**
 * @ClassName: IDSP
 * @Description: IDSP�ھ��㷨
 * @author yanli
 * @date 2015��9��6�� ����10:16:09
 * 
 */
public class tCSP_topk_continuous {

	private int l = 3; // �ȳ������仮�ֿ���
	private int topk = 5;
	private long minGap = 0;// gap��ʼλ��
	private long maxGap = 600;// gap����λ��:��ʱ��Ϊ��λ����
	private String posPath = "./data/DNA/1_posCbi.posCbi";
	private String negPath = "./data/DNA/1_negCbi.negCbi";
	private Results results;// �����

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param l
	 * @param k
	 * @param minGap
	 * @param maxGap
	 * @param pos
	 * @param neg
	 */
	public tCSP_topk_continuous(int l, int topk, int minGap, int maxGap,
			String pos, String neg) {
		super();
		this.l = l;
		this.topk = topk;
		this.minGap = minGap;
		this.maxGap = maxGap;
		this.posPath = pos;
		this.negPath = neg;
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */
	public tCSP_topk_continuous() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @Title: run
	 * @Description: ִ���ھ��㷨
	 * @param
	 * @return Results ��������
	 * @throws
	 */
	public Results run(List<String> posData, List<String> negData) {
		ElementsInfo posEI = new ElementsInfo();
		ElementsInfo negEI = new ElementsInfo();
		GenerateCandidateElement.INSTANCE.genCE2(posData, negData, posEI,
				negEI, l);
		// posEI.printInfo();
		// negEI.printInfo2();
		GenerateCandidatePattern1 gcp = new GenerateCandidatePattern1(posEI,
				negEI, l, topk, minGap, maxGap);
		gcp.genCP();
		results = gcp.getResults();
		results.count = gcp.trycount;
		return results;
	}

	/**
	 * @Title: main
	 */
	public static void main(String[] args) {

		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Helsinki"));
		//
		// int l = 3; // �ȳ������仮�ֿ���
		// double a = 0.6;//
		// double b = 0.4;//
		// int minGap = 0;// gap��ʼλ��
		// int maxGap = 3600000;// gap����λ��:��ʱ��Ϊ��λ������
		// String posPath = "E:/YAN LI/�о���/�ж���/testData/pos_11111.txt";
		// String negPath = "E:/YAN LI/�о���/�ж���/testData/neg_11111.txt";
		int l = 9; // �ȳ������仮�ֿ���
		int topk = 10;
		int minGap = 0;// gap��ʼλ��
		int maxGap = 300000;// gap����λ��:��ʱ��Ϊ��λ������
		// String stopcodesPath =
		// "E:/YAN LI/�о���/�ж���/Bus Data/data/stopcode/stopcodes.txt";
		String posPath = "E:/YAN LI/�о���/�ж���/Bus Data/data/stopcode/tmp222/511_Monday.txt";
		String negPath = "E:/YAN LI/�о���/�ж���/Bus Data/data/stopcode/tmp222/511_NOMonday.txt";
		List<String> posData = new ArrayList<String>();
		List<String> negData = new ArrayList<String>();
		try {
			posData = FileUtils.readFile(posPath);
			negData = FileUtils.readFile(negPath);
		} catch (IOException e) {
			System.out.println("Error in data set file!");
			e.printStackTrace();
		}
		long t0 = System.nanoTime();

		tCSP_topk_continuous idsp = new tCSP_topk_continuous(l, topk, minGap,
				maxGap, posPath, negPath);
		Results results = idsp.run(posData, negData);
		long t1 = System.nanoTime();
		long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
		String timeCost = String.format("time took: %d ms", millis);
		String timeCostM = String.format("time took: %s m",
				String.valueOf(millis / 60000.0));
		List<String> resultlist = new ArrayList<String>();
		resultlist.add(String.valueOf(results.count));
		for (PatternInterval p : results.getpList()) {
			resultlist.add(p.print());
		}
		resultlist.add(String.valueOf(results.getSize()));
		System.out.println(timeCost);
		resultlist.add(timeCost);
		System.out.println(timeCostM);
		resultlist.add(timeCostM);
		results.statistics();
		resultlist.add(results.toString());
		// String filename =
		// "E:/YAN LI/�о���/�ж���/Bus Data/data/stopcode/result/mon_nomon/7.333333333_.txt";
		String filename = "E:/YAN LI/�о���/�ж���/Bus Data/data/stopcode/newresult/test/topk_00000_"
				+ l
				+ "_"
				+ topk
				+ "_"
				+ minGap
				+ "_"
				+ maxGap
				+ new Date().getTime() + ".txt";
		FileUtils.write2file(resultlist, filename);
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public void setMaxGap(int maxGap) {
		this.maxGap = maxGap;
	}

	public String getPos() {
		return posPath;
	}

	public void setPos(String pos) {
		this.posPath = pos;
	}

	public String getNeg() {
		return negPath;
	}

	public void setNeg(String neg) {
		this.negPath = neg;
	}

	public Results getResults() {
		return results;
	}

	public void setResults(Results results) {
		this.results = results;
	}

}
