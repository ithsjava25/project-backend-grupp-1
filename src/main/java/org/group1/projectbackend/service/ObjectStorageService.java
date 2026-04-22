package org.group1.projectbackend.service;

import java.io.InputStream;

public interface ObjectStorageService {

    void upload(String storageKey, String contentType, long contentLength, InputStream inputStream);

    byte[] download(String storageKey);

    void delete(String storageKey);
}
