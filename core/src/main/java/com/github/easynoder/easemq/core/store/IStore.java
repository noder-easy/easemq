package com.github.easynoder.easemq.core.store;

import com.github.easynoder.easemq.core.exception.StoreException;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public interface IStore<T> {

    public boolean store(T data) throws StoreException;

    public T get() throws StoreException;
}
