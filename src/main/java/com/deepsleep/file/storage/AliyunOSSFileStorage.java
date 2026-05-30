package com.deepsleep.file.storage;

import com.deepsleep.file.model.UploadFile;
import com.deepsleep.file.oss.AliyunOSSOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AliyunOSSFileStorage implements FileStorage {

    private final AliyunOSSOperator aliyunOSSOperator;

    @Override
    public void storage(UploadFile uploadFile) {
        aliyunOSSOperator.upload(uploadFile.objectKey(), uploadFile.inputStream(), uploadFile.contentType());
    }

    @Override
    public void delete(String objectKey) {
        aliyunOSSOperator.delete(objectKey);
    }

    @Override
    public String getUrl(String objectKey) {
        return aliyunOSSOperator.generatePresignedUrl(objectKey);
    }

}
