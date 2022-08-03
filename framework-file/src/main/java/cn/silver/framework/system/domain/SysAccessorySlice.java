package cn.silver.framework.system.domain;

import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 附件管理-分片信息
 * @Author: jeecg-boot
 * @Date: 2022-01-04
 * @Version: V1.0
 */
@Data
@ApiModel(value = "sys_accessory对象", description = "附件管理-附件分片信息")
public class SysAccessorySlice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 归属附件
     */
    @ApiModelProperty(value = "归属附件")
    private String accessory;
    /**
     * 分片名称
     */
    @ApiModelProperty(value = "分片序号")
    private Integer chunkNo;
    /**
     * 存放位置
     */
    @ApiModelProperty(value = "存放位置")
    private String location;
}
