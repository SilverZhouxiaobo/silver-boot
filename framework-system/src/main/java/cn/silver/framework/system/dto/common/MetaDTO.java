package cn.silver.framework.system.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 路由显示信息 集成swagger
 *
 * @author JuniorRay
 */
@Data
@ApiModel(value = "MetaDTO", description = "路由显示信息Vo")
public class MetaDTO {
    private static final long serialVersionUID = 1L;

    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    @ApiModelProperty(value = "设置该路由在侧边栏和面包屑中展示的名字")
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    @ApiModelProperty(value = "设置该路由的图标，对应路径src/assets/icons/svg")
    private String icon;

    /**
     * 设置为true，则不会被 <keep-alive>缓存
     */
    @ApiModelProperty(value = "设置为true，则不会被 <keep-alive>缓存")
    private boolean noCache;

    public MetaDTO() {
    }

    public MetaDTO(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public MetaDTO(String title, String icon, boolean noCache) {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
    }
}
