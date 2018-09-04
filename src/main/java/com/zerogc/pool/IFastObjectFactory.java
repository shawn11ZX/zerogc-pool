package com.zerogc.pool;



/**
 * @author Shawn Zhang
 */
public interface IFastObjectFactory<T>  {

	boolean hasActivate();
	
	boolean hasDestroy();
	
	T makeObject();
	
	void activateObject(T arg0);

	void destroyObject(T arg0);

}
