package cn.silver.framework.workflow.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.repository.ProcessDefinition;

import javax.persistence.Table;
import java.util.Date;

/**
 * 流程发布数据的实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@ApiModel("流程发布信息的对象")
@Table(name = "flow_entry_publish")
public class FlowEntryPublish extends DataEntity {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "流程对象Id")
    @Column(name = "entry_id", searchType = SearchType.EQ)
    private String entryId;

    @ApiModelProperty(value = "流程引擎中的流程发布Id")
    @Column(name = "deploy_id", searchType = SearchType.EQ)
    private String deployId;

    /**
     * 流程引擎中的流程定义Id。
     */
    @ApiModelProperty(value = "流程引擎中的流程定义Id")
    @Column(name = "process_definition_id", searchType = SearchType.EQ)
    private String processDefinitionId;

    /**
     * 发布版本。
     */
    @ApiModelProperty(value = "发布版本")
    @Column(name = "publish_version", searchType = SearchType.EQ)
    private Integer publishVersion;

    /**
     * 激活状态。
     */
    @ApiModelProperty(value = "激活状态")
    @Column(name = "active_status", searchType = SearchType.EQ)
    private Boolean activeStatus;

    /**
     * 是否为主版本。
     */
    @ApiModelProperty(value = "是否为主版本")
    @Column(name = "main_version", searchType = SearchType.EQ)
    private Boolean mainVersion;


    /**
     * 发布时间。
     */
    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "publish_time", searchType = SearchType.BETWEEN)
    private Date publishTime;

    public FlowEntryPublish(String entryId, ProcessDefinition processDefinition, String remark, boolean mainVersion) {
        this.setEntryId(entryId);
        this.setProcessDefinitionId(processDefinition.getId());
        this.setDeployId(processDefinition.getDeploymentId());
        this.setPublishVersion(processDefinition.getVersion());
        this.setActiveStatus(true);
        this.setMainVersion(mainVersion);
        this.setPublishTime(new Date());
        this.setRemark(remark);
    }
}
