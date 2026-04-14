package org.group1.projectbackend.entity.enums;

public enum ActivityType {

    // Generic actions
    CREATED,
    UPDATED,
    DELETED,

    // Ticket actions
    STATUS_CHANGED,
    ASSIGNED,

    // Comment actions
    COMMENT_CREATED,
    COMMENT_UPDATED,

    // File actions
    FILE_UPLOADED,
    FILE_DELETED
}