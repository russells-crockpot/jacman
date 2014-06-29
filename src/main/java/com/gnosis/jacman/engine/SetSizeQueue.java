/**
 * 
 */
package com.gnosis.jacman.engine;

/**
 * @author user
 *
 */
public class SetSizeQueue<E> {
	
	private class Node{
		Node next;
		E datum;
		Node (Node next, E datum){
			this.next = next;
			this.datum = datum;
		}
		Node (E datum){
			this(null, datum);
		}
	}
	
	private Node first, last;
	private int size, maxSize;
	
	public SetSizeQueue(int maxSize){
		this.maxSize = maxSize;
		this.first = null;
		this.last = null;
		this.size = 0;
	}
	
	public E getFirst(){
		if (size < maxSize){
			return null;
		}
		return first.datum;
	}
	
	public E enque(E datum){
		if (size == 0){
			Node n = new Node(datum);
			first = n;
			last = n;
			size++;
			return null;
		}
		else if(size == 1){
			this.last = new Node(datum);
			this.first.next = this.last;
			size++;
			return null;
		}
		else {
			Node n = new Node(datum);
			this.last.next = n;
			this.last = n;
			size++;
			if (size < maxSize){
				return null;
			}
			else{
				return deque();
			}
		}
		
	}
	public E deque(){
		if (size < maxSize){
			return null;
		}
		else{
			E datum = first.datum;
			this.first = first.next;
			size--;
			return datum;
		}
	}

	/**
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		//TODO check max size to see if we should take some off
	}
}
