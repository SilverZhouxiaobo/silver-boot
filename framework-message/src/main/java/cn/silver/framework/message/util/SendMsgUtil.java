package cn.silver.framework.message.util;

//import com.jasson.im.api.APIClient;
//import com.jeesite.modules.msg.entity.content.PcMsgContent;
//import com.jeesite.modules.msg.utils.MsgPushUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 　　* @description: 站内信和短信推送工具类
 * 　　* @author JOJO0826
 * 　　* @date 2019/11/6 11:09
 */
@Slf4j
public class SendMsgUtil {

//    private APIClient handler = new APIClient();
//
//
//    /**
//     * 初始化
//     *
//     * @return
//     */
//    public int init() { return handler.init(host,name,pwd,apiId,dbName); }
//
//    /**
//     * pc消息推送提醒功能
//     *
//     * @param title
//     * @param Content
//     * @param userCode
//     */
//
//    public void pcMsgPush(String title, String Content, String userCode) {
//
//        PcMsgContent msgContent = new PcMsgContent();
//        msgContent.setTitle(title);
//        msgContent.setContent(Content);
//        //msgContent.addButton("办理", "/a/task/execute?id=123");
//        // 即时推送消息
//        MsgPushUtils.push(msgContent, "BizKey", "BizType", userCode);
//    }
//
//    /**
//     * 发送短信方法
//     *
//     * @param mobile
//     * @param content
//     * @param smID
//     * @return
//     */
//    public int sendSM(String mobile, String content, long smID, String enable) throws Exception {
//        if ("y".equals(enable)) {
//            int connectRe = init();
//            if (APIClient.IMAPI_SUCC == connectRe ) {
//                int result = handler.sendSM(mobile, content, smID);
//                log.info("本次短信接收的手机号为:{},短信内容为:{}", mobile, content);
//                log.info("apiId:{} , name:{} , pwd:{} ," +
//                         "host:{} , 是否开启短信:{} , 短信Id:{} , 短信发送结果:{}",
//                        apiId, name, pwd, host, stand, smID, result);
//                return result;
//            } else if (APIClient.IMAPI_CONN_ERR == connectRe) {
////				System.err.println("连接失败");
//                log.error("连接超时，错误代码：-9");
//                throw new Exception("连接超时，错误代码：-9");
////				return -9;
//            } else if (APIClient.IMAPI_API_ERR == connectRe) {
////				System.err.println("apiId不存在");
////				return -7;
//                log.error("apiId不存在，错误代码：-7");
//                throw new Exception("apiId不存在，错误代码：-7");
//            } else {
//                log.error("API接口处于暂停（失效）状态，错误代码：-10");
//                throw new Exception("API接口处于暂停（失效）状态，错误代码：-10");
//            }
//        } else {
//            log.info("smID为{}的短信发送成功",smID);
//            return 0;
//        }
//    }
//
//
//    /**
//     * 发送短信方法(群发)
//     *
//     * @param mobile
//     * @param content
//     * @param smID
//     * @return
//     */
//    public int sendSM(String[] mobile, String content, long smID, String type) throws Exception {
//        if ("y".equals(type)) {
//            int connectRe = init();
//            if (connectRe == APIClient.IMAPI_SUCC) {
//                int result = handler.sendSM(mobile, content, smID);
//                log.info("本次短信接收的手机号为:{},短信内容为:{}", mobile, content);
//                log.info("apiId:{} , name:{} , pwd:{} ," +
//                                "host:{} , 是否开启短信:{} , 短信Id:{} , 短信发送结果:{}",
//                        apiId, name, pwd, host, stand, smID, result);
//                return result;
//            } else if (connectRe == APIClient.IMAPI_CONN_ERR) {
//                log.error("连接超时，错误代码：-9");
//                throw new Exception("连接超时，错误代码：-9");
//            } else if (connectRe == APIClient.IMAPI_API_ERR) {
//                log.error("apiId不存在，错误代码：-7");
//                throw new Exception("apiId不存在，错误代码：-7");
//            } else {
//                log.error("API接口处于暂停（失效）状态，错误代码：-10");
//                throw new Exception("API接口处于暂停（失效）状态，错误代码：-10");
//            }
//        } else {
//            log.info("smID为{}的短信发送成功",smID);
//            return 0;
//        }
//    }
//
//    /**
//     * 释放接口
//     *
//     * @return
//     */
//    public void release() {
//        handler.release();
//    }


//	/**
//	 * 测试
//	 *
//	 * @param aa
//	 */
//
//	  public static void main(String aa[]) {
//		  APIClient handler2 = new
//				  APIClient();
//		  System.out.println("in ... ");
//		  int connectRe =
//				  handler2.init(Global.getConfig("sms_host"), Global.getConfig("sms_name"),
//						  Global.getConfig("sms_pwd"), Global.getConfig("sms_apiId"),
//						  Global.getConfig("sms_dbName"));
//		  if (connectRe == APIClient.IMAPI_SUCC) {
//			  int result = handler2.sendSM("18825150139", "你好", 0);
//			  System.out.println("succ ... " + result);
//		  }
//		  handler2.release();// 释放接口 System.out.println("over ... "); }
//
//	  }
}
