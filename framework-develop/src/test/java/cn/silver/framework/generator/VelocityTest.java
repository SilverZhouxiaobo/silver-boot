package cn.silver.framework.generator;

import cn.silver.framework.common.constant.Constants;
import cn.silver.framework.generator.domain.GenTable;
import cn.silver.framework.generator.util.VelocityInitializer;
import cn.silver.framework.generator.util.VelocityUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.util.List;

@Slf4j
public class VelocityTest {
    private static final String resource = VelocityTest.class.getResource("/").toString();

    public static void main(String[] args) {
        String json = "{\"businessName\":\"info\",\"className\":\"CustomInfo\",\"columns\":[{\"edit\":false,\"id\":\"1518150579041787905\",\"increment\":false,\"insert\":false,\"list\":false,\"pk\":false,\"query\":false,\"required\":false,\"superColumn\":false,\"usableColumn\":false}],\"crud\":false,\"functionAuthor\":\"hb\",\"functionName\":\"投资者信息\",\"id\":\"1518150579041787905\",\"moduleName\":\"system\",\"packageName\":\"cn.hb.software.gacim.system\",\"pkColumn\":{\"$ref\":\"$.columns[0]\"},\"tableComment\":\"投资者信息表\",\"tableName\":\"custom_info\",\"tree\":false}";
        GenTable table = JSON.parseObject(json, GenTable.class);
        VelocityInitializer.initVelocity();
        VelocityContext context = VelocityUtils.prepareContext(table);
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates) {
            template = resource + template;
            log.info("tableName:" + table.getTableName() + ",template:" + template);
//            this.getClass().getResource("/") + template;
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            log.debug(sw.toString());
        }
    }
}
