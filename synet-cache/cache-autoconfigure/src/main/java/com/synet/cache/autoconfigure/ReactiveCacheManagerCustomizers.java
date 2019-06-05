package com.synet.cache.autoconfigure;

import com.synet.cache.ReactiveCacheManager;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.util.LambdaSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReactiveCacheManagerCustomizers {

    private final List<ReactiveCacheManagerCustomizer<?>> customizers;

    public ReactiveCacheManagerCustomizers(
            List<? extends ReactiveCacheManagerCustomizer<?>> customizers) {
        this.customizers = (customizers != null) ? new ArrayList<>(customizers)
                : Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T extends ReactiveCacheManager> T customize(T cacheManager) {
        LambdaSafe.callbacks(ReactiveCacheManagerCustomizer.class, this.customizers, cacheManager)
                .withLogger(CacheManagerCustomizers.class)
                .invoke((customizer) -> customizer.customize(cacheManager));
        return cacheManager;
    }
}
