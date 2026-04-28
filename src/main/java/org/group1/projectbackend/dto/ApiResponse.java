package org.group1.projectbackend.dto;

import java.time.LocalDateTime;

public class ApiResponse<T> {

    private String status;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() { return status; }
    public T getData() { return data; }
    public LocalDateTime getTimestamp() { return timestamp; }
}