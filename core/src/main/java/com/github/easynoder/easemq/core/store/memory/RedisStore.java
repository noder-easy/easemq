package com.github.easynoder.easemq.core.store.memory;

import com.github.easynoder.easemq.core.exception.StoreException;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public class RedisStore<T> extends MemoryStore<T> {

    @Override
    public boolean store(T data) throws StoreException {
        return false;
    }

    @Override
    public T get() throws StoreException {
        return null;
    }
}
