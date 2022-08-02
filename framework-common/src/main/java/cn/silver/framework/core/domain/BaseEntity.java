package cn.silver.framework.core.domain;

import cn.silver.framework.common.utils.id.IdWorker;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity基类
 *
 * @author hb
 */
@Data
@ApiModel(value = "BaseEntity", description = "Entity基类")
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 搜索值
     */
    @ApiModelProperty(value = "搜索值")
    private transient String searchValue;

    /**
     * 开始时间
     */
    @JsonIgnore
    @ApiModelProperty(value = "开始时间")
    private transient String beginTime;
    /**
     * 结束时间
     */
    @JsonIgnore
    @ApiModelProperty(value = "结束时间")
    private transient String endTime;
    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数", hidden = true)
    private transient Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    /**
     * 导出数据时使用的id数组
     */
    public transient List<String> ids;

    /**
     * 插入之前执行方法，需要手动调用
     */
    public void preInsert() {
        if (StringUtils.isBlank(getId())) {
            setId(IdWorker.getIdStr());
        }
    }

    /**
     * 更新之前执行方法，需要手动调用
     */
    public void preUpdate() {

    }

    public boolean checkExists() {
        return false;
    }

    public String getLabel() {
        return "";
    }

    public String getValue() {
        return this.id;
    }

    public String getGroup() {
        return "";
    }

    public String getOrderColumn() {
        return "id";
    }

    public String DictRemark() {
        return "";
    }

    public String getOrderType() {
        return "desc";
    }
}
