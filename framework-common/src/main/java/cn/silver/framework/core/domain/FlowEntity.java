package cn.silver.framework.core.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Data
@NoArgsConstructor
@ApiModel(value = "FlowEntity", description = "Entity树基类")
public class FlowEntity extends DataEntity {
    private static final long serialVersionUID = 1L;
    @Excel(name = "编码", sort = 2)
    @ApiModelProperty(value = "编码")
    @Column(name = "code", searchType = SearchType.LIKE)
    private String code;

    @Excel(name = "当前状态", sort = 9)
    @ApiModelProperty(value = "当前状态")
    @Column(name = "status", searchType = SearchType.EQ)
    private String status;

    @ApiModelProperty("是否草稿")
    private transient Boolean draftFlag;

    @ApiModelProperty("当前用户Id")
    private transient String currUser;

    @ApiModelProperty("操作类型")
    private transient String handleType;

    @ApiModelProperty("审批类型")
    private transient String approveType;

    @ApiModelProperty("审批意见")
    private transient String approveOpinion;

    @ApiModelProperty("业务路由")
    private transient String businessUrl;

    @ApiModelProperty("下一级审批人")
    private transient String nextAssignee;

    private transient Date dueTime;
    @ApiModelProperty("流程参数")
    private transient JSONObject flowParams;

    public boolean createOrder() {
        return true;
    }

    /**
     * 插入之前执行方法，需要手动调用
     */
    @Override
    public void preInsert() {
        super.preInsert();
        if (StringUtils.isBlank(this.getStatus())) {
            this.setStatus("draft");
        }
    }

    public String getFlowCode() {
        return "";
    }

    public String getInitTaskKey() {
        return "initiator";
    }

    public String getInitTaskName() {
        return "";
    }
}
