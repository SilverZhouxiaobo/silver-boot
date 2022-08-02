package cn.hb.software;

import cn.silver.framework.file.util.WordExportUtil;
import fr.opensagres.xdocreport.core.XDocReportException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordExportTest {

    private static final String tempPath = "E:\\workspace\\company\\framework\\trunk\\HB-Gacim-Vue\\gacim-file\\src\\test\\resources\\event-report.docx";
    private static final String target = "E:\\workspace\\private\\silver-framework\\silver-framework\\framework-report\\src\\main\\resources\\投资者开放日活动.docx";

    public static void main(String[] args) {
        Map<String, Object> param = new HashMap<>();
        param.put("eventName", "投资者开放日活动");
        param.put("eventDate", "2015年7月10日");
        param.put("eventWeek", "星期五");
        param.put("address", "广汽丰田有限公司");
        List<Map<String, String>> agents = new ArrayList<>();
        Map<String, String> agent = new HashMap<>();
        agent.put("orgName", "广汽集团");
        agent.put("name", "袁锋、吴晓琳");
        agents.add(agent);
        agent = new HashMap<>();
        agent.put("orgName", "广汽丰田");
        agent.put("name", "郭百迅、蔡智湘、袁培博");
        agents.add(agent);
        param.put("agents", agents);
        try {
            WordExportUtil.generateWord(tempPath, param, target);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
    }
}
