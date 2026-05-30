package com.deepsleep.file.model;

import lombok.Builder;
import lombok.NonNull;

import java.io.InputStream;

@Builder
public record UploadFile(
        @NonNull String objectKey,
        String contentType,
        long size,
        @NonNull InputStream inputStream
) {}
