package cn.silver.framework.file.minio;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.silver.framework.file.filter.FileTypeFilter;
import cn.silver.framework.file.filter.StrAttackFilter;
import cn.silver.framework.file.util.CommonUtils;
import com.google.common.collect.HashMultimap;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import io.minio.messages.Part;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * minio文件上传工具类
 */
@Slf4j
public class MinioUtil {
    private static String minioUrl;
    private static String minioName;
    private static String minioPass;
    private static String bucketName;
    private static CustomMinioClient minioClient = null;

    public static void setMinioName(String minioName) {
        MinioUtil.minioName = minioName;
    }

    public static void setMinioPass(String minioPass) {
        MinioUtil.minioPass = minioPass;
    }

    public static String getMinioUrl() {
        return minioUrl;
    }

    public static void setMinioUrl(String minioUrl) {
        MinioUtil.minioUrl = minioUrl;
    }

    public static String getBucketName() {
        return bucketName;
    }

    public static void setBucketName(String bucketName) {
        MinioUtil.bucketName = bucketName;
    }

    /**
     * 创建一个桶
     */
    public static void createBucket(String bucket) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /**
     * 列出所有的桶
     */
    public static List<String> listBuckets() {
        initMinio(minioUrl, minioName, minioPass);
        try {
            List<Bucket> list = minioClient.listBuckets();
            List<String> names = new ArrayList<>();
            list.forEach(b -> {
                names.add(b.name());
            });
            return names;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 列出一个桶中的所有文件和目录
     */
    public static List<Item> listFiles(String bucket) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).recursive(true).build());
        List<Item> infos = new ArrayList<>();
        results.forEach(r -> {
            try {
                Item item = r.get();
                infos.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return infos;
    }

    public static List<Item> listFiles(String bucket, String objectName) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).prefix(objectName).recursive(true).build());
        List<Item> infos = new ArrayList<>();
        results.forEach(r -> {
            try {
                Item item = r.get();
                infos.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return infos;
    }

    /**
     * 获取文件信息
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws Exception
     */
    public static StatObjectResponse statObject(String bucketName, String objectName) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    /**
     * 更新文件上下文信息
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws Exception
     */
    public static ObjectWriteResponse putObject(String bucketName, String objectName, String contentType) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        return minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).contentType(contentType)
                .stream(MinioUtil.getObject(objectName), -1, 15 * 1000 * 1000).build());
    }

    /**
     * 查询桶是否存在
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public static Boolean existBucket(String bucketName) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询文件是否存在
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    public static Boolean existObject(String bucketName, String objectName) {
        Boolean exist = true;
        try {
            StatObjectResponse info = MinioUtil.statObject(bucketName, objectName);
        } catch (Exception e) {
            return false;
        }
        return exist;
    }

    /**
     * 合并分片文件成对象文件
     *
     * @param chunkBucKetName   分片文件所在存储桶名称
     * @param composeBucketName 合并后的对象文件存储的存储桶名称
     * @param chunkNames        分片文件名称集合
     * @param objectName        合并后的对象文件名称
     * @return true/false
     */
    @SneakyThrows
    public static boolean composeObject(String chunkBucKetName, String composeBucketName,
                                        List<String> chunkNames, String objectName, boolean isDeleteChunkObject) {
        if (null == chunkBucKetName) {
            chunkBucKetName = bucketName;
        }
        List<ComposeSource> sourceObjectList = new ArrayList<>(chunkNames.size());
        for (String chunk : chunkNames) {
            sourceObjectList.add(
                    ComposeSource.builder()
                            .bucket(chunkBucKetName)
                            .object(chunk)
                            .build()
            );
        }
        initMinio(minioUrl, minioName, minioPass);
        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(composeBucketName)
                        .object(objectName)
                        .sources(sourceObjectList)
                        .build()
        );
        if (isDeleteChunkObject) {
            for (String name : chunkNames) {
                MinioUtil.removeObject(chunkBucKetName, name);
            }
        }
        return true;
    }


    /**
     * 上传文件
     *
     * @param file
     * @param bizPath
     * @param bucket
     * @return
     */
    public static String upload(MultipartFile file, String bizPath, String bucket) {
        String file_url = "";
        //update-begin-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击
        bizPath = StrAttackFilter.filter(bizPath);
        //update-end-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击
        String newBucket = bucketName;
        if (StringUtils.isNotBlank(bucket)) {
            newBucket = bucket;
        }
        try {
            initMinio(minioUrl, minioName, minioPass);
            // 检查存储桶是否已经存在
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(newBucket).build())) {
                log.info("Bucket already exists.");
            } else {
                // 创建一个名为ota的存储桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(newBucket).build());
                log.info("create a new bucket.");
            }
            //update-begin-author:liusq date:20210809 for: 过滤上传文件类型
            FileTypeFilter.fileTypeFilter(file);
            //update-end-author:liusq date:20210809 for: 过滤上传文件类型
            InputStream stream = file.getInputStream();
            // 获取文件名
            String orgName = file.getOriginalFilename();
            if ("".equals(orgName)) {
                orgName = file.getName();
            }
            orgName = CommonUtils.getFileName(orgName);
            String objectName = bizPath + "/"
                    + (orgName.indexOf(".") == -1
                    ? orgName + "_" + System.currentTimeMillis()
                    : orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.lastIndexOf("."))
            );

            // 使用putObject上传一个本地文件到存储桶中。
            if (objectName.startsWith("/")) {
                objectName = objectName.substring(1);
            }
            PutObjectArgs objectArgs = PutObjectArgs.builder().object(objectName)
                    .bucket(newBucket)
                    .contentType(file.getContentType())
                    .stream(stream, stream.available(), -1).build();
            minioClient.putObject(objectArgs);
            stream.close();
            file_url = minioUrl + newBucket + "/" + objectName;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return file_url;
    }

    /**
     * 文件上传
     *
     * @param file
     * @param bizPath
     * @return
     */
    public static String upload(MultipartFile file, String bizPath) {
        return upload(file, bizPath, null);
    }

    /**
     * 上传本地文件
     *
     * @param bucketName 存储桶
     * @param objectName 对象名称
     * @param fileName   本地文件路径
     */
    public static String upload(String bucketName, String objectName, String fileName) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName).object(objectName).filename(fileName).build());
        return minioUrl + bucketName + "/" + objectName;
    }

    /**
     * 上传文件到minio
     *
     * @param stream
     * @param objectName
     * @return
     */
    public static String upload(InputStream stream, String objectName) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            log.info("Bucket already exists.");
        } else {
            // 创建一个名为ota的存储桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("create a new bucket.");
        }
        PutObjectArgs objectArgs = PutObjectArgs.builder()
                .object(objectName)
                .bucket(bucketName)
                .stream(stream, stream.available(), -1).build();
        minioClient.putObject(objectArgs);
        stream.close();
        return minioUrl + bucketName + "/" + objectName;
    }

    public static String upload(InputStream stream, String contentType, String objectName, String bucketName) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            log.info("Bucket already exists.");
        } else {
            // 创建一个名为ota的存储桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("create a new bucket.");
        }
        PutObjectArgs objectArgs = PutObjectArgs.builder()
                .object(objectName)
                .bucket(bucketName)
                .contentType(contentType)
                .stream(stream, stream.available(), -1).build();
        minioClient.putObject(objectArgs);
        stream.close();
        return minioUrl + bucketName + "/" + objectName;
    }

    /**
     * 上传文件到minio
     *
     * @param stream     二进制流
     * @param objectName 对象名称
     * @return 文件路径
     */
    public static String upload(InputStream stream, String bucketName, String objectName) throws Exception {
        initMinio(minioUrl, minioName, minioPass);
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            log.info("Bucket already exists.");
        } else {
            // 创建一个名为ota的存储桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("create a new bucket.");
        }
        PutObjectArgs objectArgs = PutObjectArgs.builder().object(objectName)
                .bucket(bucketName)
                .stream(stream, stream.available(), -1).build();
        minioClient.putObject(objectArgs);
        stream.close();
        return minioUrl + bucketName + "/" + objectName;
    }

    public static Boolean uploadUrl(String url, String bucketName, String objectName) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            HttpResponse response = HttpRequest.get(url).execute();
            if (response.isOk()) {
                DataInputStream in = new DataInputStream(response.bodyStream());
                MinioUtil.upload(in, bucketName, objectName);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static InputStream getObject(String objectName) {
        return getObject(bucketName, objectName);
    }

    /**
     * 获取文件流
     *
     * @param bucketName 存储桶
     * @param objectName 对象名称
     * @return 二进制流
     */
    public static InputStream getObject(String bucketName, String objectName) {
        InputStream inputStream = null;
        try {
            initMinio(minioUrl, minioName, minioPass);
            GetObjectArgs objectArgs = GetObjectArgs.builder().object(objectName)
                    .bucket(bucketName).build();
            inputStream = minioClient.getObject(objectArgs);
        } catch (Exception e) {
            log.info("文件获取失败" + e.getMessage());
        }
        return inputStream;
    }

    public static InputStream getObject(String objectName, long offset, long length) {
        return getObject(bucketName, objectName, offset, length);
    }

    /**
     * 断点下载
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度
     * @return 流
     */
    public static InputStream getObject(String bucketName, String objectName, long offset, long length) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            return minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(objectName).offset(offset).length(length)
                            .build());
        } catch (Exception e) {
            log.info("断点下载失败", e);
        }
        return null;
    }

    /**
     * 拷贝文件
     *
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param srcBucketName 目标bucket名称
     * @param srcObjectName 目标文件名称
     */
    public static ObjectWriteResponse copyObject(String bucketName, String objectName,
                                                 String srcBucketName, String srcObjectName) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            return minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .source(CopySource.builder().bucket(bucketName).object(objectName).build())
                            .bucket(srcBucketName)
                            .object(srcObjectName)
                            .build());
        } catch (Exception e) {
            log.info("拷贝文件失败", e);
        }
        return null;
    }

    /**
     * 移动文件
     *
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param srcBucketName 目标bucket名称
     * @param srcObjectName 目标文件名称
     */
    public static Boolean moveObject(String bucketName, String objectName,
                                     String srcBucketName, String srcObjectName) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .source(CopySource.builder().bucket(bucketName).object(objectName).build())
                            .bucket(srcBucketName)
                            .object(srcObjectName)
                            .build());
            RemoveObjectArgs objectArgs = RemoveObjectArgs.builder().object(objectName)
                    .bucket(bucketName).build();
            minioClient.removeObject(objectArgs);
            return true;
        } catch (Exception e) {
            log.info("移动文件失败", e);
        }
        return false;
    }

    /**
     * 删除一个桶
     *
     * @param bucketName
     * @throws Exception
     */
    public static Boolean removeBucket(String bucketName) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param objectName
     * @throws Exception
     */
    public static Boolean removeObject(String bucketName, String objectName) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            RemoveObjectArgs objectArgs = RemoveObjectArgs.builder().object(objectName)
                    .bucket(bucketName).build();
            minioClient.removeObject(objectArgs);
            return true;
        } catch (Exception e) {
            log.info("文件删除失败" + e.getMessage());
        }
        return false;
    }

    /**
     * 批量删除文件
     *
     * @param bucketName  bucket
     * @param objectNames 需要删除的文件列表
     * @return
     */
    public static void removeObjects(String bucketName, List<String> objectNames) {
        List<DeleteObject> objects = new LinkedList<>();
        objectNames.forEach(s -> {
            objects.add(new DeleteObject(s));
            try {
                removeObject(bucketName, s);
            } catch (Exception e) {
                log.error("批量删除失败！error:{}", e);
            }
        });
    }

    /**
     * 获取文件外链
     *
     * @param bucketName
     * @param objectName
     * @param expires
     * @return
     */
    public static String getObjectURL(String bucketName, String objectName, Integer expires) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            GetPresignedObjectUrlArgs objectArgs = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .object(objectName)
                    .bucket(bucketName)
                    .expiry(expires).build();
            String url = minioClient.getPresignedObjectUrl(objectArgs);
            return URLDecoder.decode(url, "UTF-8");
        } catch (Exception e) {
            log.info("文件路径获取失败" + e.getMessage());
        }
        return "";
    }

    /**
     * 初始化客户端
     *
     * @param minioUrl
     * @param minioName
     * @param minioPass
     * @return
     */
    private static MinioClient initMinio(String minioUrl, String minioName, String minioPass) {
        if (minioClient == null) {
            try {
                MinioClient minioClientTemp = MinioClient.builder().endpoint(minioUrl).credentials(minioName, minioPass).build();
                minioClient = new CustomMinioClient(minioClientTemp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return minioClient;
    }


    /**
     * 校验MD5
     *
     * @param bucketName
     * @param objectName
     * @param md5
     * @return
     */
    public static boolean checkMd5(String bucketName, String objectName, String md5) {
        try {
            initMinio(minioUrl, minioName, minioPass);
            //利用apache工具类获取文件md5值
            InputStream inputStream = MinioUtil.getObject(bucketName, objectName);
            String md5Hex = DigestUtils.md5Hex(inputStream);
            if (md5.equalsIgnoreCase(md5Hex)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取分片上传id
     *
     * @param objectName
     * @param contentType
     * @return
     */
    @SneakyThrows
    public static String getUploadId(String objectName, String contentType) {
        return getUploadId(bucketName, objectName, contentType);
    }

    /**
     * 获取分片上传id
     *
     * @param bucketName
     * @param objectName
     * @param contentType
     * @return
     */
    @SneakyThrows
    public static String getUploadId(String bucketName, String objectName, String contentType) {
        initMinio(minioUrl, minioName, minioPass);
        HashMultimap<String, String> headers = HashMultimap.create();
        headers.put("Content-Type", contentType);
        // 获取updateId
        return minioClient.initMultiPartUpload(bucketName, "", objectName, headers, null);
    }

    /**
     * 上传文件分片
     *
     * @param file
     * @param objectName
     * @param uploadId
     * @param partNum
     * @return
     */
    @SneakyThrows
    public static String uploadPart(MultipartFile file, String objectName, String uploadId, Integer partNum) {
        initMinio(minioUrl, minioName, minioPass);
//        InputStream stream = file.getInputStream();
//        InputStream stream = new ByteArrayInputStream(file.getBytes());
        UploadPartResponse response = minioClient.uploadPart(bucketName, "", objectName, file.getBytes(), file.getBytes().length,
                uploadId, partNum, null, null);
        return response.etag();
    }

    /**
     * 合并文件分片
     *
     * @param objectName
     * @param uploadId
     * @param partTags
     * @return
     */
    @SneakyThrows
    public static String mergeFile(String objectName, String uploadId, List<String> partTags) {
        initMinio(minioUrl, minioName, minioPass);
        Part[] parts = new Part[partTags.size()];
        for (int i = 0, length = partTags.size(); i < length; i++) {
            parts[i] = new Part(i, partTags.get(i));
        }
        ObjectWriteResponse objectWriteResponse = minioClient.mergeMultipartUpload(bucketName, "", objectName,
                uploadId, parts, null, null);
        return minioUrl + bucketName + "/" + objectName;
    }

    @SneakyThrows
    public static String getSign(String objectName) {
        initMinio(minioUrl, minioName, minioPass);
        InputStream inputStream = MinioUtil.getObject(bucketName, objectName);
//            String md5Hex = DigestUtils.md5Hex(inputStream);
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5 = MessageDigest.getInstance("md5");
        while ((numRead = inputStream.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        inputStream.close();
        return new BigInteger(1, md5.digest()).toString(16);
    }
}
