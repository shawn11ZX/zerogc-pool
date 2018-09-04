package com.zerogc.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author Shawn Zhang
 */

public class FastObjectPool<T> {
	static List<FastObjectPool> ALL_POOLS = Collections.synchronizedList(new ArrayList<FastObjectPool>());

	public static String printAllDebugInfo()
	{

		StringBuilder sb = new StringBuilder();
		sb.append("<p>FastObjectPool</p>");
		sb.append("<table>");
		sb.append("<thead>");
		sb.append("<td>name</td>");
		sb.append("<td>allocCount</td>");
		sb.append("<td>freeCount</td>");
		sb.append("<td>activeCount</td>");
		sb.append("<td>newCount</td>");
		sb.append("<td>dupCount</td>");
		sb.append("</thead>");
		for (int i = 0; i < ALL_POOLS.size(); i++)
		{
			ALL_POOLS.get(i).printDebugInfo(sb);
		}
		sb.append("</table>");
		return sb.toString();
	}
	void printDebugInfo(StringBuilder sb)
	{
		sb.append("<tr>");
		sb.append("<td>");
		sb.append(this._factory.toString());
		sb.append("</td>");

		sb.append("<td>");
		sb.append(this.allocCount.get());
		sb.append("</td>");

		sb.append("<td>");
		sb.append(this.freeCount.get());
		sb.append("</td>");

		sb.append("<td>");
		sb.append(this.allocCount.get() - this.freeCount.get());
		sb.append("</td>");

		sb.append("<td>");
		sb.append(this.newCount.get());
		sb.append("</td>");


		int[] stamp = new int[1];
		HashSet<Object> h = new HashSet<Object>();
		int dupCount = 0;
		Node first = _objectPool._nodeHead.get(stamp);
		for (int i = 0; i < 1000000 && first != null; i++)
		{
			Object o = first.item;
			first = first.next;

			if (o != null)
			{
				int id = System.identityHashCode(o);
				if (h.contains(id)) {
					dupCount++;
				}
				else {
					h.add(id);
				}
			}
		}
		sb.append("<td>");
		sb.append(dupCount);
		if (first != null)
			sb.append("error");
		sb.append("</td>");


		sb.append("</tr>");

	}

	public void close() {

	}

	public static class Node<T> {
		public volatile T item;
		public volatile Node<T> next;
	}
	
	public static class NodePool<T> {
		public final AtomicStampedReference<Node<T>> _nodeHead = new AtomicStampedReference<Node<T>>(null, 0);
		Node<T> pop()
		{
			Node<T> oldHead;
			Node<T> newHead;
			int stamp;
			do {
				stamp = _nodeHead.getStamp();
				oldHead = _nodeHead.getReference();
				if (oldHead == null)
				{
					return null;
				}
				newHead = oldHead.next;
				if (_nodeHead.compareAndSet(oldHead, newHead, stamp, stamp+1))
					break;
				else
					Thread.yield();
			} while(true);
			oldHead.next = null;
			return oldHead;
		}
		
		void push(Node<T> node)
		{
			Node<T> oldHead;	
			int stamp;
			do {
				stamp = _nodeHead.getStamp();
				oldHead = _nodeHead.getReference();
				node.next = oldHead;
				if (_nodeHead.compareAndSet(oldHead, node, stamp, stamp+1))
					break;
				else
					Thread.yield();
			} while (true);
		}
	}
	
	
	
	final IFastObjectFactory<T> _factory;
	public final NodePool<T> _nodePool = new NodePool<T>();
	public final NodePool<T> _objectPool = new NodePool<T>();
	final AtomicLong newCount = new AtomicLong();
	final AtomicLong allocCount = new AtomicLong();
	final AtomicLong freeCount = new AtomicLong();
	final boolean hasActivate;
	final boolean hasDestroy;
	public FastObjectPool(IFastObjectFactory<T> factory)
	{
		ALL_POOLS.add(this);
		_factory = factory;
		hasActivate = factory.hasActivate();
		hasDestroy = factory.hasDestroy();
	}
	
	
	public T borrowObject()
	{
		
		allocCount.incrementAndGet();
		Node<T> node = _objectPool.pop();
		
		T item;
		if (node != null)
		{
			item = node.item;
			node.item = null;
			_nodePool.push(node);
			
		}
		else
		{
			newCount.incrementAndGet();
			item = _factory.makeObject();
		}
		if(hasActivate)
		{
			_factory.activateObject(item);
		}
		return item;
	}
	
	
	
	public void returnObject(T item)
	{
		if (item == null)
			return;
		if (hasDestroy)
		{
			_factory.destroyObject(item);
		}
		
		Node<T> node = _nodePool.pop();
		if (node == null)
		{
			node = new Node<T>();
		}
		node.item = item;
		_objectPool.push(node);
		
		freeCount.incrementAndGet();
	}
}
