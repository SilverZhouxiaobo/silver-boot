package cn.silver.framework.system.domain;

import cn.silver.framework.core.domain.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Repository;


/**
 * @Description: 附件管理
 * @Author: jeecg-boot
 * @Date: 2022-01-04
 * @Version: V1.0
 */
@Data
@Repository
@ApiModel(value = "sys_accessory对象", description = "附件管理")
public class SysAccessory extends DataEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件系统分片主键")
    private String uploadId;
    /**
     * 附件类型
     */
    @ApiModelProperty(value = "附件类型")
    private String type;

    /**
     * 附件名称
     */
    @ApiModelProperty(value = "附件名称")
    private String name;
    /**
     * 附件名称
     */
    @ApiModelProperty(value = "附件MD5")
    private String md5;
    /**
     * 上下文类型
     */
    @ApiModelProperty(value = "上下文类型")
    private String format;
    /**
     * 文件签名
     */
    @ApiModelProperty(value = "文件签名")
    private String sign;
    /**
     * 存储方式
     */
    @ApiModelProperty(value = "存储方式")
    private String saveMode;
    /**
     * 文件句柄
     */
    @ApiModelProperty(value = "文件句柄")
    private String location;

    @ApiModelProperty(value = "文件URL")
    private String fileUrl;
    /**
     * 文件句柄
     */
    @ApiModelProperty(value = "文件分片数")
    private Integer sliceNum;
    /**
     * 文件句柄
     */
    @ApiModelProperty(value = "文件状态")
    private String status;

    @ApiModelProperty(value = "文件名称")
    private String baseName;

    private transient Integer successChunk;
    private transient String bizPath;
    private transient boolean sliceFlag;
}
