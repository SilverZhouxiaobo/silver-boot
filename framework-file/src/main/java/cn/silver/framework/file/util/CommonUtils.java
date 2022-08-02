package cn.silver.framework.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.silver.framework.file.config.FileConfig;
import cn.silver.framework.file.constant.ContentTypeConstant;
import cn.silver.framework.file.constant.FileUploadType;
import cn.silver.framework.file.minio.MinioUtil;
import cn.silver.framework.file.oss.OssBootUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CommonUtils {

    private CommonUtils() {
    }

    /**
     * 判断文件名是否带盘符，重新处理
     *
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName) {
        //判断是否带有盘符信息
        // Check for Unix-style path
        int unixSep = fileName.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = fileName.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1) {
            // Any sort of path separator found...
            fileName = fileName.substring(pos + 1);
        }
        //替换上传文件名字的特殊字符
        fileName = fileName.replace("=", "").replace(",", "").replace("&", "").replace("#", "");
        //替换上传文件名字中的空格
        fileName = fileName.replaceAll("\\s", "");
        return fileName;
    }

    /**
     * 获取分片上传id
     *
     * @param uploadType  上传方式
     * @param objectName  对象名称
     * @param contentType 上下文类型
     * @return
     */
    public static String getUploadId(String uploadType, String objectName, String contentType) {
        String uploadId = "";
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            uploadId = MinioUtil.getUploadId(objectName, contentType);
        } else {
            uploadId = OssBootUtil.getUploadId(objectName, contentType);
        }
        return uploadId;
    }

    /**
     * 统一全局上传
     *
     * @Return: java.lang.String
     */
    public static String upload(File filePath, String bizPath, String uploadType) {
        String url = "";
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            url = MinioUtil.upload(getMultipartFile(filePath, "file"), bizPath);
        } else {
            url = OssBootUtil.upload(getMultipartFile(filePath, "file"), bizPath);
        }
        return url;
    }

    /**
     * 统一全局上传
     *
     * @Return: java.lang.String
     */
    public static String upload(MultipartFile file, String bizPath, String uploadType) {
        String url = "";
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            url = MinioUtil.upload(file, bizPath);
        } else {
            url = OssBootUtil.upload(file, bizPath);
        }
        return url;
    }

    /**
     * 统一全局上传 带桶
     *
     * @Return: java.lang.String
     */
    public static String upload(MultipartFile file, String bizPath, String uploadType, String customBucket) {
        String url = "";
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            url = MinioUtil.upload(file, bizPath, customBucket);
        } else {
            url = OssBootUtil.upload(file, bizPath, customBucket);
        }
        return url;
    }

    @SneakyThrows
    public static String upload(InputStream stream, String contentType, String objectName, String uploadType) {
        String url = "";
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            url = MinioUtil.upload(stream, contentType, objectName, MinioUtil.getBucketName());
        } else {
            url = OssBootUtil.upload(stream, contentType, objectName);
        }
        return url;
    }

    /**
     * 分片上传文件服务器
     *
     * @param uploadType
     * @param objectName
     * @param file
     * @param uploadId
     * @param partNum
     * @return
     */
    @SneakyThrows
    public static String uploadPart(String uploadType, String objectName, MultipartFile file, String uploadId, Integer partNum) {
        String url = "";
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            url = MinioUtil.uploadPart(file, objectName, uploadId, partNum);
        } else {
            url = OssBootUtil.uploadPart(file, objectName, uploadId, partNum);
        }
        return url;
    }

    @SneakyThrows
    public static String uploadPartLocal(String bizPath, MultipartFile file, String md5, Integer partNum) {
        String path = FileConfig.uploadPath + File.separator + bizPath + File.separator + md5;
        File dirfile = new File(path);
        if (!dirfile.exists()) {
            //目录不存在，创建目录
            dirfile.mkdirs();
        }
        String chunkName = partNum + ".tmp";
        String filePath = path + File.separator + chunkName;
        File savefile = new File(filePath);
        if (!savefile.exists()) {
            //文件不存在，则创建
            savefile.createNewFile();
        }
        //将文件保存
        file.transferTo(savefile);
        return bizPath + File.separator + md5 + File.separator + chunkName;
    }

    /**
     * 合并文件分片
     *
     * @param uploadType
     * @param objectName
     * @param uploadId
     * @param parts
     * @return
     */
    public static String mergeFile(String uploadType, String objectName, String uploadId, List<String> parts) {
        String url = "";
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            url = MinioUtil.mergeFile(objectName, uploadId, parts);
        } else {
            url = OssBootUtil.mergeFile(objectName, uploadId, parts);
        }
        return url;
    }

    public static String mergeFileLocal(String location, String[] parts) {
        String path = FileConfig.uploadPath + File.separator + location;
        //合成后的文件
        File target = new File(path);
        if (!target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            fileOutputStream = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            for (String slice : parts) {
                File file = new File(FileConfig.uploadPath + File.separator + slice);
                inputStream = new FileInputStream(file);
                int len = 0;
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
                inputStream.close();
                FileUtil.del(file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public static String getSign(String uploadType, String objectName) {
        String sign = "";
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            sign = MinioUtil.getSign(objectName);
        } else {
            sign = OssBootUtil.getSign(objectName);
        }
        return sign;
    }

    /**
     * 本地文件上传
     *
     * @param bizPath 自定义路径
     * @return
     */
    public static String uploadLocal(File file, String bizPath, String uploadpath) {
        return uploadLocal(getMultipartFile(file, "file"), bizPath, uploadpath);
    }

    /**
     * 本地文件上传
     *
     * @param mf      文件
     * @param bizPath 自定义路径
     * @return
     */
    public static String uploadLocal(MultipartFile mf, String bizPath, String uploadpath) {
        try {
            String ctxPath = uploadpath;
            String fileName = null;
            File file = new File(ctxPath + File.separator + bizPath + File.separator);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            String orgName = mf.getOriginalFilename();// 获取文件名
            orgName = CommonUtils.getFileName(orgName);
            if (orgName.contains(".")) {
                fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
            } else {
                fileName = orgName + "_" + System.currentTimeMillis();
            }
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);
            String dbpath = null;
            if (StringUtils.isNotBlank(bizPath)) {
                dbpath = bizPath + File.separator + fileName;
            } else {
                dbpath = fileName;
            }
            if (dbpath.contains("\\")) {
                dbpath = dbpath.replace("\\", "/");
            }
            return dbpath;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    public static MultipartFile getMultipartFile(File file, String filedName) {
        return new CommonsMultipartFile(createFileItem(file, filedName));
    }

    /*
    创建FileItem
     */
    private static FileItem createFileItem(File file, String fieldName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String contentType = ContentTypeConstant.getFormat(file);
        FileItem item = factory.createItem(fieldName, contentType, true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            fis = new FileInputStream(file);
            os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    public static List<File> parser(List<String> locations, String temppath) throws IOException {
        File tempPath = new File(temppath);
        tempPath.mkdirs();
        for (String location : locations) {
            File file = null;
            if (FileUploadType.MINIO.getCode().equals(FileConfig.uploadType)) {
                String minioPath = location.substring(location.indexOf("upload/"));
                file = new File(FileConfig.uploadPath + File.separator + minioPath);
                InputStream currInput = MinioUtil.getObject(MinioUtil.getBucketName(), minioPath);
                if (currInput != null) {
                    org.apache.commons.io.FileUtils.copyInputStreamToFile(currInput, file);
                } else {
                    continue;
                }
            } else {
                file = new File(FileConfig.uploadPath + File.separator + location);
            }
            if ("application/x-zip-compressed".equals(ContentTypeConstant.getFormat(file))) {
                ZipUtil.unzip(file.getPath(), tempPath.getPath(), Charset.forName("gbk"));
            } else {
                FileUtil.copy(file.getPath(), tempPath.getPath(), true);
            }
            FileUtil.del(file);
        }
        return Arrays.asList(tempPath.listFiles());
    }

    public static InputStream getObject(String uploadType, String objectName) {
        InputStream stream = null;
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            stream = MinioUtil.getObject(objectName);
        } else {
            stream = OssBootUtil.getOssFile(objectName);
        }
        return stream;
    }

    public static InputStream getObject(String uploadType, String objectName, long offset, long length) {
        InputStream stream = null;
        if (FileUploadType.MINIO.getCode().equals(uploadType)) {
            stream = MinioUtil.getObject(objectName, offset, length);
        } else {
            stream = OssBootUtil.getOssFile(objectName, offset, length);
        }
        return stream;
    }

    public static File getFile(String location, String locationType) throws IOException {
        File file = new File(FileConfig.uploadPath + File.separator + location);
        if (!FileUploadType.MINIO.getCode().equals(locationType)) {
            InputStream stream = CommonUtils.getObject(locationType, "upload/" + location);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(stream, file);
        }
        return file;
    }

    @SneakyThrows
    public static void copyFile(String location, String target, String locationType) {
        if (FileUploadType.MINIO.getCode().equals(locationType)) {
            if (new File(FileConfig.uploadPath + File.separator + location).exists()) {
                FileUtil.copy(FileConfig.uploadPath + File.separator + location, target, true);
            }
        } else {
            InputStream stream = CommonUtils.getObject(locationType, "upload/" + location);
            if (Objects.nonNull(stream)) {
                org.apache.commons.io.FileUtils.copyInputStreamToFile(stream, new File(target));
            }
        }
    }

}