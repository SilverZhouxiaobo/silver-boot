package cn.silver.framework.file.oss;

import cn.silver.framework.file.filter.StrAttackFilter;
import cn.silver.framework.file.util.CommonUtils;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description: 阿里云 oss 上传工具类(高依赖版)
 * @Date: 2019/5/10
 */
@Slf4j
public class OssBootUtil {

    private static String endPoint;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String bucketName;
    private static String staticDomain;
    /**
     * oss 工具客户端
     */
    private static OSSClient ossClient = null;

    public static String getStaticDomain() {
        return staticDomain;
    }

    public static void setStaticDomain(String staticDomain) {
        OssBootUtil.staticDomain = staticDomain;
    }

    public static String getEndPoint() {
        return endPoint;
    }

    public static void setEndPoint(String endPoint) {
        OssBootUtil.endPoint = endPoint;
    }

    public static String getAccessKeyId() {
        return accessKeyId;
    }

    public static void setAccessKeyId(String accessKeyId) {
        OssBootUtil.accessKeyId = accessKeyId;
    }

    public static String getAccessKeySecret() {
        return accessKeySecret;
    }

    public static void setAccessKeySecret(String accessKeySecret) {
        OssBootUtil.accessKeySecret = accessKeySecret;
    }

    public static String getBucketName() {
        return bucketName;
    }

    public static void setBucketName(String bucketName) {
        OssBootUtil.bucketName = bucketName;
    }

    public static OSSClient getOssClient() {
        return ossClient;
    }

    /**
     * 上传文件至阿里云 OSS
     * 文件上传成功,返回文件完整访问路径
     * 文件上传失败,返回 null
     *
     * @param file    待上传文件
     * @param fileDir 文件保存目录
     * @return oss 中的相对文件路径
     */
    public static String upload(MultipartFile file, String fileDir, String customBucket) {
        String FILE_URL = null;
        initOSS(endPoint, accessKeyId, accessKeySecret);
        StringBuilder fileUrl = new StringBuilder();
        String newBucket = bucketName;
        if (StringUtils.isNotBlank(customBucket)) {
            newBucket = customBucket;
        }
        try {
            //判断桶是否存在,不存在则创建桶
            if (!ossClient.doesBucketExist(newBucket)) {
                ossClient.createBucket(newBucket);
            }
            // 获取文件名
            String orgName = file.getOriginalFilename();
            if (StringUtils.isBlank(orgName)) {
                orgName = file.getName();
            }
            orgName = CommonUtils.getFileName(orgName);
            String fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
            if (!fileDir.endsWith("/")) {
                fileDir = fileDir.concat("/");
            }
            //update-begin-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击
            fileDir = StrAttackFilter.filter(fileDir);
            //update-end-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击
            fileUrl = fileUrl.append(fileDir + fileName);

            if (StringUtils.isNotBlank(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
                FILE_URL = staticDomain + "/" + fileUrl;
            } else {
                FILE_URL = "https://" + newBucket + "." + endPoint + "/" + fileUrl;
            }
            PutObjectResult result = ossClient.putObject(newBucket, fileUrl.toString(), file.getInputStream());
            // 设置权限(公开读)
//            ossClient.setBucketAcl(newBucket, CannedAccessControlList.PublicRead);
            if (result != null) {
                log.info("------OSS文件上传成功------" + fileUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return FILE_URL;
    }

    public static String upload(InputStream stream, String objectName, String customBucket) {
        String FILE_URL = null;
        initOSS(endPoint, accessKeyId, accessKeySecret);
        StringBuilder fileUrl = new StringBuilder();
        String newBucket = bucketName;
        if (StringUtils.isNotBlank(customBucket)) {
            newBucket = customBucket;
        }
        //判断桶是否存在,不存在则创建桶
        if (!ossClient.doesBucketExist(newBucket)) {
            ossClient.createBucket(newBucket);
        }
        //update-end-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击
        fileUrl = fileUrl.append(objectName);

        if (StringUtils.isNotBlank(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
            FILE_URL = staticDomain + "/" + fileUrl;
        } else {
            FILE_URL = "https://" + newBucket + "." + endPoint + "/" + fileUrl;
        }
        PutObjectResult result = ossClient.putObject(newBucket, fileUrl.toString(), stream);
        // 设置权限(公开读)
//            ossClient.setBucketAcl(newBucket, CannedAccessControlList.PublicRead);
        if (result != null) {
            log.info("------OSS文件上传成功------" + fileUrl);
        }
        return FILE_URL;
    }

    /**
     * 获取原始URL
     *
     * @param url: 原始URL
     * @Return: java.lang.String
     */
    public static String getOriginalUrl(String url) {
        String originalDomain = "https://" + bucketName + "." + endPoint;
        if (url.indexOf(staticDomain) != -1) {
            url = url.replace(staticDomain, originalDomain);
        }
        return url;
    }

    public static String getUploadId(String objectName, String contentType) {
        initOSS(endPoint, accessKeyId, accessKeySecret);
        InitiateMultipartUploadRequest initUploadRequest = new InitiateMultipartUploadRequest(bucketName, objectName);
        InitiateMultipartUploadResult initResult = ossClient.initiateMultipartUpload(initUploadRequest);
        return initResult.getUploadId();
    }

    public static String uploadPart(MultipartFile file, String objectName, String uploadId, Integer partNum) throws IOException {
        initOSS(endPoint, accessKeyId, accessKeySecret);
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setKey(objectName);
        uploadPartRequest.setUploadId(uploadId);
        uploadPartRequest.setInputStream(file.getInputStream());
        uploadPartRequest.setPartSize(file.getSize());
        uploadPartRequest.setPartNumber(partNum);
        UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
        return uploadPartResult.getPartETag().getETag();
    }

    public static String mergeFile(String objectName, String uploadId, List<String> parts) {
        String FILE_URL = null;
        initOSS(endPoint, accessKeyId, accessKeySecret);
        List<PartETag> partETags = new ArrayList<>(parts.size());
        for (int i = 0, length = parts.size(); i < length; i++) {
            partETags.add(new PartETag(i, parts.get(i)));
        }
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        if (StringUtils.isNotBlank(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
            FILE_URL = staticDomain + "/" + uploadId;
        } else {
            FILE_URL = "https://" + bucketName + "." + endPoint + "/" + uploadId;
        }
        return FILE_URL;
    }

    /**
     * 文件上传
     *
     * @param file
     * @param fileDir
     * @return
     */
    public static String upload(MultipartFile file, String fileDir) {
        return upload(file, fileDir, null);
    }

    /**
     * 上传文件至阿里云 OSS
     * 文件上传成功,返回文件完整访问路径
     * 文件上传失败,返回 null
     *
     * @param file    待上传文件
     * @param fileDir 文件保存目录
     * @return oss 中的相对文件路径
     */
    public static String upload(FileItemStream file, String fileDir) {
        String FILE_URL = null;
        initOSS(endPoint, accessKeyId, accessKeySecret);
        StringBuilder fileUrl = new StringBuilder();
        try {
            String suffix = file.getName().substring(file.getName().lastIndexOf('.'));
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            if (!fileDir.endsWith("/")) {
                fileDir = fileDir.concat("/");
            }
            fileDir = StrAttackFilter.filter(fileDir);
            fileUrl = fileUrl.append(fileDir + fileName);
            if (StringUtils.isNotBlank(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
                FILE_URL = staticDomain + "/" + fileUrl;
            } else {
                FILE_URL = "https://" + bucketName + "." + endPoint + "/" + fileUrl;
            }
            PutObjectResult result = ossClient.putObject(bucketName, fileUrl.toString(), file.openStream());
            // 设置权限(公开读)
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (result != null) {
                log.info("------OSS文件上传成功------" + fileUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return FILE_URL;
    }

    /**
     * 删除文件
     *
     * @param url
     */
    public static void deleteUrl(String url) {
        deleteUrl(url, null);
    }

    /**
     * 删除文件
     *
     * @param url
     */
    public static void deleteUrl(String url, String bucket) {
        String newBucket = bucketName;
        if (StringUtils.isNotBlank(bucket)) {
            newBucket = bucket;
        }
        String bucketUrl = "";
        if (StringUtils.isNotBlank(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
            bucketUrl = staticDomain + "/";
        } else {
            bucketUrl = "https://" + newBucket + "." + endPoint + "/";
        }
        url = url.replace(bucketUrl, "");
        ossClient.deleteObject(newBucket, url);
    }

    /**
     * 删除文件
     *
     * @param fileName
     */
    public static void delete(String fileName) {
        ossClient.deleteObject(bucketName, fileName);
    }

    /**
     * 获取文件流
     *
     * @param objectName
     * @return
     */
    public static InputStream getOssFile(String objectName) {
        return getOssFile(objectName, null);
    }

    /**
     * 获取文件流
     *
     * @param objectName
     * @param bucket
     * @return
     */
    public static InputStream getOssFile(String objectName, String bucket) {
        InputStream inputStream = null;
        try {
            String newBucket = bucketName;
            if (StringUtils.isNotBlank(bucket)) {
                newBucket = bucket;
            }
            initOSS(endPoint, accessKeyId, accessKeySecret);
            OSSObject ossObject = ossClient.getObject(newBucket, objectName);
            inputStream = new BufferedInputStream(ossObject.getObjectContent());
        } catch (Exception e) {
            log.info("文件获取失败" + e.getMessage());
        }
        return inputStream;
    }

    public static InputStream getOssFile(String objectName, long offset, long length) {
        return getOssFile(objectName, null, offset, length);
    }


    public static InputStream getOssFile(String objectName, String bucket, long offset, long length) {
        InputStream inputStream = null;
        try {
            String newBucket = bucketName;
            if (StringUtils.isNotBlank(bucket)) {
                newBucket = bucket;
            }
            // 创建OSSClient实例。
            initOSS(endPoint, accessKeyId, accessKeySecret);
            GetObjectRequest getObjectRequest = new GetObjectRequest(newBucket, objectName);
            // 对于大小为1000 Bytes的文件，正常的字节范围为0~999。
            // 获取0~999字节范围内的数据，包括0和999，共1000个字节的数据。如果指定的范围无效（比如开始或结束位置的指定值为负数，或指定值大于文件大小），则下载整个文件。
            getObjectRequest.setRange(offset, offset + length);
            // 范围下载。
            OSSObject ossObject = ossClient.getObject(getObjectRequest);
            inputStream = ossObject.getObjectContent();
            // 读取数据。
            byte[] buf = new byte[1024];
            for (int n = 0; n != -1; ) {
                n = inputStream.read(buf, 0, buf.length);
            }
        } catch (Exception e) {
            log.info("文件获取失败" + e.getMessage());
        }
        return inputStream;
    }

    /**
     * 获取文件外链
     *
     * @param bucketName
     * @param objectName
     * @param expires
     * @return
     */
    public static String getObjectURL(String bucketName, String objectName, Date expires) {
        initOSS(endPoint, accessKeyId, accessKeySecret);
        try {
            if (ossClient.doesObjectExist(bucketName, objectName)) {
                URL url = ossClient.generatePresignedUrl(bucketName, objectName, expires);
                return URLDecoder.decode(url.toString(), "UTF-8");
            }
        } catch (Exception e) {
            log.info("文件路径获取失败" + e.getMessage());
        }
        return null;
    }

    /**
     * 初始化 oss 客户端
     *
     * @return
     */
    private static OSSClient initOSS(String endpoint, String accessKeyId, String accessKeySecret) {
        if (ossClient == null) {
            ossClient = new OSSClient(endpoint,
                    new DefaultCredentialProvider(accessKeyId, accessKeySecret),
                    new ClientConfiguration());
        }
        return ossClient;
    }


    /**
     * 上传文件到oss
     *
     * @param stream
     * @param relativePath
     * @return
     */
    public static String upload(InputStream stream, String relativePath) {
        String FILE_URL = null;
        String fileUrl = relativePath;
        initOSS(endPoint, accessKeyId, accessKeySecret);
        if (StringUtils.isNotBlank(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
            FILE_URL = staticDomain + "/" + relativePath;
        } else {
            FILE_URL = "https://" + bucketName + "." + endPoint + "/" + fileUrl;
        }
        PutObjectResult result = ossClient.putObject(bucketName, fileUrl, stream);
        // 设置权限(公开读)
        ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        if (result != null) {
            log.info("------OSS文件上传成功------" + fileUrl);
        }
        return FILE_URL;
    }

    public static String getSign(String objectName) {
        return "";
    }
}