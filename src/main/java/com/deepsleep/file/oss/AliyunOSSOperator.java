package com.deepsleep.file.oss;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.PresignOptions;
import com.aliyun.sdk.service.oss2.exceptions.ServiceException;
import com.aliyun.sdk.service.oss2.models.*;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunOSSOperator {

    private final OSSClient ossClient;
    private final AliyunOSSProperties ossProperties;

    public void upload(String objectKey, InputStream inputStream) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.newBuilder()
                    .bucket(ossProperties.bucket())
                    .key(objectKey)
                    .body(BinaryData.fromStream(inputStream))
                    .build();

            PutObjectResult result = ossClient.putObject(putObjectRequest);

            log.info(
                    "上传文件到阿里云OSS：key={} statusCode={} requestId={} eTag={}",
                    objectKey, result.statusCode(), result.requestId(), result.eTag()
            );
        } catch (Exception e) {
            ServiceException se = ServiceException.asCause(e);
            if (se != null) {
                log.error("上传文件到阿里云OSS失败： requestID={} errorCode={}\n", se.requestId(), se.errorCode());
            }
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
    }

    public void delete(String objectKey) {

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.newBuilder()
                    .bucket(ossProperties.bucket())
                    .key(objectKey)
                    .build();

            DeleteObjectResult result = ossClient.deleteObject(deleteObjectRequest);

            log.info(
                    "从阿里云OSS删除文件：key={} statusCode={} requestId={}",
                    objectKey, result.statusCode(), result.requestId()
            );

        } catch (Exception e) {
            ServiceException se = ServiceException.asCause(e);
            if (se != null) {
                log.error("从阿里云OSS删除文件失败： requestID={} errorCode={}\n", se.requestId(), se.errorCode());
            }
            throw new BusinessException(ResultCode.FILE_DELETE_FAILED);
        }

    }

    public String generatePresignedUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.newBuilder()
                .bucket(ossProperties.bucket())
                .key(objectKey)
                .build();

        PresignOptions presignOptions = PresignOptions.newBuilder()
                .expiration(Duration.ofSeconds(ossProperties.presignedUrlExpireSeconds()))
                .build();

        PresignResult presignResult = ossClient.presign(getObjectRequest, presignOptions);

        return presignResult.url();
    }
}
