package cn.silver.framework.core.model;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@ApiModel(value = "ApproveModel", description = "审批数据")
public class ApproveModel {

    @ApiModelProperty("id")
    private transient String id;
    @ApiModelProperty("审批类型")
    private transient String approveType;
    @ApiModelProperty("审批意见")
    private transient String approveOpinion;

    @ApiModelProperty("业务路由")
    private transient String businessUrl;

    @ApiModelProperty("下一级审批人")
    private transient String nextAssignee;

    @ApiModelProperty("审批到期时间")
    private transient Date dueTime;

    @ApiModelProperty("流程参数")
    private transient JSONObject flowParams;

    public ApproveModel(String id, String approveType, String approveOpinion) {
        this.setId(id);
        this.approveType = approveType;
        this.approveOpinion = approveOpinion;
    }
}
