package cn.hbsoftware.framework;

import lombok.Data;

@Data
public class QuoTaBean {
    //日志id
    private long log_id;
    //识别结果个数
    private int words_result_num;
    //识别结果集
    private WordsResult words_result;

    /**
     * WordsResult 识别具体的内容
     * @author 小帅丶
     *
     */
    @Data
    public static class WordsResult{
        //发票代码
        private String invoice_code;
        //发票号码
        private String invoice_number;
        //发票金额
        private String invoice_rate;
    }
}
