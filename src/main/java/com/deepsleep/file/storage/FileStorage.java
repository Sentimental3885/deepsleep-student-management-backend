package com.deepsleep.file.storage;


import com.deepsleep.file.model.UploadFile;

public interface FileStorage {

    void storage(UploadFile uploadFile);

    void delete(String objectKey);

    String getUrl(String objectKey);

}
