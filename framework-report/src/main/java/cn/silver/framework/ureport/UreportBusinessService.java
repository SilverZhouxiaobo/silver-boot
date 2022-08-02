package cn.silver.framework.ureport;

import cn.silver.framework.system.domain.SysReport;
import cn.silver.framework.system.service.ISysReportService;

import com.bstek.ureport.exception.ReportException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class UreportBusinessService extends FileReportProvider {
    @Autowired
    private ISysReportService tempService;

    @Override
    public void deleteReport(String file) {
        if (file.startsWith(getPrefix())) {
            file = file.substring(getPrefix().length());
        }
        String fullPath = getFileStoreDir() + "/" + file;

        //删除数据库记录
        SysReport template = tempService.selectSignleByFullPath(fullPath);
        if (template != null) {
            tempService.delete(template.getId());
        }

        File f = new File(fullPath);
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    public void saveReport(String file, String content) {
        if (file.startsWith(getPrefix())) {
            file = file.substring(getPrefix().length(), file.length());
        }
        String fullPath = getFileStoreDir() + "/" + file;
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(new File(fullPath));
            IOUtils.write(content, outStream, "utf-8");
            //判断模板是否已存在---根据模板fullPath查询
            SysReport template = tempService.selectSignleByFullPath(fullPath);
            if (template != null) {
                //更新模板数据库
                template.setRemark(content);
                template.setUpdateTime(new Date());
                tempService.update(template);
            } else {
                //将模板数据保存在数据库中
                template = new SysReport();
                template.setRemark(content);
                template.setFileStoreDir(getFileStoreDir());
                template.setFullPath(fullPath);
                template.setName(file.replaceAll("\\.ureport\\.xml", ""));
                template.setPrefix(getPrefix());
                template.setTempFileName(file);
                template.setCreateTime(new Date());
                tempService.insert(template);
            }
        } catch (Exception ex) {
            throw new ReportException(ex);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
