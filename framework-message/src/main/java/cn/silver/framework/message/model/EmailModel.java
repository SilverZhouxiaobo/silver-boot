package cn.silver.framework.message.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author zhoux
 */
@Data
@NoArgsConstructor
@ApiModel(value = "EmailModel", description = "电子邮件对象")
public class EmailModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;
    @NotBlank(message = "收件地址不能为空")
    @ApiModelProperty("接收方邮件")
    private String[] email;
    @ApiModelProperty("邮件主题")
    private String subject;
    @ApiModelProperty("邮件内容")
    private String content;
    @ApiModelProperty("模板")
    private String template;
    @ApiModelProperty("自定义参数")
    private HashMap<String, String> kvMap;

}
