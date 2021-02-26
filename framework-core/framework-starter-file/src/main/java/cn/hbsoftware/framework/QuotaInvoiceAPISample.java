package cn.hbsoftware.framework;

import cn.hbsoftware.framework.util.Base64Util;
import cn.hbsoftware.framework.util.FileReadUtil;
import cn.hbsoftware.framework.util.HttpUtil;
import com.alibaba.fastjson.JSON;

import java.net.URLEncoder;

public class QuotaInvoiceAPISample {
    //接口地址
    public static String QUOTAINVOICE_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/quota_invoice";
    //根据应用APIKEY SECRETKEY 获取的accesstoken 文档 https://ai.baidu.com/docs#/Auth/75d80ed1
    public static String ACCESSTOKEN = "";

    public static void main(String[] args) {
        //返回字符串
//		String result=getQuoTaText("G:/testimg/quota.jpg");
        //返回bean对象 方便取值
        QuoTaBean bean = getQuoTaBean("E:\\BaiduNetdiskDownload\\png\\img.png");
        System.out.println("发票代码==>"+bean.getWords_result().getInvoice_code());
        System.out.println("发票号码==>"+bean.getWords_result().getInvoice_number());
        System.out.println("发票金额==>"+bean.getWords_result().getInvoice_rate());
    }

    /**
     * 定额发票识别 通过图片获取手写文字内容
     * 返回字符串
     * @param filePath 图片文件目录
     * @return text
     */
    public  static String getQuoTaText(String filePath){
        String result = "";
        try {
            byte[] imgData = FileReadUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            //识别图片上的手写文字
            String params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
            result = HttpUtil.post(QUOTAINVOICE_URL,ACCESSTOKEN, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 定额发票识别 通过图片获取手写文字内容
     * 返回对象
     * @param filePath 图片文件目录
     * @return QuoTaBean
     */
    public static QuoTaBean getQuoTaBean(String filePath){
        String result = getQuoTaText(filePath);
        QuoTaBean bean = JSON.parseObject(result,QuoTaBean.class);
        return bean;
    }
}
