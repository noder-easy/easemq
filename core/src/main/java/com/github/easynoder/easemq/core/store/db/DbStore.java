package com.github.easynoder.easemq.core.store.db;

import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.core.store.IStore;

/**
 * Desc: db存储 Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public abstract class DbStore<T> implements IStore<T> {
    public boolean store(T data) throws StoreException{

        // TODO: 16/7/10
        return false;
    }

    public T get() throws StoreException {
        return null;
    }
}
