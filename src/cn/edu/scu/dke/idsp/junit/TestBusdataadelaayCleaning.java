package cn.edu.scu.dke.idsp.junit;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimeZone;

import org.junit.Test;

import cn.edu.scu.dke.idsp.util.BitSetUtils;
import cn.edu.scu.dke.idsp.util.DateUtil;
import cn.edu.scu.dke.idsp.util.SubBitSetList;

/** 
 * @ClassName: TestBusdataadelaayCleaning 
 * @Description: TODO
 * @author yanli
 * @date 2016骞?鏈?鏃?涓嬪崍2:21:06 
 *  
 */
public class TestBusdataadelaayCleaning {
//	@Test
	public void  testDate(){
		//TimeZone.setDefault(TimeZone.getTimeZone("Europe/Helsinki"));
		//DateUtil.date2timeFromZero(1445870778026l);
		BitSet b = new BitSet();
		b.set(0);
		b.set(0, 0, false);
		b.set(3);
		b.set(4);
		ArrayList<BitSet> regionsList = SubBitSetList.INSTANCE.getSub(b);
		for (BitSet bitSet : regionsList) {
			System.out.println(bitSet);
		}
	}
	@Test
	public void  testDate111(){
		//TimeZone.setDefault(TimeZone.getTimeZone("Europe/Helsinki"));
		//DateUtil.date2timeFromZero(1445870778026l);
		Queue<BitSet> queue = new LinkedList<BitSet>();
		BitSet bs = new BitSet();
//		bs.set(0);
		bs.set(6,8);
		BitSetUtils.obtainContinuousTimeInterval(queue, bs);
		//队列方式遍历，元素逐个被移除 
        while (queue.peek() != null) { 
                System.out.println(queue.poll()); 
        } 
	}
//	@Test
	public void  testQueueContinuousTimeInterval(){
		//TimeZone.setDefault(TimeZone.getTimeZone("Europe/Helsinki"));
		//DateUtil.date2timeFromZero(1445870778026l);
		Queue<BitSet> queue = new LinkedList<BitSet>();
		BitSet bs = new BitSet();
//		bs.set(0);
		bs.set(4);
//		bs.set(6,8);
		BitSetUtils.queueContinuousTimeInterval(queue, bs);
		//队列方式遍历，元素逐个被移除 
        while (queue.peek() != null) { 
                System.out.println(queue.poll()); 
        } 
	}
}
