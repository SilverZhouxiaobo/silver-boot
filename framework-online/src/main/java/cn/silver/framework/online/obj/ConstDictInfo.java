package cn.silver.framework.online.obj;

import lombok.Data;

import java.util.List;

/**
 * 在线表单常量字典的数据结构。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class ConstDictInfo {

    private List<ConstDictData> dictData;

    @Data
    public static class ConstDictData {
        private String type;
        private Object id;
        private String name;
    }
}
