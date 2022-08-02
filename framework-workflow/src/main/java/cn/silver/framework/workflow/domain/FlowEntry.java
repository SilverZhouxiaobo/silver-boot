package cn.silver.framework.workflow.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 流程的实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@ApiModel("流程对象")
@Table(name = "flow_entry")
public class FlowEntry extends DataEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 流程名称。
     */
    @ApiModelProperty(value = "流程名称")
    @Excel(name = "流程定义名称", sort = 2)
    @NotBlank(message = "数据验证失败，流程名称不能为空！")
    @Column(name = "process_definition_name", searchType = SearchType.LIKE, unique = true, nullable = false)
    private String processDefinitionName;

    /**
     * 流程标识Key。
     */
    @Excel(name = "流程标识", sort = 3)
    @ApiModelProperty(value = "流程标识Key")
    @NotBlank(message = "数据验证失败，流程标识Key不能为空！")
    @Column(name = "process_definition_key", searchType = SearchType.LIKE, unique = true, dictable = true, nullable = false)
    private String processDefinitionKey;

    /**
     * 流程分类。
     */
    @Excel(name = "流程分类", sort = 4)
    @ApiModelProperty(value = "流程分类")
    @NotNull(message = "数据验证失败，流程分类不能为空！")
    @Column(name = "category_id", searchType = SearchType.EQ, nullable = false)
    private String categoryId;

    /**
     * 工作流部署的发布主版本Id。
     */
    @Excel(name = "主版本实例", sort = 5)
    @Column(name = "main_entry_publish_id", searchType = SearchType.EQ)
    private String mainEntryPublishId;

    @Excel(name = "主版本号", sort = 5)
    @Column(name = "publish_version", searchType = SearchType.EQ)
    private Integer publishVersion;

    @ApiModelProperty(value = "激活状态")
    private Boolean activeStatus;

    /**
     * 最新发布时间。
     */
    @Column(name = "latest_publish_time")
    @ApiModelProperty(value = "最新发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最新发布时间", sort = 6, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date latestPublishTime;

    /**
     * 流程状态。
     */
    @ApiModelProperty(value = "流程状态")
    @Column(name = "status", searchType = SearchType.EQ)
    @Excel(name = "流程状态", sort = 7, dictType = "flow_state")
    private String status;

    /**
     * 流程定义的xml。
     */
    @Excel(name = "流程定义信息 ", sort = 8)
    @ApiModelProperty(value = "流程定义的xml")
    @Column(name = "bpmn_xml", searchType = SearchType.EQ)
    private String bpmnXml;

    /**
     * 绑定表单类型。
     */
    @ApiModelProperty(value = "绑定表单类型")
    @NotNull(message = "数据验证失败，工作流绑定表单类型不能为空！")
    @Excel(name = "绑定表单类型 ", sort = 9, dictType = "flow_form_type")
    @Column(name = "bind_form_type", searchType = SearchType.EQ)
    private String bindFormType;

    /**
     * 在线表单的页面Id。
     */
    @ApiModelProperty(value = "在线表单的页面Id")
    @Excel(name = "在线表单的页面Id ", sort = 10)
    @Column(name = "page_id", searchType = SearchType.EQ)
    private String pageId;

    /**
     * 在线表单Id。
     */
    @Excel(name = "在线表单Id ", sort = 11)
    @ApiModelProperty(value = "在线表单Id")
    @Column(name = "default_form_id", searchType = SearchType.EQ)
    private String defaultFormId;

    /**
     * 静态表单的缺省路由名称。
     */
    @Excel(name = "静态表单的缺省路由名称 ", sort = 12)
    @ApiModelProperty(value = "在线表单的缺省路由名称")
    @Column(name = "default_router_name", searchType = SearchType.EQ)
    private String defaultRouterName;

    private transient FlowEntryPublish mainFlowEntryPublish;

    private transient FlowCategory flowCategory;

    public boolean checkExists() {
        return true;
    }

    @Override
    public String getLabel() {
        return this.processDefinitionName;
    }

    @Override
    public String getValue() {
        return this.processDefinitionKey;
    }
}
