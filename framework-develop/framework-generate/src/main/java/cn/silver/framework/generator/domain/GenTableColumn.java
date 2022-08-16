package cn.silver.framework.generator.domain;

import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.domain.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 代码生成业务字段表 gen_table_column
 *
 * @author hb
 */
@Data
@ApiModel(value = "GenTableColumn", description = "代码生成业务字段表")
public class GenTableColumn extends DataEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 归属表编号
     */
    @ApiModelProperty(value = "归属表编号")
    private String tableId;

    /**
     * 列名称
     */
    @ApiModelProperty(value = "列名称")
    private String columnName;

    /**
     * 列描述
     */
    @ApiModelProperty(value = "列描述")
    private String columnComment;

    /**
     * 列类型
     */
    @ApiModelProperty(value = "列类型")
    private String columnType;

    /**
     * JAVA类型
     */
    @ApiModelProperty(value = "JAVA类型")
    private String javaType;

    /**
     * JAVA字段名
     */
    @NotBlank(message = "Java属性不能为空")
    @ApiModelProperty(value = "JAVA字段名")
    private String javaField;

    /**
     * 是否主键（1是）
     */
    @ApiModelProperty(value = "是否主键")
    private String isPk;

    /**
     * 是否自增（1是）
     */
    @ApiModelProperty(value = "是否自增（1是）")
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    @ApiModelProperty(value = "是否必填（1是）")
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    @ApiModelProperty(value = "是否为插入字段（1是）")
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    @ApiModelProperty(value = "是否编辑字段（1是）")
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    @ApiModelProperty(value = "是否列表字段（1是）")
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    @ApiModelProperty(value = "是否查询字段（1是）")
    private String isQuery;

    /**
     * 查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围）
     */
    @ApiModelProperty(value = "查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围）")
    private String queryType;

    /**
     * 显示类型（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、editor富文本控件）
     */
    @ApiModelProperty(value = "显示类型（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、editor富文本控件）")
    private String htmlType;

    /**
     * 字典类型
     */
    @ApiModelProperty(value = "字典类型")
    private String dictType;


    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;

    public static boolean isSuperColumn(String javaField) {
        return StringUtils.equalsAnyIgnoreCase(javaField,
                // BaseEntity
                "id",
                // DataEntity
                "createBy", "createName", "createTime", "updateBy", "updateName", "updateTime", "remark", "deleted",
                // TreeEntity
                "parentName", "pid", "orderNum", "ancestors");
    }

    public static boolean isUsableColumn(String javaField) {
        // isSuperColumn()中的名单用于避免生成多余Domain属性，若某些属性在生成页面时需要用到不能忽略，则放在此处白名单
        return StringUtils.equalsAnyIgnoreCase(javaField, "pid", "orderNum", "remark");
    }

    public String getIsPk() {
        return isPk;
    }

    public void setIsPk(String isPk) {
        this.isPk = isPk;
    }

    public boolean isPk() {
        return isPk(this.isPk);
    }

    public boolean isPk(String isPk) {
        return isPk != null && StringUtils.equals("1", isPk);
    }

    public String getIsIncrement() {
        return isIncrement;
    }

    public void setIsIncrement(String isIncrement) {
        this.isIncrement = isIncrement;
    }

    public boolean isIncrement() {
        return isIncrement(this.isIncrement);
    }

    public boolean isIncrement(String isIncrement) {
        return isIncrement != null && StringUtils.equals("1", isIncrement);
    }

    public String getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
    }

    public boolean isRequired() {
        return isRequired(this.isRequired);
    }

    public boolean isRequired(String isRequired) {
        return isRequired != null && StringUtils.equals("1", isRequired);
    }

    public String getIsInsert() {
        return isInsert;
    }

    public void setIsInsert(String isInsert) {
        this.isInsert = isInsert;
    }

    public boolean isInsert() {
        return isInsert(this.isInsert);
    }

    public boolean isInsert(String isInsert) {
        return isInsert != null && StringUtils.equals("1", isInsert);
    }

    public boolean isEdit() {
        return isInsert(this.isEdit);
    }

    public boolean isEdit(String isEdit) {
        return isEdit != null && StringUtils.equals("1", isEdit);
    }

    public boolean isList() {
        return isList(this.isList);
    }

    public boolean isList(String isList) {
        return isList != null && StringUtils.equals("1", isList);
    }

    public String getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }

    public String getIsQuery() {
        return isQuery;
    }

    public void setIsQuery(String isQuery) {
        this.isQuery = isQuery;
    }

    public boolean isQuery() {
        return isQuery(this.isQuery);
    }

    public boolean isQuery(String isQuery) {
        return isQuery != null && StringUtils.equals("1", isQuery);
    }

    public String getIsList() {
        return isList;
    }

    public void setIsList(String isList) {
        this.isList = isList;
    }

    public String getHtmlType() {
        return htmlType;
    }

    public void setHtmlType(String htmlType) {
        this.htmlType = htmlType;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public boolean isSuperColumn() {
        return isSuperColumn(this.javaField);
    }

    public boolean isUsableColumn() {
        return isUsableColumn(javaField);
    }

    public String readConverterExp() {
        String remarks = StringUtils.substringBetween(this.columnComment, "（", "）");
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(remarks)) {
            for (String value : remarks.split(" ")) {
                if (StringUtils.isNotEmpty(value)) {
                    Object startStr = value.subSequence(0, 1);
                    String endStr = value.substring(1);
                    sb.append(startStr).append("=").append(endStr).append(",");
                }
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            return this.columnComment;
        }
    }
}
