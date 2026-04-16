package org.group1.projectbackend.controller;

import org.group1.projectbackend.dto.activitylog.ActivityLogDto;
import org.group1.projectbackend.dto.activitylog.CreateActivityLogDto;
import org.group1.projectbackend.entity.enums.ActivityType;
import org.group1.projectbackend.service.ActivityLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityLogController.class)
public class ActivityLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityLogService activityLogService;
    private ActivityLogDto activityLogDto;

    @BeforeEach
    void setUp() {
        activityLogDto = new ActivityLogDto(
                1L,
                ActivityType.CREATED,
                "Test description",
                1L,
                1L,
                null
        );
    }

    @Test
    @WithMockUser
    void testCreateActivityLog() throws Exception {
        when(activityLogService.createActivityLog(any(CreateActivityLogDto.class)))
                .thenReturn(activityLogDto);

        mockMvc.perform(post("/activitylogs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "activityType":"CREATED",
                                  "description":"Test description",
                                  "userId":1,
                                  "documentId":1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.activityType").value("CREATED"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.documentId").value(1));
    }

    @Test
    @WithMockUser
    void testGetAllActivityLogs() throws Exception {
        when(activityLogService.getAllActivityLogs())
                .thenReturn(List.of(activityLogDto));

        mockMvc.perform(get("/activitylogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].activityType").value("CREATED"))
                .andExpect(jsonPath("$[0].description").value("Test description"))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].documentId").value(1));
    }

    @Test
    @WithMockUser
    void testGetActivityLogById() throws Exception {
        when(activityLogService.getActivityLogById(1L))
                .thenReturn(activityLogDto);

        mockMvc.perform(get("/activitylogs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.activityType").value("CREATED"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.documentId").value(1));
    }

    @Test
    @WithMockUser
    void testGetActivityLogsByDocumentId() throws Exception {
        when(activityLogService.getActivityLogsByDocumentId(1L, "asc"))
                .thenReturn(List.of(activityLogDto));

        mockMvc.perform(get("/activitylogs/document/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].activityType").value("CREATED"))
                .andExpect(jsonPath("$[0].description").value("Test description"))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].documentId").value(1));
    }

    @Test
    @WithMockUser
    void testGetActivityLogsByUserId() throws Exception {
        when(activityLogService.getActivityLogsByUserId(1L, "asc"))
                .thenReturn(List.of(activityLogDto));

        mockMvc.perform(get("/activitylogs/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].activityType").value("CREATED"))
                .andExpect(jsonPath("$[0].description").value("Test description"))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].documentId").value(1));
    }
}