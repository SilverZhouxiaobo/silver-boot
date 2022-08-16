package cn.silver.framework.core.model;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ApiModel(value = "ExternalLinkVo", description = "外链生成对象")
public class ExternalLinkModel {
    private static final long serialVersionUID = 1L;
    private String name;
    private String linkUrl;
    private String linkType;
    private String linkParams;
    private Date effectiveTime;
    private Date deadTime;
    private String eventKey;
    private String businessKey;

    public ExternalLinkModel(String eventKey, String businessKey, String name, String url, String type, JSONObject params, Date effectiveTime, Date deadTime) {
        this.name = name;
        this.linkUrl = url;
        this.linkType = type;
        this.eventKey = eventKey;
        this.businessKey = businessKey;
        this.linkParams = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
        this.effectiveTime = ObjectUtils.isNotEmpty(effectiveTime) ? effectiveTime : new Date();
        this.deadTime = ObjectUtils.isNotEmpty(deadTime) ? deadTime : null;
    }
}
