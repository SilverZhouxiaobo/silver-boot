package cn.silver.framework.generator.domain;

import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.common.utils.id.IdWorker;
import cn.silver.framework.core.domain.DataEntity;
import cn.silver.framework.db.annotation.GenConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 业务表 gen_table
 *
 * @author hb
 */
@Data
@Slf4j
@ApiModel(value = "GenTable", description = "业务表 gen_table")
public class GenTable extends DataEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 表名称
     */
    @NotBlank(message = "表名称不能为空")
    @ApiModelProperty(value = "表名称")
    private String tableName;

    /**
     * 表描述
     */
    @NotBlank(message = "表描述不能为空")
    @ApiModelProperty(value = "表描述")
    private String tableComment;

    /**
     * 关联父表的表名
     */
    @ApiModelProperty(value = "关联父表的表名")
    private String subTableName;

    /**
     * 本表关联父表的外键名
     */
    @ApiModelProperty(value = "本表关联父表的外键名")
    private String subTableFkName;

    /**
     * 实体类名称(首字母大写)
     */
    @NotBlank(message = "实体类名称不能为空")
    @ApiModelProperty(value = "实体类名称(首字母大写)")
    private String className;

    /**
     * 使用的模板（crud单表操作 tree树表操作）
     */
    @ApiModelProperty(value = "使用的模板（crud单表操作 tree树表操作）")
    private String tplCategory;

    /**
     * 生成包路径
     */
    @ApiModelProperty(value = "生成包路径")
    @NotBlank(message = "生成包路径不能为空")
    private String packageName;

    /**
     * 生成模块名
     */
    @NotBlank(message = "生成模块名不能为空")
    @ApiModelProperty(value = "生成模块名")
    private String moduleName;

    /**
     * 生成业务名
     */
    @NotBlank(message = "生成业务名不能为空")
    @ApiModelProperty(value = "生成业务名不能为空")
    private String businessName;

    /**
     * 生成功能名
     */
    @NotBlank(message = "生成功能名不能为空")
    @ApiModelProperty(value = "生成功能名")
    private String functionName;

    /**
     * 生成作者
     */
    @NotBlank(message = "作者不能为空")
    @ApiModelProperty(value = "生成作者")
    private String functionAuthor;

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    @ApiModelProperty(value = "生成代码方式（0zip压缩包 1自定义路径）")
    private String genType;

    /**
     * 生成路径（不填默认项目路径）
     */
    @ApiModelProperty(value = "生成路径（不填默认项目路径）")
    private String genPath;

    /**
     * 主键信息
     */
    @ApiModelProperty(value = "主键信息")
    private GenTableColumn pkColumn;

    /**
     * 子表信息
     */
    private GenTable subTable;

    /**
     * 表列信息
     */
    @Valid
    @ApiModelProperty(value = "表列信息")
    private List<GenTableColumn> columns;

    /**
     * 其它生成选项
     */
    @ApiModelProperty(value = "其它生成选项")
    private String options;

    /**
     * 树编码字段
     */
    @ApiModelProperty(value = "树编码字段")
    private String treeCode;

    /**
     * 树父编码字段
     */
    @ApiModelProperty(value = "树父编码字段")
    private String treeParentCode;

    /**
     * 树名称字段
     */
    @ApiModelProperty(value = "树名称字段")
    private String treeName;

    /**
     * 上级菜单ID字段
     */
    @ApiModelProperty(value = "上级菜单ID字段")
    private String parentMenuId;

    /**
     * 上级菜单名称字段
     */
    @ApiModelProperty(value = "上级菜单名称字段")
    private String parentMenuName;

    private String tableId = IdWorker.getIdStr();
    private String searchId = IdWorker.getIdStr();
    private String insertId = IdWorker.getIdStr();
    private String updateId = IdWorker.getIdStr();
    private String deleteId = IdWorker.getIdStr();
    private String importId = IdWorker.getIdStr();
    private String exportId = IdWorker.getIdStr();

    public static boolean isTree(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_TREE, tplCategory);
    }

    public static boolean isCrud(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_CRUD, tplCategory);
    }

    public static boolean isData(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_DATA, tplCategory);
    }

    public static boolean isSuperColumn(String tplCategory, String javaField) {
        List<String> superColumns = new ArrayList<>();
        switch (tplCategory) {
            case GenConstants.TPL_TREE:
                superColumns.addAll(Arrays.asList(GenConstants.TREE_ENTITY));
            case GenConstants.TPL_DATA:
                superColumns.addAll(Arrays.asList(GenConstants.DATA_ENTITY));
            default:
                superColumns.addAll(Arrays.asList(GenConstants.BASE_ENTITY));
        }
        return StringUtils.equalsAnyIgnoreCase(javaField, superColumns.toArray(new String[]{}));
    }

    public boolean isTree() {
        return isTree(this.tplCategory);
    }

    public boolean isData() {
        return isData(this.tplCategory);
    }

    public boolean isCrud() {
        return isCrud(this.tplCategory);
    }

    public boolean isSuperColumn(String javaField) {
        return isSuperColumn(this.tplCategory, javaField);
    }
}