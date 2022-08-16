package cn.silver.framework.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "DeptModel", description = "部门数据对象")
public class DeptModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("部门主键")
    private String id;
    @ApiModelProperty("部门名称")
    private String deptName;
}
