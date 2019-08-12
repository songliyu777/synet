package com.synet.net.exception;

/**
 * 基础异常常量类
 *
 * @author konghang
 */
public interface BaseExceptionConstants {

    /**
     * 异常编码定义
     */
    interface StartCode{
        /**
         * 通用错误 code开始10000结束于10999
         */
        long COMMON_EXCEPTION_START_CODE = 10000;

        /**
         * Oauth错误 code开始11000结束于11999
         */
        long OAUTH_EXCEPTION_START_CODE = 11000;

        /**
         * 系统用户错误 code开始12000结束于12999
         */
        long SYSUSER_EXCEPTION_START_CODE = 12000;

        /**
         * 前端用户错误 code开始13000结束于13999
         */
        long USER_EXCEPTION_START_CODE = 13000;

        /**
         * 交易错误 code开始13000结束于13999
         */
        long TRADE_EXCEPTION_START_CODE = 14000;

        /**
         * 资源错误 code开始18000结束于18999
         */
        long RESOURCE_EXCEPTION_START_CODE = 18000;

        //100000以前的为公用模块异常码，100000及以后的异常码使用
    }

    /**
     * 获取错误码
     *
     * @return
     */
    long getCode();

    /**
     * 获取错误信息
     *
     * @return
     */
    String getMessage();
}
