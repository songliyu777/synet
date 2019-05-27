package com.yuyan.lightning.cache.interceptor;

import java.util.Set;

public interface BasicOperation {

    /**
     * Return the cache name(s) associated with the operation.
     */
    Set<String> getCacheNames();
}
