package cn.silver.framework.workflow.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.util.Date;

/**
 * 流程任务消息所属用户的操作表。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@Table(name = "flow_message_operation")
@ApiModel(value = "流程任务消息所属用户的操作信息")
public class FlowMessageOperation extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 任务消息Id。
     */
    @ApiModelProperty("流程消息对象")
    @Column(name = "message_id", searchType = SearchType.EQ)
    private String messageId;

    /**
     * 用户登录名。
     */
    @ApiModelProperty("用户登录名")
    @Column(name = "login_name", searchType = SearchType.EQ)
    private String loginName;

    /**
     * 操作类型。
     * 常量值参考FlowMessageOperationType对象。
     */
    @ApiModelProperty("操作类型")
    @Column(name = "operation_type", searchType = SearchType.EQ)
    private String operationType;

    /**
     * 操作时间。
     */
    @ApiModelProperty("操作时间")
    @Column(name = "operation_time", searchType = SearchType.BETWEEN)
    private Date operationTime;
}
