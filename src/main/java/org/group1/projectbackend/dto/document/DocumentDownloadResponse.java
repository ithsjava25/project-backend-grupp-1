package org.group1.projectbackend.dto.document;

import org.springframework.core.io.Resource;

public record DocumentDownloadResponse(
        String fileName,
        String contentType,
        Resource resource
) {
}
