package cn.hbsoftware.framework;


import cn.hbsoftware.framework.pdf.PdfBoxUtil;
import cn.hbsoftware.framework.pdf.PdfFileUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;

public class PdfTest {

    public static String filepath = "F:\\workspace\\PDF";
    public static String fileName = "";
    private static String parsePath = "E:\\BaiduNetdiskDownload\\2020 IT人才技能图谱（极客时间出品）";

    public static void main(String[] args) {
        try {
            File path = new File(parsePath);
            if (path.isDirectory()) {
                for (File file : path.listFiles()) {
                    System.out.println(file.getAbsolutePath());
                    if (file.getName().endsWith("pdf") || file.getName().endsWith("PDF")) {
                        String content = PdfFileUtil.readPdf(file.getAbsolutePath());
                        System.out.println(content);
                        Document document = PdfFileUtil.create(filepath,file.getName());
                        document.open();
                        document.newPage();
                        document.add(new Paragraph(content));
                        document.close();
                    }
                }
            } else {
                if (path.getName().endsWith("pdf") || path.getName().endsWith("PDF")) {
                    PdfBoxUtil.readPDF(parsePath, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
