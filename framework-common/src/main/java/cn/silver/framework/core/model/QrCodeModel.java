package cn.silver.framework.core.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("二维码生成信息")
public class QrCodeModel {
    private String name;
    private String code;
    private String link;
    private String content;

    public QrCodeModel(String code, String name, String link, String content) {
        this.code = code;
        this.name = name;
        this.link = link;
        this.content = content;
    }
}
