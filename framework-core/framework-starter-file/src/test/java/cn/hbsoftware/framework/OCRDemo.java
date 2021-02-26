package cn.hbsoftware.framework;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OCRDemo {
    private static ITesseract instance = new Tesseract();

    private static final String LANG_OPTION = "-l";
    //获取系统换行符
    private static final String EOL = System.getProperty("line.separator");
    //获取项目路径
    private static String Path = System.getProperty("user.dir");
    //tesseract.exe路径
    private static String tessPath = Path+"/src/main/resources/tesseract-ocr/tesseract.exe";

    static {
        //如果未将tessdata放在根目录下需要指定绝对路径
        instance.setDatapath("D:\\Dev\\Tesseract-OCR\\tessdata");
        //如果需要识别英文之外的语种，需要指定识别语种，并且需要将对应的语言包放进项目中
        instance.setLanguage("chi_sim+eng");
    }
    public static void main(String[] args) throws TesseractException {
        // 指定识别图片
        File imgDir = new File("D:\\Dev\\test.png");
        long startTime = System.currentTimeMillis();
        String ocrResult = instance.doOCR(imgDir);
        // 输出识别结果
        System.out.println("OCR Result: \n" + ocrResult + "\n 耗时：" + (System.currentTimeMillis() - startTime) + "ms");
//        String path = "E:\\学习资料\\业务学习\\";
//        String pdfPath = path + "罪证电子档案系统二期项目方案 - 20180628新版-终版方案v4.pdf";
//        String content = parsingPdf(pdfPath);
//
//        FileUtil f = new FileUtil();
//        f.WriteFile(content, path, "罪证电子档案系统二期项目方案.txt", "UTF-8");
//        try {
//            parsingPdfImg(pdfPath, path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * @param imageFile   传入的图像文件
     * @return 识别后的字符串
     */
    public static String recognizeText(File imageFile) throws Exception {
        /**
         * 设置输出文件的保存的文件目录
         */
        File outputFile = new File(imageFile.getParentFile(), "output");
        StringBuffer strB = new StringBuffer();
        List<String> cmd = new ArrayList<String>();
        System.out.println("===========os.name:"+System.getProperties().getProperty("os.name"));
        cmd.add(tessPath);
        cmd.add("");
        cmd.add(outputFile.getName());
        cmd.add(LANG_OPTION);
        cmd.add("chi_sim");
        //cmd.add("eng");
        ProcessBuilder pb = new ProcessBuilder();
        /**
         *设置此流程构建器的工作目录。
         */
        pb.directory(imageFile.getParentFile());
        cmd.set(1, imageFile.getName());
        pb.command(cmd);
        pb.redirectErrorStream(true);
        long startTime = System.currentTimeMillis();
        System.out.println("开始时间：" + startTime);
        Process process = pb.start();
        // tesseract.exe 1.jpg 1 -l chi_sim
        //不习惯使用ProcessBuilder的，也可以使用Runtime，效果一致
        // Runtime.getRuntime().exec("tesseract.exe 1.jpg 1 -l chi_sim");
        /**
         * 流程的退出值。按照惯例，0表示正常
         * 终止.
         */
//        System.out.println(cmd.toString());
        int w = process.waitFor();
        if (w == 0){// 0代表正常退出
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(outputFile.getAbsolutePath() + ".txt"),"UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                if(!"".equals(str)) {
                    strB.append(str.replaceAll("\\s*", "")).append(EOL);
                }
            }
            in.close();
            long endTime = System.currentTimeMillis();
            System.out.println("结束时间：" + endTime);
            System.out.println("耗时：" + (endTime - startTime) + "毫秒");
        } else {
            String msg;
            switch (w) {
                case 1:
                    msg = "访问文件时出错。图片的文件名中可能有空格。";
                    break;
                case 29:
                    msg = "无法识别图像或其所选区域。";
                    break;
                case 31:
                    msg = "不支援的图片格式。";
                    break;
                default:
                    msg = "发生错误。";
            }
            throw new RuntimeException(msg);
        }
        new File(outputFile.getAbsolutePath() + ".txt").delete();
        return strB.toString();
    }


    public static String parsingPdf(String pdfPath) {
        File pdfFile = new File(pdfPath);
        String content = "";
        PDDocument document = null;
        try {
            // 方式一：

            InputStream input = new FileInputStream( pdfFile );
            //加载 pdf 文档
            PDFParser parser = new PDFParser(new RandomAccessBuffer(input));
            parser.parse();
            document = parser.getPDDocument();
            // 方式二：
//            document = PDDocument.load(pdfFile);

            // 获取页码
            int pages = document.getNumberOfPages();

            // 读文本内容
            PDFTextStripper stripper = new PDFTextStripper();
            // 设置按顺序输出
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(pages);
            content = stripper.getText(document);
            System.out.println(content);
        } catch (Exception e) {
            System.out.println(e);
        }
        return content;
    }

    public static void parsingPdfImg(String pdfPath, String outImgPath) throws IOException, TesseractException {
        // 待解析PDF
        File pdfFile = new File(pdfPath);
        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pages_size = document.getNumberOfPages();

        System.out.println("getAllPages===============" + pages_size);
        int j = 0;
        for (int i = 0; i < pages_size; i++) {
            // 读文本内容
            PDFTextStripper stripper = new PDFTextStripper();
            // 设置按顺序输出
            stripper.setSortByPosition(true);
            stripper.setStartPage(i+1);
            stripper.setEndPage(i+1);
            String content = stripper.getText(document);
            System.out.println(content);
            PDPage page = document.getPage(i);
            PDResources resources = page.getResources();
            Iterable xobjects = resources.getXObjectNames();

            if (xobjects != null) {
                Iterator imageIter = xobjects.iterator();
                while (imageIter.hasNext()) {
                    COSName key = (COSName) imageIter.next();
                    if (resources.isImageXObject(key)) {
                        try {
                            PDImageXObject image = (PDImageXObject) resources.getXObject(key);
                            //将PDF文档中的图片 分别另存为图片。
                            File file = new File(outImgPath + "img_" + j + ".png");
                            System.out.println(instance.doOCR(file));;
                            BufferedImage input = image.getOpaqueImage();
                            ImageIO.write(input, "png", file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        j++;
                    }
                }
            }
        }
        try{
            document.close();
        }catch (Exception e){
            System.out.println(e);
        }
        System.out.println(j);
    }

    public static void createBlankPdf(String outPdfPath) {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);
            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(font, 14);
//            contentStream.moveTextPositionByAmount(100, 700);
//            contentStream.drawString("Hello World");
//            contentStream.drawString("中文");
            contentStream.endText();
            contentStream.close();
            document.save(outPdfPath);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
