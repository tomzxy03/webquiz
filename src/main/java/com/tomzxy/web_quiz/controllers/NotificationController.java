package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.Notification.NotificationReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.Notification.NotificationResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Tag(name = "notification",description = "Api notification")
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Notification.BASE)
public class NotificationController {
    private NotificationService notificationService;

    @GetMapping("")
    @Operation(summary = "Get all notifications", description = "Retrieve all notifications with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "notifications retrieved successfully")
    })
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllNotificationsWithPageable(@Min(0) int page, @Max(10) int size){
        log.info("Get all notifications");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(notificationService.getAllNotification(page, size)));
    }
    @PostMapping("")
    @Operation(summary = "Create notification", description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created successfully",
                    content = @Content(schema = @Schema(implementation = DataResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<NotificationResDTO>> createNotification(@RequestBody NotificationReqDTO notificationReqDTO){
        log.info("Create notification with title {}", notificationReqDTO.getTitle());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResDTO.create(notificationService.createNotification(notificationReqDTO)));
    }
    @GetMapping(ApiDefined.Notification.ID)
    @Operation(summary = "Get notification by ID", description = "Retrieve a notification by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "notification found successfully"),
            @ApiResponse(responseCode = "404", description = "notification not found")
    })
    public ResponseEntity<DataResDTO<NotificationResDTO>> getNotificationById(@PathVariable Long notificationId){
        log.info("Get notification by id {}", notificationId );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(notificationService.getNotificationId(notificationId)));
    }
    @PutMapping(ApiDefined.Notification.ID)
    @Operation(summary = "Update notification", description = "Update an existing notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification updated successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<DataResDTO<NotificationResDTO>> updateNotification(
            @Parameter(description = "Notification ID") @PathVariable Long notificationId,
            @RequestBody @Valid NotificationReqDTO notificationReqDTO){
        log.info("Update notification with id {}", notificationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.update(notificationService.updateNotification(notificationId,notificationReqDTO)));
    }

    @DeleteMapping(ApiDefined.Notification.ID)
    @Operation(summary = "Delete notification", description = "Delete a notification by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<DataResDTO<Object>> deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable Long notificationId){
        log.info("Delete notification with id {}", notificationId);
        notificationService.deleteNotification(notificationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }

}
