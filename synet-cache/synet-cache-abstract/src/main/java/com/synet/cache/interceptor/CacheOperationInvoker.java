package com.synet.cache.interceptor;

@FunctionalInterface
public interface CacheOperationInvoker {

    Object invoke() throws CacheOperationInvoker.ThrowableWrapper;


    /**
     * Wrap any exception thrown while invoking {@link #invoke()}.
     */
    @SuppressWarnings("serial")
    class ThrowableWrapper extends RuntimeException {

        private final Throwable original;

        public ThrowableWrapper(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }
}
