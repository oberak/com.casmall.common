package com.casmall.common;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ByteQueue {
	protected static Log logger = LogFactory.getLog(ByteQueue.class);

	private byte[] queue;
	private int capacity;
	private int size;
	private int head;
	private int tail;

	public ByteQueue(int cap) {
		capacity = (cap > 0) ? cap : 1; // at least 1
		queue = new byte[capacity];
		head = 0;
		tail = 0;
		size = 0;
	} // ByteQueue

	/**
	 * Inserts the specified element into this queue if it is possible to do so
	 * immediately without violating capacity restrictions, returning
	 * <tt>true</tt> upon success and throwing an <tt>IllegalStateException</tt>
	 * if no space is currently available.
	 * 
	 * @param e
	 *            the element to add
	 * @return <tt>true</tt>
	 * @throws IllegalStateException
	 *             if the element cannot be added at this time due to capacity
	 *             restrictions
	 */
	public synchronized boolean add(byte e) {
		if (isFull()) {
			throw new IllegalStateException("Queue is full!");
		}

		queue[head] = e;
		head = (head + 1) % capacity;
		size++;

		if (logger.isDebugEnabled()) {
			logQueue("add byte '", new byte[]{e});
		}

		notifyAll(); // let any waiting threads know about change
		
		return true;
	} // add byte

	/**
	 * Inserts the specified element into this queue if it is possible to do so
	 * immediately without violating capacity restrictions, returning
	 * <tt>true</tt> upon success and throwing an <tt>IllegalStateException</tt>
	 * if no space is currently available.
	 * 
	 * @param bytes
	 *            the element to add
	 * @return <tt>true</tt>
	 * @throws NullPointerException
	 *             if the specified element is null
	 * @throws IllegalStateException
	 *             if the element cannot be added at this time due to capacity
	 *             restrictions
	 */
	public synchronized boolean add(byte[] bytes) {
		if (bytes == null) {
			throw new NullPointerException("element is null!");
		}
		if (isFull()) {
			throw new IllegalStateException("Queue is full!");
		}

		if (getRemainingCapacity() < bytes.length) {
			throw new IllegalStateException(
					"The remaining capacity is not sufficient.(r:"
							+ getRemainingCapacity() + "/a:" + bytes.length
							+ ")");
		}

		int ptr = 0;

		while (ptr < bytes.length) {
			int distToEnd = capacity - head;
			int blockLen = Math.min(this.getRemainingCapacity(), distToEnd);

			int bytesRemaining = bytes.length - ptr;
			int copyLen = Math.min(blockLen, bytesRemaining);

			System.arraycopy(bytes, ptr, queue, head, copyLen);
			head = (head + copyLen) % capacity;
			size += copyLen;
			ptr += copyLen;
		} // while

		if (logger.isDebugEnabled()) {
			logQueue("add bytes", bytes);
		}
		
		notifyAll(); // let any waiting threads know about change
		
		return true;
	} // add bytes

	/**
	 * Retrieves and removes the head of this queue. This method differs from
	 * the poll method in that it throws an exception if this queue is empty.
	 * 
	 * @return the head of this queue
	 * @throws NoSuchElementException
	 *             If this queue is empty.
	 */
	public synchronized byte remove() throws NoSuchElementException {

		if (isEmpty()) {
			throw new NoSuchElementException("Queue is empty!");
		}

		byte b = queue[tail];
		tail = (tail + 1) % capacity;
		size--;

		if (logger.isDebugEnabled()) {
			logQueue("remove",new byte[]{b});
		}
		
		notifyAll(); // let any waiting threads know about change

		return b;
	}
	
	/**
	 * Retrieves and removes the head of this queue. This method differs from
	 * the poll method in that it throws an exception if this queue is empty.
	 * If requested length is longer, remove all
	 * 
	 * @param len length of Retrieves
	 * @return the head of this queue
	 * @throws NoSuchElementException
	 *             If this queue is empty.
	 */
	public synchronized byte[] remove(int len) throws NoSuchElementException {

		if (isEmpty()) {
			throw new NoSuchElementException("Queue is empty!");
		}

		byte[] bytes = null;
		if(len > size()){
			if(logger.isWarnEnabled()){
				logger.warn("remove size too big. reassign to queue size. queue:"+size()+"/request:"+len);
			}
			bytes = trunkData(size());
		}else{
			bytes = trunkData(len);
		}
		

		if (logger.isDebugEnabled()) {
			logQueue("remove("+len+")", bytes);
		}
		
		notifyAll(); // let any waiting threads know about change

		return bytes;
	}

	/**
	 * Inserts the specified element into this queue if it is possible to do so
	 * immediately without violating capacity restrictions, returning
	 * <tt>true</tt> upon success and <tt>false</tt> if no space is currently
	 * available.
	 * 
	 * @param e
	 *            the element to add
	 * @return <tt>true</tt> if the element was added to this queue, else
	 *         <tt>false</tt>
	 */
	public synchronized boolean offer(byte e) {
		if (isFull()) {
			if (logger.isWarnEnabled()) {
				logger.warn("Queue is full!");
			}
			return false;
		}

		return add(e);
	} // offer

	/**
	 * Inserts the specified element into this queue if it is possible to do so
	 * immediately without violating capacity restrictions, returning
	 * <tt>true</tt> upon success and <tt>false</tt> if no space is currently
	 * available.
	 * If the remaining capacity is not sufficient, offer as possible
	 * 
	 * @param bytes
	 *            the element to add
	 * @return <tt>true</tt> if the element was added to this queue, else
	 *         <tt>false</tt>
	 * @throws NullPointerException
	 *             if the specified element is null
	 * @throws IllegalArgumentException
	 *             if some property of the specified element prevents it from
	 *             being added to this queue
	 */
	public synchronized boolean offer(byte[] bytes) {
		if (bytes == null) {
			throw new NullPointerException("element is null!");
		}

		if (isFull()) {
			if (logger.isWarnEnabled()) {
				logger.warn("Queue is full!");
			}
			return false;
		}

		// If capacity is not sufficient, insert as much as possible.
		if (getRemainingCapacity() < bytes.length) {
			if(logger.isWarnEnabled()){
				logger.warn("The remaining capacity is not sufficient.(queue:"
							+ getRemainingCapacity() + "/offer:" + bytes.length
							+ ")");
			}
			byte[] rList = new byte[getRemainingCapacity()];
			System.arraycopy(bytes, 0, rList, 0, getRemainingCapacity());
			if (logger.isDebugEnabled()) {
				logger.debug("capacity is not sufficient. offer as possible.");
				logger.debug("String:" + new String(rList));
				logger.debug("Hex:" + StringUtil.printHex(rList));
			}
			return add(rList);
		}
		return add(bytes);
	}

	/**
	 * Inserts the specified element into this queue, waiting up to the
	 * specified wait time if necessary for space to become available.
	 * 
	 * @param e
	 *            the element to add
	 * @param timeout
	 *            how long to wait before giving up, in units of <tt>unit</tt>
	 * @param unit
	 *            a <tt>TimeUnit</tt> determining how to interpret the
	 *            <tt>timeout</tt> parameter
	 * @return <tt>true</tt> if successful, or <tt>false</tt> if the specified
	 *         waiting time elapses before space is available
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 * @throws IllegalArgumentException
	 *             if some property of the specified element prevents it from
	 *             being added to this queue
	 */
	public synchronized boolean offer(byte e, long timeout, TimeUnit unit)
			throws InterruptedException {
		while (isFull()) {
			wait(unit.convert(timeout, unit));
		}

		return offer(e);
	} // offer

	/**
	 * Inserts the specified element into this queue, waiting up to the
	 * specified wait time if necessary for space to become available.
	 * If the remaining capacity is not sufficient, offer as possible
	 * 
	 * @param bytes
	 *            the element to add
	 * @param timeout
	 *            how long to wait before giving up, in units of <tt>unit</tt>
	 * @param unit
	 *            a <tt>TimeUnit</tt> determining how to interpret the
	 *            <tt>timeout</tt> parameter
	 * @return <tt>true</tt> if successful, or <tt>false</tt> if the specified
	 *         waiting time elapses before space is available
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 * @throws NullPointerException
	 *             if the specified element is null
	 * @throws IllegalArgumentException
	 *             if some property of the specified element prevents it from
	 *             being added to this queue
	 */
	public synchronized boolean offer(byte[] bytes, long timeout, TimeUnit unit)
			throws InterruptedException {
		if(bytes == null){
			throw new NullPointerException("offer bytes is null!");
		}
		while (isFull()) {
			wait(unit.convert(timeout, unit));
		}

		return offer(bytes);
	} // offer

	/**
	 * Inserts the specified element into this queue, waiting if necessary for
	 * space to become available.
	 * 
	 * @param e
	 *            the element to add
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 * @throws IllegalArgumentException
	 *             if some property of the specified element prevents it from
	 *             being added to this queue
	 */
	public synchronized void put(byte e) throws InterruptedException {

		// If full, waiting
		while (isFull()) {
			wait();
		}

		add(e);

		notifyAll(); // let any waiting threads know about change
	} // add

	/**
	 * Inserts the specified element into this queue, waiting if necessary for
	 * space to become available.
	 * 
	 * @param bytes
	 *            the element to add
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 * @throws NullPointerException
	 *             if the specified element is null
	 * @throws IllegalArgumentException
	 *             if some property of the specified element prevents it from
	 *             being added to this queue
	 */
	public synchronized void put(byte[] bytes) throws InterruptedException {
		if (bytes == null) {
			throw new NullPointerException("put element is null!");
		}
		// If full or not available, waiting
		while (isFull() || getRemainingCapacity() < bytes.length) {
			wait();
		}

		add(bytes);

		notifyAll(); // let any waiting threads know about change
	} // add

	/**
	 * Retrieves and removes the head of this queue, waiting if necessary until
	 * an element becomes available.
	 * 
	 * @return all elements of this queue
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 */
	public synchronized byte[] take() throws InterruptedException {
		return take(size);
	} // take

	/**
	 * Retrieves and removes the head of this queue, waiting if necessary until
	 * an element becomes available.
	 * 
	 * @param len
	 *            length of get bytes
	 * @return the head N bytes of this queue
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 */
	public synchronized byte[] take(int len) throws InterruptedException {
		while (isEmpty() || this.size < len) {
			wait();
		}

		byte[] bytes = trunkData(len);

		if (logger.isDebugEnabled()) {
			logQueue("take bytes("+len+")'", bytes);
		}
		// Signal any and all waiting threads that
		// something has changed.
		notifyAll();

		return bytes;
	} // take

	/**
	 * Retrieves and removes the head of this queue, or null if this queue is
	 * empty.
	 * 
	 * @return all data of this queue, or <tt>null</tt> if this queue is empty.
	 */
	public synchronized byte[] poll() {
		return poll(size);
	}

	/**
	 * Retrieves and removes the head of this queue, or null if this queue is
	 * empty.
	 * 
	 * @param len
	 *            length of Retrieves
	 * @return the head of this queue, or <tt>null</tt> if this queue is empty.
	 */
	/**
	 * @param len
	 * @return the head of this queue, or <tt>null</tt> if this queue is empty.
	 */
	public synchronized byte[] poll(int len) {
		if (isEmpty()) {
			return null;
		}

		byte[] bytes = trunkData(len);
		
		if (logger.isDebugEnabled()) {
			logQueue("poll bytes("+len+")", bytes);
		}
		notifyAll(); // let any waiting threads know about change
		return bytes;
	}

	/**
	 * Retrieves and removes the head of this queue, waiting up to the specified
	 * wait time if necessary for an element to become available.
	 * 
	 * @param timeout
	 *            how long to wait before giving up, in units of <tt>unit</tt>
	 * @param unit
	 *            a <tt>TimeUnit</tt> determining how to interpret the
	 *            <tt>timeout</tt> parameter
	 * @return the head of this queue, or <tt>null</tt> if the specified waiting
	 *         time elapses before an element is available
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 */
	public synchronized byte[] poll(int len, long timeout, TimeUnit unit)
			throws InterruptedException {
		while (isEmpty() || this.size < len) {
			wait(unit.convert(timeout, unit));
		}

		byte[] bytes = trunkData(len);
		if (logger.isDebugEnabled()) {
			logQueue("poll bytes("+len+") with timeout", bytes);
		}
		// Signal any and all waiting threads that
		// something has changed.
		notifyAll();

		return bytes;
	}

	/**
	 * Retrieves and removes the head of this queue, waiting up to the specified
	 * wait time if necessary for an element to become available.
	 * 
	 * @param timeout
	 *            how long to wait before giving up, in units of <tt>unit</tt>
	 * @param unit
	 *            a <tt>TimeUnit</tt> determining how to interpret the
	 *            <tt>timeout</tt> parameter
	 * @return the head of this queue, or <tt>null</tt> if the specified waiting
	 *         time elapses before an element is available
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 */
	public synchronized byte[] poll(long timeout, TimeUnit unit)
			throws InterruptedException {
		return poll(size, timeout, unit);
	} // poll

	/**
	 * Retrieves, but does not remove, the head of this queue
	 * 
	 * @return the head of this queue
	 * @throws NoSuchElementException
	 *             If this queue is empty
	 */
	public synchronized byte element() throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException("Queue is empty!");
		}
		return queue[tail];
	} // element

	/**
	 * Retrieves, but does not remove, the head of this queue
	 * 
	 * @param len
	 *            length of retrieves
	 * @return the head of this queue
	 * @throws NoSuchElementException
	 *             If this queue is empty
	 */
	public synchronized byte[] element(int len) throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException("Queue is empty!");
		}
		if (len > size()) {
			return selectData(size());
		}

		return selectData(len);
	} // element

	/**
	 * Retrieves, but does not remove, the head of this queue, or returns null
	 * if this queue is empty.
	 * 
	 * @return the head of this queue
	 */
	public synchronized byte peek() {
		if (isEmpty()) {
			return 0;
		}
		return queue[tail];
	} // peek

	/**
	 * Retrieves, but does not remove, the head of this queue, or returns null
	 * if this queue is empty.
	 * 
	 * @param len
	 *            length of peek
	 * @return the head of this queue
	 */
	public synchronized byte[] peek(int len) {
		if (isEmpty()) {
			return null;
		}
		if (len > size()) {
			return selectData(size());
		}

		return selectData(len);
	} // peek

	/**
	 * clear queue
	 */
	public synchronized void clear() {
		queue = new byte[capacity];
		head = 0;
		tail = 0;
		size = 0;
		
		if(logger.isDebugEnabled()){
			logger.debug("Clear queue.");
		}
		notifyAll(); // let any waiting threads know about change
	} // clear

	/**
	 * Get Size
	 * 
	 * @return Data size of Queue
	 */
	public synchronized int size() {
		return size;
	} // size

	/**
	 * Whether or not empty
	 * 
	 * @return <tt>true</tt> if queue is null, else <tt>false</tt>
	 */
	public synchronized boolean isEmpty() {
		return (size == 0);
	} // isEmpty

	/**
	 * Whether or not full
	 * 
	 * @return <tt>true</tt> if queue is full, else <tt>false</tt>
	 */
	public synchronized boolean isFull() {
		return (size == capacity);
	} // isFull

	/**
	 * Get Capacity
	 * 
	 * @return Capacity of Queue
	 */
	public synchronized int getCapacity() {
		return capacity;
	} // getCapacity

	/**
	 * Returns the number of additional elements that this queue can ideally (in
	 * the absence of memory or resource constraints) accept without blocking,
	 * or Integer.MAX_VALUE if there is no intrinsic limit
	 * 
	 * @return
	 */
	public synchronized int getRemainingCapacity() {
		return capacity - size;
	} // getRemainingCapacity

	/**
	 * Retrieves and removes the head of this queue
	 * 
	 * @param len
	 *            length of Retrieves
	 * @return the head N bytes of this queue
	 */
	private synchronized byte[] trunkData(int len) {
		byte[] list = selectData(len);

		tail = (tail + len) % capacity;
		size = size - len; // everything has been removed
		return list;
	} // trunkData

	/**
	 * Retrieves, but does not remove, the head of this queue
	 * 
	 * @param len
	 *            length of Retrieves
	 * @return the head N bytes of this queue
	 */
	private synchronized byte[] selectData(int len) {
		byte[] list = new byte[len];

		// copy in the block from tail to the end
		int distToEnd = capacity - tail;
		int copyLen = Math.min(len, distToEnd);
		System.arraycopy(queue, tail, list, 0, copyLen);

		// If data wraps around, copy the remaining data
		// from the front of the array.
		if (len > copyLen) {
			System.arraycopy(queue, 0, list, copyLen, len - copyLen);
		}
		return list;
	} // selectData

	/**
	 * Find byte from Queue
	 * 
	 * @param find
	 * @return
	 */
	public int find(byte[] find){
		return find(find,0);
	}
	
	/**
	 * Find byte from Queue with start position
	 * 
	 * @param find find bytes
	 * @param start start position
	 * @return
	 */
	public int find(byte[] find, int start){
		int pos = -1;
		int idx = 0;
		boolean isFound;
		if(size>0 && size > start){
			for(int i=0;i<=size-find.length;i++){
				isFound = false;
				idx = start+tail + i;
				if(idx >= capacity){
					idx -= capacity;
				}
				if(queue[idx] == find[0]){
					isFound = true;
					for(int j=1;j<find.length;j++){
						if(queue[idx + j - ( ((idx+j) < capacity) ? 0 : capacity )] != find[j]){
							isFound = false;
							break;
						}
					} // for
				} // if
				
				if(isFound){
					pos = idx - tail;
					if(pos < 0){
						pos += capacity;
					}
					return pos;
				}
			} // for			
		} // if
		return pos;
	}
	/**
	 * logging Queue
	 * 
	 * @param msg
	 *            logging method name
	 */
	public synchronized void logQueue(String msg, byte[] data) {
		if (logger.isDebugEnabled()) {
			if(isEmpty()){
				logger.debug("queue : Empty - after:"+msg );
			}else{
				byte[] bytes = peek(size);
				logger.debug("queue : ["+new String(bytes)+"] - after "+msg+"("+data.length+" bytes)" );
				//logger.debug("queue : ["+new String(bytes)+"] ["+StringUtil.printHex(bytes)+"] - after "+msg+"("+data.length+" bytes)" );
			} // if
		}
	} // logQueue
}