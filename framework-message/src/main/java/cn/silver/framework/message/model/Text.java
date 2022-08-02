package cn.silver.framework.message.model;

import lombok.Data;

/**
 * 文本
 */
@Data
public class Text {
    //是    消息内容，最长不超过2048个字节
    private String content;
}
