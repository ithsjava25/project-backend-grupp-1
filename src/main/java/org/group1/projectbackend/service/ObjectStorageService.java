package org.group1.projectbackend.service;

import java.io.InputStream;
import org.springframework.core.io.Resource;

public interface ObjectStorageService {

    void upload(String storageKey, String contentType, long contentLength, InputStream inputStream);

    Resource download(String storageKey);

    void delete(String storageKey);
}
