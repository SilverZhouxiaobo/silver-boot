package cn.silver.framework.ureport;

import cn.silver.framework.common.utils.ServletUtils;
import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Component
@PropertySource(value = {"classpath:ureport.properties"})
public abstract class FileReportProvider implements ReportProvider {

    private String prefix = "file:";
    private boolean disabled;

    @Getter
    @Setter
    @Value("${ureport.fileStoreDir}")
    private String fileStoreDir;

    @PostConstruct
    public void init() {
        File file = new File(fileStoreDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public InputStream loadReport(String file) {
        HttpServletRequest request = ServletUtils.getRequest();
        if (file.startsWith(prefix)) {
            file = file.substring(prefix.length(), file.length());
        }
        String fullPath = fileStoreDir + "/" + file;
        try {
            return new FileInputStream(fullPath);
        } catch (FileNotFoundException e) {
            throw new ReportException(e);
        }
    }

    public abstract void deleteReport(String file);
//    @Override
//    public void deleteReport(String file) {
//        if(file.startsWith(prefix)){
//            file=file.substring(prefix.length(),file.length());
//        }
//        String fullPath=fileStoreDir+"/"+file;
//        File f=new File(fullPath);
//        if(f.exists()){
//            f.delete();
//        }
//    }

    @Override
    public List<ReportFile> getReportFiles() {
        File file = new File(fileStoreDir);
        List<ReportFile> list = new ArrayList<ReportFile>();
        for (File f : file.listFiles()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(f.lastModified());
            list.add(new ReportFile(f.getName(), calendar.getTime()));
        }
        Collections.sort(list, new Comparator<ReportFile>() {
            @Override
            public int compare(ReportFile f1, ReportFile f2) {
                return f2.getUpdateDate().compareTo(f1.getUpdateDate());
            }
        });
        return list;
    }

    public abstract void saveReport(String file, String content);

//    @Override
//    public void saveReport(String file, String content) {
//        if(file.startsWith(prefix)){
//            file=file.substring(prefix.length(),file.length());
//        }
//        String fullPath=fileStoreDir+"/"+file;
//        FileOutputStream outStream=null;
//        try{
//            outStream=new FileOutputStream(new File(fullPath));
//            IOUtils.write(content, outStream,"utf-8");
//        }catch(Exception ex){
//            throw new ReportException(ex);
//        }finally{
//            if(outStream!=null){
//                try {
//                    outStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    @Override
    public String getName() {
        return "自定义文件系统";
    }

    @Override
    public boolean disabled() {
        return false;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }
}
