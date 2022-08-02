package cn.silver.framework.file.util;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

import java.io.*;
import java.util.Collection;
import java.util.Map;

/**
 * @author Administrator
 */
public class WordExportUtil {
    public static OutputStream generateWord(String templatePath, Map<String, Object> params, String outputFile) throws IOException, XDocReportException {
        //获取Word模板，模板存放路径在项目的resources目录下
        InputStream ins = new FileInputStream(templatePath);
        return generateWord(ins, params, outputFile);
    }

    public static OutputStream generateWord(InputStream template, Map<String, Object> params, String outputFile) throws IOException, XDocReportException {
        //注册xdocreport实例并加载FreeMarker模板引擎
        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(template, TemplateEngineKind.Freemarker);
        //创建xdocreport上下文对象
        IContext context = report.createContext();
        //创建字段元数据
        //Word模板中的表格数据对应的集合类型
        FieldsMetadata fm = report.createFieldsMetadata();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
            if (entry.getValue() instanceof Collection) {
                fm.load(entry.getKey(), entry.getValue().getClass(), true);
            }
        }
        //输出到本地目录
        FileOutputStream out = new FileOutputStream(outputFile);
        report.process(context, out);
        template.close();
        return out;
    }

    public static void main(String[] args) {
//        String path = WordExportUtil.class.getResource("活动纪要导出模板.docx");
    }
}
