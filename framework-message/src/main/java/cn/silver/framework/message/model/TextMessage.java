package cn.silver.framework.message.model;

import lombok.Data;

/**
 * 文本消息
 */
@Data
public class TextMessage extends BaseMessage {
    // 文本
    private Text text;
    // 否 表示是否是保密消息，0表示否，1表示是，默认0
    private int safe;
}
