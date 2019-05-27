package com.yuyan.lightning.cache.interceptor;

import org.springframework.lang.Nullable;

public class CachePutOperation extends CacheOperation {

    @Nullable
    private final String unless;


    /**
     * @since 4.3
     */
    public CachePutOperation(CachePutOperation.Builder b) {
        super(b);
        this.unless = b.unless;
    }


    @Nullable
    public String getUnless() {
        return this.unless;
    }


    /**
     * @since 4.3
     */
    public static class Builder extends CacheOperation.Builder {

        @Nullable
        private String unless;

        public void setUnless(String unless) {
            this.unless = unless;
        }

        @Override
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(" | unless='");
            sb.append(this.unless);
            sb.append("'");
            return sb;
        }

        @Override
        public CachePutOperation build() {
            return new CachePutOperation(this);
        }
    }
}
