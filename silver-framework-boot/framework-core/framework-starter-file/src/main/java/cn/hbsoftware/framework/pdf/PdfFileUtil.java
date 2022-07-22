package cn.hbsoftware.framework.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.*;

public class PdfFileUtil {
    public static Document create(String path, String fileName) throws DocumentException, IOException {
        Document document = new Document();
        if (!fileName.endsWith(".pdf") && !fileName.endsWith(".PDF")) {
            fileName += ".pdf";
        }
        PdfWriter.getInstance(document, new FileOutputStream(path + File.separator + fileName));
        return document;
    }

    public static PdfWriter getWriter(String path, String fileName) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        if (!fileName.endsWith(".pdf") && !fileName.endsWith(".PDF")) {
            fileName += ".pdf";
        }
        return PdfWriter.getInstance(document, new FileOutputStream(path + File.separator + fileName));
    }

    //插入Chunk, Phrase, Paragraph, List
    public static void addC() throws DocumentException {
        Document document = new Document();
        document.add(new Chunk("China"));
        document.add(new Chunk(" "));
        Font font = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.WHITE);
        Chunk id = new Chunk("chinese", font);
        id.setBackground(BaseColor.BLACK, 1f, 0.5f, 1f, 1.5f);
        id.setTextRise(6);
        document.add(id);
        document.add(Chunk.NEWLINE);

        document.add(new Chunk("Japan"));
        document.add(new Chunk(" "));
        Font font2 = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.WHITE);
        Chunk id2 = new Chunk("japanese", font2);
        id2.setBackground(BaseColor.BLACK, 1f, 0.5f, 1f, 1.5f);
        id2.setTextRise(6);
        id2.setUnderline(0.2f, -2f);
        document.add(id2);
        document.add(Chunk.NEWLINE);

        //Phrase对象: a List of Chunks with leading
        document.newPage();
        document.add(new Phrase("Phrase page"));

        Phrase director = new Phrase();
        Chunk name = new Chunk("China");
        name.setUnderline(0.2f, -2f);
        director.add(name);
        director.add(new Chunk(","));
        director.add(new Chunk(" "));
        director.add(new Chunk("chinese"));
        director.setLeading(24);
        document.add(director);

        Phrase director2 = new Phrase();
        Chunk name2 = new Chunk("Japan");
        name2.setUnderline(0.2f, -2f);
        director2.add(name2);
        director2.add(new Chunk(","));
        director2.add(new Chunk(" "));
        director2.add(new Chunk("japanese"));
        director2.setLeading(24);
        document.add(director2);

//Paragraph对象: a Phrase with extra properties and a newline
        document.newPage();
        document.add(new Paragraph("Paragraph page"));

        Paragraph info = new Paragraph();
        info.add(new Chunk("China "));
        info.add(new Chunk("chinese"));
        info.add(Chunk.NEWLINE);
        info.add(new Phrase("Japan "));
        info.add(new Phrase("japanese"));
        document.add(info);

        //List对象: a sequence of Paragraphs called ListItem
        document.newPage();
        List list = new List(List.ORDERED);
        for (int i = 0; i < 10; i++) {
            ListItem item = new ListItem(String.format("%s: %d movies",
                    "country" + (i + 1), (i + 1) * 100), new Font(
                    Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.WHITE));
            List movielist = new List(List.ORDERED, List.ALPHABETICAL);
            movielist.setLowercase(List.LOWERCASE);
            for (int j = 0; j < 5; j++) {
                ListItem movieitem = new ListItem("Title" + (j + 1));
                List directorlist = new List(List.UNORDERED);
                for (int k = 0; k < 3; k++) {
                    directorlist.add(String.format("%s, %s", "Name1" + (k + 1),
                            "Name2" + (k + 1)));
                }
                movieitem.add(directorlist);
                movielist.add(movieitem);
            }
            item.add(movielist);
            list.add(item);
        }
        document.add(list);
    }

    public static void setElement(Document document, String path, String fileName) throws FileNotFoundException, DocumentException {
        //页面大小
        Rectangle rect = new Rectangle(PageSize.B5.rotate());
        //页面背景色
        rect.setBackgroundColor(BaseColor.ORANGE);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path + File.separator + fileName));
        //PDF版本(默认1.4)
        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_2);
        document.addTitle("Title@sample");
        document.addAuthor("Author@rensanning");
        document.addSubject("Subject@iText sample");
        document.addKeywords("Keywords@iText");
        document.addCreator("Creator@iText");
        //页边空白
        document.setMargins(10, 20, 30, 40);
    }

    public static String readPdf(String path,String fileName){
        return readPdf(path + File.separator + fileName);
    }

    public static String readPdf(String fileName){
        String pageContent = "";
        try {
            PdfReader reader = new PdfReader(fileName);
            int pageNum = reader.getNumberOfPages();
            for(int i=1;i<=pageNum;i++){
                pageContent += PdfTextExtractor.getTextFromPage(reader, i);//读取第i页的文档内容
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageContent;
    }
    public static void convertHtmlToPdf(String path, String fileName) throws IOException, DocumentException {
        Document document = new Document(PageSize.LETTER);
        PdfWriter.getInstance(document, new FileOutputStream(path + File.separator + fileName));
        document.open();
        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader("<h1>This is a test!</h1>"));
        document.close();
    }

    public static void setPass(Document document, String path, String fileName) throws DocumentException, FileNotFoundException {
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path + File.separator + fileName));
        // 设置密码为："World"
        writer.setEncryption("Hello".getBytes(), "World".getBytes(),
                PdfWriter.ALLOW_SCREENREADERS,
                PdfWriter.STANDARD_ENCRYPTION_128);
    }

    public static void putImageWatermark(String path, String fileName, String imgPath) throws IOException, DocumentException {
        //添加水印
        //图片水印
        PdfReader reader = new PdfReader(path + File.separator + fileName);
        PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(path + File.separator + fileName));

        Image img = Image.getInstance(imgPath);
        img.setAbsolutePosition(200, 400);
        PdfContentByte under = stamp.getUnderContent(1);
        under.addImage(img);

        stamp.close();
        reader.close();
    }

    /**
     * 添加文字水印
     *
     * @param path     pdf路径
     * @param fileName
     * @throws IOException
     * @throws DocumentException
     */
    public static void putFontWatermark(String path, String fileName) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(path + File.separator + fileName);
        PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(path + File.separator + fileName));

        PdfContentByte over = stamp.getOverContent(2);
        over.beginText();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI,
                BaseFont.EMBEDDED);
        over.setFontAndSize(bf, 18);
        over.setTextMatrix(30, 30);
        over.showTextAligned(Element.ALIGN_LEFT, "DUPLICATE", 230, 430, 45);
        over.endText();

        stamp.close();
        reader.close();
    }

    public static void setBackgroud(String path, String fileName, String imgPath) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(path + File.separator + fileName);
        PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(path + File.separator + fileName));
        //背景图
        Image img2 = Image.getInstance(imgPath);
        img2.setAbsolutePosition(0, 0);
        PdfContentByte under2 = stamp.getUnderContent(3);
        under2.addImage(img2);
        stamp.close();
        reader.close();
    }
}
