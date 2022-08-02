package cn.silver.framework.core.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.security.util.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import tk.mybatis.mapper.annotation.LogicDelete;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@ApiModel(value = "DataEntity", description = "Entity业务基类")
public class DataEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    @Column(name = "create_by", updatable = false)
    private String createBy;

    @ApiModelProperty(value = "创建者名称")
    @Excel(name = "创建人", type = Excel.Type.EXPORT)
    @Column(name = "create_name", updatable = false)
    private String createName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    @Excel(name = "创建时间", type = Excel.Type.EXPORT, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新人名称")
    @Excel(name = "更新人", type = Excel.Type.EXPORT)
    private String updateName;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    @Excel(name = "最后更新时间", type = Excel.Type.EXPORT, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Excel(name = "描述信息", type = Excel.Type.ALL)
    private String remark;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @LogicDelete
    @ApiModelProperty(value = "删除标志（0代表存在 1代表删除）")
    private String deleted;

    @Override
    public String getOrderColumn() {
        return "update_time";
    }

    @Override
    public String DictRemark() {
        return this.remark;
    }

    /**
     * 插入之前执行方法，需要手动调用
     */
    @Override
    public void preInsert() {
        super.preInsert();
        setDeleted("0");
        LoginUser user = SecurityUtils.getLoginUser();
        if (user != null) {
            this.setCreateBy(user.getId());
            this.setCreateName(user.getNickName());
            this.setUpdateBy(user.getId());
            this.setUpdateName(user.getNickName());
        }
        this.setUpdateTime(new Date());
        this.setCreateTime(new Date());
    }

    /**
     * 更新之前执行方法，需要手动调用
     */
    @Override
    public void preUpdate() {
        super.preUpdate();
        this.setUpdateTime(new Date());
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            LoginUser user = context.getAuthentication() != null ? (LoginUser) context.getAuthentication().getPrincipal() : null;
            if (user != null) {
                this.setUpdateBy(user.getId());
                this.setUpdateName(user.getNickName());
            }
        }
    }
}
