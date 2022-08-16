package cn.silver.framework.system.constant;

import cn.silver.framework.core.constant.BaseContant;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum HandleType implements BaseContant {
    /**
     * 添加评论
     */
    HANDLE_COMMENT("00", "添加评论"),
    /**
     * 添加收藏
     */
    HANDLE_COLLECT_ON("01", "添加收藏"),
    /**
     * 取消收藏
     */
    HANDLE_COLLECT_OFF("11", "取消收藏"),
    /**
     * 点赞
     */
    HANDLE_LIKE_ON("02", "点赞"),
    /**
     * 取消点赞
     */
    HANDLE_LIKE_OFF("12", "取消点赞"),
    /**
     * 添加关注
     */
    HANDLE_FOCUS_ON("03", "添加关注"),
    /**
     * 取消关注
     */
    HANDLE_FOCUS_OFF("13", "取消关注");
    private String code;
    private String name;

    public static HandleType getType(String handleType) {
        HandleType result = null;
        for (HandleType current : HandleType.values()) {
            if (current.code.equals(handleType)) {
                result = current;
                break;
            }
        }
        return result;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }
}
