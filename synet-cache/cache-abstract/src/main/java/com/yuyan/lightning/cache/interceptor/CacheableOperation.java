package com.yuyan.lightning.cache.interceptor;

import org.springframework.lang.Nullable;

public class CacheableOperation extends CacheOperation{

    @Nullable
    private final String unless;

    private final boolean sync;


    /**
     * @since 4.3
     */
    public CacheableOperation(CacheableOperation.Builder b) {
        super(b);
        this.unless = b.unless;
        this.sync = b.sync;
    }


    @Nullable
    public String getUnless() {
        return this.unless;
    }

    public boolean isSync() {
        return this.sync;
    }


    /**
     * @since 4.3
     */
    public static class Builder extends CacheOperation.Builder {

        @Nullable
        private String unless;

        private boolean sync;

        public void setUnless(String unless) {
            this.unless = unless;
        }

        public void setSync(boolean sync) {
            this.sync = sync;
        }

        @Override
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(" | unless='");
            sb.append(this.unless);
            sb.append("'");
            sb.append(" | sync='");
            sb.append(this.sync);
            sb.append("'");
            return sb;
        }

        @Override
        public CacheableOperation build() {
            return new CacheableOperation(this);
        }
    }

}
