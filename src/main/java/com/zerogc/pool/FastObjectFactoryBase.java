package com.zerogc.pool;

/**
 * @author Shawn Zhang
 */
public abstract class FastObjectFactoryBase<T> implements IFastObjectFactory<T> {

	public boolean hasActivate() { return true; }
	public boolean hasDestroy() { return true; }
	
	public void activateObject(T arg0) {}

	public void destroyObject(T arg0) {}

	public String toString() {
		return this.getClass().getName();
	}

}
