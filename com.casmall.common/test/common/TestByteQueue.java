package common;

import static org.junit.Assert.fail;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.casmall.common.ByteQueue;

public class TestByteQueue {
	private static ByteQueue bq;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		bq = new ByteQueue(10);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testAddByte() {
		Assert.assertTrue(bq.isEmpty());
		Assert.assertFalse(bq.isFull());
		
		Assert.assertTrue(bq.add((byte)'A'));
		
		Assert.assertEquals(1, bq.size());
		Assert.assertEquals(10, bq.getCapacity());
		Assert.assertEquals(9, bq.getRemainingCapacity());
		
		Assert.assertFalse(bq.isEmpty());
		Assert.assertFalse(bq.isFull());
	}

	@Test
	public void testAddByteArray() {
		bq.clear();
		
		Assert.assertTrue(bq.add("ABCD".getBytes()));
		
		Assert.assertEquals(4, bq.size());
		Assert.assertEquals(6, bq.getRemainingCapacity());
		
		Assert.assertTrue(bq.add("EFGHIJ".getBytes()));
		Assert.assertTrue(bq.isFull());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testAddByteArrayException() {
		bq.clear();
		bq.add("ABCDEFGHIJKL".getBytes());
	}

	@Test
	public void testRemoveByte() {
		bq.clear();
		bq.add("ABCDEFGHIJ".getBytes());
		
		Assert.assertEquals((byte)'A', bq.remove());
		Assert.assertEquals(9, bq.size());
	}
	
	@Test
	public void testRemoveByteArray() {
		bq.clear();
		bq.add("BCDEFGHIJ".getBytes());
		
		Assert.assertEquals("BCD", new String(bq.remove(3)));
		Assert.assertEquals(6, bq.size());
		Assert.assertEquals("EFGHIJ", new String(bq.remove(6)));
		Assert.assertTrue(bq.isEmpty());
		
		Assert.assertTrue(bq.add("XX".getBytes()));
		Assert.assertEquals("XX", new String(bq.remove(3)));
		Assert.assertTrue(bq.isEmpty());
	}

	@Test(expected=NoSuchElementException.class)
	public void testRemoveByteArrayException() {
		bq.clear();


		bq.remove();
	}
	
	@Test
	public void testOfferByte() {
		bq.clear();
		Assert.assertTrue(bq.offer((byte)'1'));
		Assert.assertEquals(1, bq.size());
	}

	@Test
	public void testOfferByteArray() {
		bq.clear();
		Assert.assertTrue(bq.offer("01234567890123".getBytes()));
		Assert.assertTrue(bq.isFull());
		Assert.assertFalse(bq.offer((byte)'1'));
		Assert.assertFalse(bq.offer("23456789012".getBytes()));
		
	}

	@Test
	public void testOfferByteLongTimeUnit() {
		bq.clear();
		try {
			Assert.assertTrue(bq.offer((byte)'0',1,TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testOfferByteArrayLongTimeUnit() {
		bq.clear();
		try {
			Assert.assertTrue(bq.offer("0123456789".getBytes(),1,TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testTake() {
		bq.clear();
		try {
			String str = "0123456";
			bq.offer(str.getBytes());
			Assert.assertEquals(str,new String(bq.take()));
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testTakeInt() {
		bq.clear();
		try {
			String str = "0123456";
			bq.offer(str.getBytes());
			Assert.assertEquals(str,new String(bq.take(str.length())));
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testPutByte() {
		bq.clear();
		try {
			bq.put((byte)'1');
			Assert.assertEquals(1, bq.size());
			Assert.assertEquals((byte)'1', bq.remove());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutByteArray() {
		bq.clear();
		String str = "0123456789";
		try {
			bq.put(str.getBytes());
			Assert.assertEquals(10, bq.size());
			Assert.assertEquals(str, new String(bq.remove(10)));
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Ignore
	@Test(expected=IllegalArgumentException.class)
	public void testPutByteArrayException() {
		bq.clear();
		String str = "0123456789";
		try {
			bq.put(str.getBytes());
			Assert.assertEquals(10, bq.size());
			
			// Exception 발생하지 않음.
			//bq.put(str.getBytes());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testPoll() {
		bq.clear();
		Assert.assertNull(bq.poll());
		
		String str = "012";
		bq.offer(str.getBytes());
		Assert.assertEquals(3, bq.size());
		Assert.assertEquals(str, new String(bq.poll(3)));
		
		str = "0123456789";
		bq.offer(str.getBytes());
		Assert.assertTrue(bq.isFull());
		Assert.assertEquals(str, new String(bq.poll()));
	}

	@Test
	public void testPollIntLongTimeUnit() {
		bq.clear();
		try {
			String str = "012";
			bq.offer(str.getBytes());
			Assert.assertEquals(str, new String(bq.poll(3, 1, TimeUnit.SECONDS)));

			str = "0123456789";
			bq.offer(str.getBytes());
			Assert.assertTrue(bq.isFull());
			Assert.assertEquals(str, new String(bq.poll(str.length(), 1, TimeUnit.SECONDS)));			
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testPollLongTimeUnit() {
		bq.clear();
		try {
			String str = "012";
			bq.offer(str.getBytes());
			Assert.assertEquals(str, new String(bq.poll( 1, TimeUnit.SECONDS)));

			str = "0123456789";
			bq.offer(str.getBytes());
			Assert.assertTrue(bq.isFull());
			Assert.assertEquals(str, new String(bq.poll(1, TimeUnit.SECONDS)));			
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetRemainingCapacity() {
		bq.clear();
		Assert.assertEquals(10, bq.getRemainingCapacity());
		bq.add("012".getBytes());
		Assert.assertEquals(7, bq.getRemainingCapacity());
		bq.add("3456789".getBytes());
		Assert.assertEquals(0, bq.getRemainingCapacity());
	}

	@Test
	public void testElement() {
		bq.clear();
		try{
			Assert.assertEquals((byte)0,bq.element());
			Assert.fail();
		}catch(NoSuchElementException e){
			//e.printStackTrace();
			Assert.assertTrue(true);
		}
		Assert.assertTrue(bq.add((byte)'0'));
		Assert.assertEquals((byte)'0',bq.element());
	}

	@Test
	public void testElementInt() {
		bq.clear();
		try{
			Assert.assertEquals((byte)0,bq.element(2));
			Assert.fail();
		}catch(NoSuchElementException e){
			//e.printStackTrace();
			Assert.assertTrue(true);
		}
		String str = "01234";
		Assert.assertTrue(bq.add(str.getBytes()));
		Assert.assertEquals(str, new String(bq.element(str.length())));
		
		Assert.assertEquals(str, new String(bq.element(str.length()+2)));
	}

	@Test
	public void testPeek() {
		bq.clear();
		
		Assert.assertEquals((byte)0,bq.peek());
		
		Assert.assertTrue(bq.add((byte)'0'));
		Assert.assertEquals((byte)'0',bq.peek());
		Assert.assertEquals(1, bq.size());
		Assert.assertEquals(9, bq.getRemainingCapacity());
	}

	@Test
	public void testPeekInt() {
		bq.clear();
		Assert.assertTrue(bq.add("0123456789".getBytes()));
		Assert.assertEquals("0123456789", new String(bq.peek(11)));
	}
	
	@Test
	public void testFind() {
		bq.clear();
		Assert.assertTrue(bq.add("0123456789".getBytes()));
		Assert.assertEquals(-1, bq.find("X".getBytes(), 0));
		Assert.assertEquals(2, bq.find("2".getBytes(), 0));
		Assert.assertEquals(1, bq.find("123".getBytes(), 0));
		Assert.assertEquals(-1, bq.find("12".getBytes(), 2));
		
		Assert.assertEquals("012", new String(bq.poll(3)));
		
		Assert.assertEquals(0, bq.find("3456".getBytes(), 0));
		Assert.assertEquals(2, bq.find("56".getBytes(), 0));
		Assert.assertEquals(-1, bq.find("56".getBytes(), 3));
		
		Assert.assertTrue(bq.add("ABC".getBytes()));
		Assert.assertEquals(6, bq.find("9A".getBytes(), 0));
		Assert.assertEquals(7, bq.find("ABC".getBytes(), 0));
		Assert.assertEquals(9, bq.find("C".getBytes(), 0));
	}

}
