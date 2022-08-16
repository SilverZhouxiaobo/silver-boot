package cn.silver.framework.core.model;

import cn.silver.framework.core.domain.TreeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 *
 * @author hb
 */
@Data
@ApiModel(value = "TreeSelect", description = "Treeselect树结构实体类")
public class TreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    @ApiModelProperty(value = "节点ID")
    private String id;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String label;

    @ApiModelProperty(value = "节点值")
    private String value;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(value = "子节点")
    private List<TreeSelect> children;

    public TreeSelect() {

    }

    public TreeSelect(TreeEntity entity) {
        this.id = entity.getId();
        this.label = entity.getLabel();
        this.value = entity.getValue();
        this.children = entity.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }
}
