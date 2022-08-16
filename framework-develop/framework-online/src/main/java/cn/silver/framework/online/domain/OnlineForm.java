package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 在线表单实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@Table(name = "online_form")
public class OnlineForm extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @Column(name = "form_id")
    private String formId;

    /**
     * 页面id。
     */
    @ApiModelProperty(value = "页面Id")
    @Column(name = "page_id")
    private String pageId;

    /**
     * 表单编码。
     */
    @ApiModelProperty(value = "表单编码")
    @Column(name = "form_code")
    private String formCode;

    /**
     * 表单名称。
     */
    @ApiModelProperty(value = "表单名称")
    @Column(name = "form_name")
    private String formName;

    /**
     * 表单类别。
     */
    @ApiModelProperty(value = "表单类型")
    @Column(name = "form_kind")
    private Integer formKind;

    /**
     * 表单类型。
     */
    @ApiModelProperty(value = "表单类别")
    @Column(name = "form_type")
    private Integer formType;

    /**
     * 表单主表id。
     */
    @ApiModelProperty(value = "表单主表Id")
    @Column(name = "master_table_id")
    private String masterTableId;

    /**
     * 表单组件JSON。
     */
    @ApiModelProperty(value = "表单组件JSON")
    @Column(name = "widget_json")
    private String widgetJson;

    /**
     * 表单参数JSON。
     */
    @ApiModelProperty(value = "表单参数JSON")
    @Column(name = "params_json")
    private String paramsJson;

    /**
     * 更新时间。
     */
    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * masterTableId 的一对一关联数据对象，数据对应类型为OnlineTable。
     */
    @ApiModelProperty(value = "asterTableId 的一对一关联数据对象")
    private transient OnlineTable onlineTable;

    /**
     * masterTableId 字典关联数据。
     */
    @ApiModelProperty(value = "masterTableId 字典关联数据")
    private transient Map<String, Object> masterTableIdDictMap;

    /**
     * formType 常量字典关联数据。
     */
    @ApiModelProperty(value = "formType 常量字典关联数据")
    private transient Map<String, Object> formTypeDictMap;

    /**
     * 当前表单关联的数据源Id集合。
     */
    @ApiModelProperty(value = "当前表单关联的数据源Id集合")
    private List<String> datasourceIdList;
}

