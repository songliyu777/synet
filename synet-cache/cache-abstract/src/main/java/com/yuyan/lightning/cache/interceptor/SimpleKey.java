package com.yuyan.lightning.cache.interceptor;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

public class SimpleKey implements Serializable {

    /** An empty key. */
    public static final SimpleKey EMPTY = new SimpleKey();


    private final Object[] params;

    private final int hashCode;


    /**
     * Create a new {@link org.springframework.cache.interceptor.SimpleKey} instance.
     * @param elements the elements of the key
     */
    public SimpleKey(Object... elements) {
        Assert.notNull(elements, "Elements must not be null");
        this.params = new Object[elements.length];
        System.arraycopy(elements, 0, this.params, 0, elements.length);
        this.hashCode = Arrays.deepHashCode(this.params);
    }


    @Override
    public boolean equals(Object other) {
        return (this == other ||
                (other instanceof SimpleKey && Arrays.deepEquals(this.params, ((SimpleKey) other).params)));
    }

    @Override
    public final int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + StringUtils.arrayToCommaDelimitedString(this.params) + "]";
    }

}
