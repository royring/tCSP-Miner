package cn.edu.scu.dke.idsp.vo;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: Element
 * @Description: Store the element of sequential database
 * @author yanli
 * @date 2015��9��6�� ����3:49:31
 *
 */
public class Element {
	/**
	 * value of element
	 */
	private String value;
	/**
	 * pOccurrence: position occurrence information
	 */
	private HashMap<Integer, BitSet> pOccurrence;

//	/**
//	 * �������е���ʱ������ĳ���λ�ã�������ʱ�������������
//	 */
//	private HashMap<String, HashMap<Integer, BitSet>> subIntervalOcurrence;
	/**
	 * ��¼���п��ܵ�������
	 */
	private BitSet upperboundBitSet = new BitSet();

	/**
	 * <p>
	 * Title: construct with all fields
	 * </p>
	 * <p>
	 * Description: construct with all fields
	 * </p>
	 * 
	 * @param value
	 * @param pOccurrence
	 */
	public Element(String value, HashMap<Integer, BitSet> pOccurrence) {
		super();
		this.value = value;
		this.pOccurrence = pOccurrence;
//		this.subIntervalOcurrence = new HashMap<String, HashMap<Integer, BitSet>>();
	}

	/**
	 * <p>
	 * Title: construct with value
	 * </p>
	 * <p>
	 * Description: construct with all fields
	 * </p>
	 * 
	 * @param value
	 * @param pOccurrence
	 */
	public Element(String value) {
		super();
		this.value = value;
		this.pOccurrence = new HashMap<Integer, BitSet>();
//		this.subIntervalOcurrence = new HashMap<String, HashMap<Integer, BitSet>>();
	}

	public Element() {
		super();
//		this.subIntervalOcurrence = new HashMap<String, HashMap<Integer, BitSet>>();
		// TODO Auto-generated constructor stub
	}

	/** add new position occurrence information of this element **/
	public void addPOccurrence(int seqId, int position) {
		BitSet bs = pOccurrence.get(seqId);
		if (bs == null) {
			bs = new BitSet();
			pOccurrence.put(seqId, bs);
		}
		bs.set(position, true);
	}

	/**
	 * 
	 * @Title: getpOccurrenceBySeqId
	 * @param seqId
	 * @return BitSet
	 * @throws
	 */
	public BitSet getpOccurrenceBySeqId(int seqId) {
		return this.pOccurrence.get(seqId);
	}

	public void printInfo() {
		System.out.println("Element: " + value);
		System.out.println("  Position Occurrence: ");
		for (Integer key : pOccurrence.keySet()) {
			System.out.println("    " + key + ": " + pOccurrence.get(key));
		}
	}

	@Override
	public Element clone() {
		Element clone = new Element(this.value);
		for(Integer id: this.pOccurrence.keySet()){
			BitSet bs = (BitSet)this.pOccurrence.get(id).clone();
			clone.pOccurrence.put(id, bs);
		}
		clone.upperboundBitSet = (BitSet) this.upperboundBitSet.clone();
		return clone;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public HashMap<Integer, BitSet> getpOccurrence() {
		return pOccurrence;
	}

	public void setpOccurrence(HashMap<Integer, BitSet> pOccurrence) {
		this.pOccurrence = pOccurrence;
	}

//	public HashMap<String, HashMap<Integer, BitSet>> getSubIntervalOcurrence() {
//		return subIntervalOcurrence;
//	}
//
//	public void setSubIntervalOcurrence(
//			HashMap<String, HashMap<Integer, BitSet>> subIntervalOcurrence) {
//		this.subIntervalOcurrence = subIntervalOcurrence;
//	}

//	/**
//	 * @Title: addSubintOcur2element
//	 * @Description: ����
//	 * @param seqId
//	 * @param position
//	 * @param windowScope
//	 *            �趨�ļ�
//	 * @return void ��������
//	 * @throws
//	 */
//	public void addSubintOcur2element(int seqId, int position, Window window) {
//		List<BitSet> windowlist = window.getWindowlist();// ����������
//		Map<String, Map<Integer, Integer[]>> windowScope = window
//				.getWindowScope();// ���������Ӧsequence��λ����Ϣ
//
//		for (int i = 0; i < windowlist.size(); i++) {
//			BitSet b = windowlist.get(i);
//			Map<Integer, Integer[]> windScopeIndex = windowScope.get(b
//					.toString());// ����������������seq�������ʱ�������λ����Ϣ
//			Integer[] windex = windScopeIndex.get(seqId);
//			HashMap<Integer, BitSet> seqmap = subIntervalOcurrence.get(b
//					.toString());
//			if (seqmap == null) {
//				seqmap = new HashMap<Integer, BitSet>();
//				subIntervalOcurrence.put(b.toString(), seqmap);
//			}
//			if (windex[0] <= position && position <= windex[1]) {
//				BitSet positionBitSet = seqmap.get(seqId);
//				if (positionBitSet == null) {
//					positionBitSet = new BitSet();
//					seqmap.put(seqId, positionBitSet);
//				}
//				positionBitSet.set(position, true);
//			}else if (position < windex[0]) {
//				break;
//			}
//
//		}
//
//	}

	public void setIntervalsBitSet(int seqId, int position, Window window) {
		List<BitSet> windowlist = window.getWindowlist();// ����������
		Map<String, Map<Integer, Integer[]>> windowScope = window
				.getWindowScope();// ���������Ӧsequence��λ����Ϣ
		for (int i = 0; i < windowlist.size(); i++) {
			BitSet b = windowlist.get(i);
			Map<Integer, Integer[]> windScopeIndex = windowScope.get(b.toString());// ����������������seq�������ʱ�������λ����Ϣ
			Integer[] windex = windScopeIndex.get(seqId);
			if (windex[0] <= position && position <= windex[1]) {
				String bstr = b.toString();
				int index = Integer.valueOf(bstr.substring(1,bstr.length()-1));
				this.getUpperboundBitSet().set(index);
				break;
			}
		}
		//this.setIntervalsBitSet(window.getRegion());

	}

	public BitSet getUpperboundBitSet() {
		return upperboundBitSet;
	}

	public void setUpperboundBitSet(BitSet upperboundBitSet) {
		this.upperboundBitSet = upperboundBitSet;
	}

}
