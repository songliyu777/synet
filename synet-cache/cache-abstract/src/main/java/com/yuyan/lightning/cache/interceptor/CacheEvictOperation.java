package com.yuyan.lightning.cache.interceptor;


public class CacheEvictOperation extends CacheOperation {

    private final boolean cacheWide;

    private final boolean beforeInvocation;


    /**
     * @since 4.3
     */
    public CacheEvictOperation(CacheEvictOperation.Builder b) {
        super(b);
        this.cacheWide = b.cacheWide;
        this.beforeInvocation = b.beforeInvocation;
    }


    public boolean isCacheWide() {
        return this.cacheWide;
    }

    public boolean isBeforeInvocation() {
        return this.beforeInvocation;
    }


    /**
     * @since 4.3
     */
    public static class Builder extends CacheOperation.Builder {

        private boolean cacheWide = false;

        private boolean beforeInvocation = false;

        public void setCacheWide(boolean cacheWide) {
            this.cacheWide = cacheWide;
        }

        public void setBeforeInvocation(boolean beforeInvocation) {
            this.beforeInvocation = beforeInvocation;
        }

        @Override
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(",");
            sb.append(this.cacheWide);
            sb.append(",");
            sb.append(this.beforeInvocation);
            return sb;
        }

        @Override
        public CacheEvictOperation build() {
            return new CacheEvictOperation(this);
        }
    }

}
