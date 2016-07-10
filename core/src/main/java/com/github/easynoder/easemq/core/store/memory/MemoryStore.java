package com.github.easynoder.easemq.core.store.memory;

import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.core.store.IStore;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public abstract class MemoryStore<T> implements IStore<T> {
    public abstract boolean store(T data) throws StoreException;

    public abstract T get() throws StoreException;
}
