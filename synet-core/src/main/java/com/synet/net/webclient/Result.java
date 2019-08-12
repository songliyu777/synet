package com.synet.net.webclient;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 返回结果
 *
 * 成功字段和失败字段互斥出现
 *
 * @author konghang
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    ///////////////////成功字段///////////////
    /**
     * 基本数据类型
     */
    private T result;

    /**
     * 当前页返回数据列表
     */
    private List<T> results;

    /**
     * 总数量
     */
    private Long totalCount;

    ///////////////////失败字段///////////////
    /**
     * 错误码
     */
    private Long code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 列表
     *
     * @param results
     * @param <T>
     * @return
     */
    public static <T> Result<T> list(List<T> results) {
        return new Result<T>().setResults(results);
    }

    /**
     * 分页列表
     *
     * @param results
     * @param <T>
     * @return
     */
    public static <T> Result<T> page(List<T> results) {
        return new Result<T>().setResults(results).setTotalCount(new Long(results.size()));
    }

    /**
     * 分页列表
     *
     * @param results
     * @param totalCount
     * @param <T>
     * @return
     */
    public static <T> Result<T> page(List<T> results, Long totalCount) {
        return new Result<T>().setResults(results).setTotalCount(totalCount);
    }

    /**
     * 基本类型
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> result(T data) {
        return new Result<T>().setResult(data);
    }

    /**
     * 错误返回
     *
     * @param code 错误码
     * @param message 错误消息
     * @return
     */
    public static Result error(long code, String message) {
        return new Result().setCode(code).setMessage(message);
    }

    public List<T> getResults() {
        return results;
    }

    public Result<T> setResults(List<T> results) {
        this.results = results;
        return this;
    }

    public Result<T> setResult(T data) {
        this.result = data;
        return this;
    }

    public T getResult() {
        return result;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public Result setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public Long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Result setCode(Long code) {
        this.code = code;
        return this;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Result<?> that = (Result<?>) o;

        if (results != null ? !results.equals(that.results) : that.results != null) {
            return false;
        }
        return !(totalCount != null ? !totalCount.equals(that.totalCount) : that.totalCount != null);

    }

    @Override
    public int hashCode() {
        int result = results != null ? results.hashCode() : 0;
        result = 31 * result + (totalCount != null ? totalCount.hashCode() : 0);
        return result;
    }
}
