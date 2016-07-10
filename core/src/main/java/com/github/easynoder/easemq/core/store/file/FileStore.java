package com.github.easynoder.easemq.core.store.file;

import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.core.store.IStore;

/**
 * Desc: 文件存储 Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public class FileStore<T> implements IStore<T> {
    public boolean store(T data) throws StoreException {

        //todo
        return false;
    }

    public T get() throws StoreException {
        // todo
        return null;
    }
}
