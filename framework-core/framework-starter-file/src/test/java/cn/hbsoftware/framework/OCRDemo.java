package cn.hbsoftware.framework;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class OCRDemo {
    public static void main(String[] args) throws TesseractException {

        ITesseract instance = new Tesseract();

        //如果未将tessdata放在根目录下需要指定绝对路径

        //instance.setDatapath("the absolute path of tessdata");

        //如果需要识别英文之外的语种，需要指定识别语种，并且需要将对应的语言包放进项目中

        instance.setLanguage("chi_sim");

        // 指定识别图片

        File imgDir = new File("E:\\BaiduNetdiskDownload\\2020 IT人才技能图谱（极客时间出品）\\Angular2技能图谱 1.0.png");

        long startTime = System.currentTimeMillis();

        String ocrResult = instance.doOCR(imgDir);

        // 输出识别结果

        System.out.println("OCR Result: \n" + ocrResult + "\n 耗时：" + (System.currentTimeMillis() - startTime) + "ms");

    }

}
