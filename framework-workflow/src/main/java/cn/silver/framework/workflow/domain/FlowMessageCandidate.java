package cn.silver.framework.workflow.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.util.Collection;

/**
 * 流程任务消息的候选身份实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@ApiModel("流程任务消息的候选身份实体对象")
@Table(name = "flow_message_candicate")
public class FlowMessageCandidate extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 任务消息Id。
     */
    @ApiModelProperty("流程消息对象")
    @Column(name = "message_id", searchType = SearchType.EQ)
    private String messageId;

    /**
     * 候选身份类型。
     */
    @ApiModelProperty("候选身份类型")
    @Column(name = "candidate_type", searchType = SearchType.EQ)
    private String candidateType;

    /**
     * 候选身份Id。
     */
    @ApiModelProperty("候选身份Id")
    @Column(name = "candidate_id", searchType = SearchType.EQ)
    private String candidateId;

    private transient Collection<String> candidateIds;

    public FlowMessageCandidate(String messageId, String candidateType, String candidateId) {
        this.setMessageId(messageId);
        this.setCandidateType(candidateType);
        this.setCandidateId(candidateId);
    }
}
