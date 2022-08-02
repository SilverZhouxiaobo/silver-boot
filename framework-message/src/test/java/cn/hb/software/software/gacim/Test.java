package cn.hb.software.software.gacim;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Test {
    public static void main(String[] args) {
        String template = "${userName}，您好！\n" +
                "请前待办任务办理事项！${taskName}";
        if (StringUtils.isNotBlank(template) && template.contains("${")) {
            String[] strings = template.split("\\$\\{");
            List<String> result = new ArrayList<>(strings.length - 1);
            for (int i = 1, length = strings.length; i < length; i++) {
                log.info("index:" + i + ",value:" + strings[i]);
                result.add(strings[i].substring(0,strings[i].indexOf("}")));
            }
            log.info(result.toString());

        }
    }
}
