package com.deepsleep.infrastructure.file.storage;


import com.deepsleep.infrastructure.file.model.UploadFile;

public interface FileStorage {

    void storage(UploadFile uploadFile);

    void delete(String objectKey);

    String getUrl(String objectKey);

    default void deleteQuietly(String objectKey) {
        try {
            delete(objectKey);
        } catch (Exception ignored) {
            // 静默删除，忽略所有异常
        }
    }

}
