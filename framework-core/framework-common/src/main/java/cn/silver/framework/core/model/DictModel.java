package cn.silver.framework.core.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@ApiModel(value = "DictModel", description = "字典对象")
public class DictModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String label;
    private String value;

    private String group;
    private String cssClass;
    private String listClass;
    private Integer sort;
    private String remark;

    public DictModel(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public DictModel(String label, String value, Integer sort) {
        this(label, value);
        this.sort = sort;
    }

    public DictModel(String id, String label, String value, String group, String remark) {
        this(label, value);
        this.id = id;
        this.group = group;
        this.remark = remark;
    }

    public DictModel(String label, String value, Integer sort, String cssClass, String listClass, String remark) {
        this(label, value, sort);
        this.remark = remark;
        this.cssClass = cssClass;
        this.listClass = listClass;
    }
}
