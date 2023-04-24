package cn.silver.boot.framework.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 【Response返回状态的枚举类】
 *
 * @author JuniorRay
 * @date 2020-11-12
 */
public enum ResponseEnum {

    /**
     * 操作成功
     **/
    SUCCESS(200, "操作成功"),
    /**
     * 服务端异常
     **/
    ERROR(500, "系统内部错误"),
    /**
     * 对象创建成功
     **/
    CREATED(201, "对象创建成功"),
    /**
     * 请求已经被接受
     **/
    ACCEPTED(202, "请求已经被接受"),
    /**
     * 操作已经执行成功，但是没有返回数据
     **/
    NO_CONTENT(204, "操作已经执行成功，但是没有返回数据"),
    /**
     * 资源已被移除
     **/
    MOVED_PERM(301, "资源已被移除"),
    /**
     * 重定向
     */
    SEE_OTHER(303, "重定向"),
    /**
     * 资源没有被修改
     **/
    NOT_MODIFIED(304, "资源没有被修改"),
    /**
     * 参数列表错误（缺少，格式不匹配
     **/
    BAD_REQUEST(400, "参数列表错误（缺少，格式不匹配）"),
    /**
     * 未授权
     **/
    UNAUTHORIZED(401, "未授权"),
    /**
     * 访问受限，授权过期
     **/
    FORBIDDEN(403, "访问受限，授权过期"),
    /**
     * 资源，服务未找到
     **/
    NOT_FOUND(404, "资源，服务未找到"),
    /**
     * 不允许的http方法
     **/
    BAD_METHOD(405, "不允许的http方法"),
    /**
     * 资源冲突，或者资源被锁
     **/
    CONFLICT(409, "资源冲突，或者资源被锁"),
    /**
     * 不支持的数据，媒体类型
     **/
    UNSUPPORTED_TYPE(415, "不支持的数据，媒体类型"),
    /**
     * 接口未实现
     **/
    NOT_IMPLEMENTED(501, "接口未实现"),

    DATA_ERROR_EXIST(1001, "数据已存在"),
    DATA_ERROR_NOT_FOUND(1002, "数据不存在"),
    DATA_VALIDATED_FAILED (1003,"数据验证失败"),

    /**
     * 重复提交
     **/
    REPEAT_SUBMIT(9999, "不允许重复提交，请稍后再试"),
    SAVE_FAIL(9901, "保存失败"),
    SAVE_SUCCESS(9902, "保存成功"),
    DELETE_FAIL(9903, "删除失败"),
    DELETE_SUCCESS(9904, "删除成功"),

    /**
     * 登录模块
     **/
    LOGIN_SUCCESS(9100, "登录成功"),
    LOGIN_FAIL(9101, "登录失败"),
    LOGOUT_SUCCESS(9102, "退出成功"),
    LOGOUT_FAIL(9103, "退出失败"),
    /**
     * 其他
     **/
    DEMO_MODE(9114, "演示模式，不允许操作"),
    CRON_ERROR(9115, "cron表达式不正确"),
    ABNORMAL_PICTURE_UPLOAD(9116, "上传图片异常，请联系管理员"),
    CAPTCHA_OFF(9117, "验证码未启用");

    private static final Map<Integer, ResponseEnum> ENUM_MAP = new HashMap<Integer, ResponseEnum>();

    static {
        for (ResponseEnum item : values()) {
            ENUM_MAP.put(item.getCode(), item);
        }
    }

    // 成员变量
    private final Integer code;
    private final String message;

    // 构造方法
    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseEnum getModelByKey(Integer code) {
        if (code != null) {
            return ENUM_MAP.get(code);
        }
        return null;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
