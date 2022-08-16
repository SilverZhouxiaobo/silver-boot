package cn.silver.framework.system.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "模板导出通用参数")
public class BaseTempExportDTO {

    @NotBlank(message = "模板代码不能为空")
    @ApiModelProperty(value = "模板代码")
    private String code;
    @ApiModelProperty(value = "单号")
    private String orderNo;
    @ApiModelProperty(value = "开始时间")
    private Date beginTime;
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "数组参数")
    private List<String> extraParams;
}
