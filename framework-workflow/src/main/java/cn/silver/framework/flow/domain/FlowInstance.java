package cn.silver.framework.flow.domain;

import cn.silver.framework.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
@NoArgsConstructor
@ApiModel("流程实例信息")
public class FlowInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "实例名称")
    private String name;
    @ApiModelProperty(value = "租户信息")
    private String tenantId;
    @ApiModelProperty(value = "流程定义Id")
    private String processDefinitionId;
    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;
    @ApiModelProperty(value = "流程定义编码")
    private String processDefinitionKey;
    @ApiModelProperty(value = "流程定义版本")
    private Integer processDefinitionVersion;
    @ApiModelProperty(value = "流程发布ID")
    private String deploymentId;
    @ApiModelProperty(value = "业务数据ID")
    private String businessKey;
    @ApiModelProperty(value = "业务状态")
    private String businessStatus;
    @ApiModelProperty(value = "描述信息")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date stopTime;

    @ApiModelProperty(value = "启动用户ID")
    private String startUserId;

    @ApiModelProperty(value = "流程状态")
    private Integer revision;

    @ApiModelProperty(value = "是否结束")
    private Boolean finished;

    public FlowInstance(HistoricProcessInstance instance) {
        this.stopTime = instance.getEndTime();
        BeanUtils.copyProperties(instance, this);
    }
}
