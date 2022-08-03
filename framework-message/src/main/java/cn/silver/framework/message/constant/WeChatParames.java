package cn.silver.framework.message.constant;

public class WeChatParames {

    // 1.微信参数
    // 企业ID
    public final static String corpId = "ww677d953a3a56a195";
    // 企业应用私钥OA
    public final static String corpsecret = "JaxtuHSSZu4VzraTDf3zrEozH1zW9FE8cPzM7vbAi34";
    public final static String outersecret = "YoV6E6HasIUjxj-1jFMKjwIP9LnEW9yWDmLbUP1QsJY";
    // 企业应用的id
    public final static int agentId = 1000002;

    public final static String sendInnerMessage_url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
    public final static String sendMessage_url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_msg_template?access_token=ACCESS_TOKEN";

    public final static String access_token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpId}&corpsecret={corpsecret}";
}
