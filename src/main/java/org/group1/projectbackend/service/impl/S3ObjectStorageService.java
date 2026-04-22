package org.group1.projectbackend.service.impl;

import java.io.InputStream;
import org.group1.projectbackend.config.S3StorageProperties;
import org.group1.projectbackend.service.ObjectStorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3ObjectStorageService implements ObjectStorageService {

    private final S3Client s3Client;
    private final S3StorageProperties properties;

    public S3ObjectStorageService(S3Client s3Client, S3StorageProperties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    @Override
    public void upload(String storageKey, String contentType, long contentLength, InputStream inputStream) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storageKey)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (S3Exception ex) {
            throw new IllegalStateException("Failed to upload object to S3-compatible storage", ex);
        }
    }

    @Override
    public Resource download(String storageKey) {
        try {
            validateObjectSize(storageKey);

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storageKey)
                    .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
            return new InputStreamResource(response);
        } catch (S3Exception ex) {
            throw new IllegalStateException("Failed to download object from S3-compatible storage", ex);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storageKey)
                    .build();

            s3Client.deleteObject(request);
        } catch (S3Exception ex) {
            throw new IllegalStateException("Failed to delete object from S3-compatible storage", ex);
        }
    }

    private void validateObjectSize(String storageKey) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(storageKey)
                .build();

        HeadObjectResponse response = s3Client.headObject(request);
        long maxDownloadSizeBytes = properties.getMaxDownloadSizeBytes();

        if (maxDownloadSizeBytes > 0 && response.contentLength() > maxDownloadSizeBytes) {
            throw new IllegalStateException("Object exceeds maximum allowed download size");
        }
    }
}
