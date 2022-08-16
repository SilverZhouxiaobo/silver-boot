package cn.silver.framework.online.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ColumnData {

    /**
     * 在线表字段对象。
     */
    private OnlineColumn column;

    /**
     * 字段值。
     */
    private Object columnValue;

    public ColumnData() {

    }
}
