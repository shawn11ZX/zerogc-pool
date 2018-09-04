package com.zerogc.pool;

/**
 * @author Shawn Zhang
 */
public class DefaultFastObjectFactory<T> extends FastObjectFactoryBase<T> {
	final private Class<? extends T> clazz;

	@Override
	public String toString()
	{
		return clazz.getName();
	}
	
	public DefaultFastObjectFactory(Class<? extends T> clazz)
	{
		this.clazz = clazz;
	}
	
	public boolean hasActivate() { return false; }
	public boolean hasDestroy() { return false; }

	
	@Override
	public T makeObject() {
		T obj = null;
		try {
			obj = clazz.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
};
