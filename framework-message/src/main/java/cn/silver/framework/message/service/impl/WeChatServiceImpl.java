package cn.silver.framework.message.service.impl;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.api.ISysBaseApi;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.message.constant.MessageStatus;
import cn.silver.framework.message.constant.WeChatParames;
import cn.silver.framework.message.service.IWeChatService;
import cn.silver.framework.message.util.WeChatUtil;
import cn.silver.framework.system.domain.SysMessage;
import cn.silver.framework.system.domain.SysMessageConfig;
import cn.silver.framework.system.domain.SysMessageReceiver;
import cn.silver.framework.system.service.ISysMessageConfigService;
import cn.silver.framework.system.service.ISysMessageReceiverService;
import cn.silver.framework.system.service.ISysMessageService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WeChatServiceImpl implements IWeChatService {

    @Autowired
    private ISysBaseApi baseApi;
    @Autowired
    private ISysMessageService messageService;
    @Autowired
    private ISysMessageConfigService configService;
    @Autowired
    private ISysMessageReceiverService receiverService;

    @Cacheable(value = "ACCESS_TOKEN", key = "#key")
    public String getAccessToken(String key) {
        String url = this.baseApi.getConfigByCode("");
        String result = "";
        String requestUrl = WeChatParames.access_token_url.replace("{corpId}", WeChatParames.corpId).replace("{corpsecret}", key);
        JSONObject jsonObject = WeChatUtil.httpSend(requestUrl, null, HttpMethod.GET);
        // 如果请求成功
        if (MapUtils.isNotEmpty(jsonObject)) {
            if (jsonObject.getIntValue("errcode") == 0) {
                result = jsonObject.getString("access_token");
            } else {
                throw new CustomException("获取access_token失败：" + jsonObject.getString("errmsg"), jsonObject.getIntValue("errcode"));
            }
        }
        return result;
    }

    /**
     * @param message void
     * @desc ：0.公共方法：发送消息
     */
    @Override
    public void sendMessage(SysMessage message) {
        JSONObject params = new JSONObject();
        if (StringUtils.isNotBlank(message.getSenderAccount())) {
            params.put("sender", message.getSenderAccount());
        }
        List<SysMessageReceiver> receivers = this.receiverService.selectByMain(message.getId());
        if (CollectionUtils.isEmpty(receivers)) {
            throw new CustomException("接收账号为空，无法发送消息", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        params.put("safe", 0);
        params.put("msgtype", "text");
        JSONObject messageContent = new JSONObject(2);
        messageContent.put("title", message.getTitle());
        messageContent.put("content", message.getSendContent());
        params.put("text", messageContent);
        String secret, link;
        if (StringUtils.isNotBlank(message.getConfig())) {
            SysMessageConfig config = this.configService.selectById(message.getConfig());
            if (config.getOwner()) {
                params.put("agentid", config.getAgentId());
                params.put("external_userid", receivers.stream().map(SysMessageReceiver::getReceiveAccount).collect(Collectors.toList()).toArray(new String[]{}));
            } else {
                params.put("touser", receivers.stream().map(SysMessageReceiver::getReceiveAccount).collect(Collectors.joining("|")));
            }
            secret = config.getAgentSecret();
            link = config.getMessageLink();
        } else {
            secret = WeChatParames.outersecret;
            link = WeChatParames.sendMessage_url;
        }
        // 1.获取access_token:根据企业id和应用密钥获取access_token,并拼接请求url
        String accessToken = getAccessToken(secret);
        String url = link.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = WeChatUtil.httpSend(url, params, HttpMethod.POST);
        message.setSendNum(message.getSendNum() + 1);
        if (MapUtils.isNotEmpty(jsonObject)) {
            if (0 != jsonObject.getIntValue("errcode")) {
                message.setRemark("消息发送失败 errcode:{} errmsg:{}" + jsonObject.getIntValue("errcode") + jsonObject.getString("errmsg"));
                message.setSendStatus(MessageStatus.FAILD.getCode());
            } else {
                message.setSendStatus(MessageStatus.FINISHED.getCode());
            }
            this.messageService.update(message);
        }
    }

}
