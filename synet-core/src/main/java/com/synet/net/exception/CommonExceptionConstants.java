package com.synet.net.exception;

/**
 * 通用异常常量
 *
 * @author konghang
 */
public enum CommonExceptionConstants implements BaseExceptionConstants {
    not_available_data(0,"无效的传入参数"),
    no_find_data(1,"数据不存在"),
    unlogin_error(2, "需要登录后才能访问"),
    no_data_permission(3,"没有数据操作权限"),
    // 上传图片的一些异常
    read_file_error(4,"读取文件错误"),
    beyond_image_max_size(5,"超过文件最大限制"),
    not_image(6,"此文件不是图片文件"),
    image_format_not_support(7,"图片格式不支持"),
    ip_local_error(8,"获取本机IP失败"),
    no_jurisdiction(9,"没有权限操作"),
    no_sys_address_info(10,"没有找到地址编码信息"),

    upload_image_failed(12,"图片文件上传失败"),
    mobile_number_format_error(13, "手机号码格式错误"),
    date_format_err(14,"时间格式不正确"),
    upload_failed(15,"文件上传失败"),
    download_failed(16,"文件下载失败"),
    dir_not_exits(17,"目录不存在"),
    token_expired(18, "token已经过期"),
    token_not_valid(19, "token验证错误"),
    time_format_error(20, "时间格式化错误"),
    data_is_exist(21,"数据已经存在"),
    ip_format_error(22,"ip格式不正确"),
    param_valid_error(23, "传入参数错误"),

    username_password_error(24, "用户名或密码错误"),
    id_generate_error(25, "id generate error")
    ;

    private long code;
    private String message;

    CommonExceptionConstants(long code, String message) {
        this.code = BaseExceptionConstants.StartCode.COMMON_EXCEPTION_START_CODE +code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
