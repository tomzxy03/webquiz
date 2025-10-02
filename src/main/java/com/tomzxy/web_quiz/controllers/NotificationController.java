package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.Notification.NotificationReqDTO;
import com.tomzxy.web_quiz.dto.requests.UserReqDto;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.NotificationResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.UserResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public DataResDTO<PageResDTO<?>> getAllNotificationsWithPageable(@Min(0) int page, @Max(10) int size){
        log.info("Get all notifications");
        try{
            return DataResDTO.ok(notificationService.getAllNotification(page, size));
        }catch (Exception e){
            return DataResDTO.error(AppCode.NOT_FOUND, "Notification not found!");
        }
    }
    @PostMapping("")
    @Operation(summary = "Create notification", description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created successfully",
                    content = @Content(schema = @Schema(implementation = DataResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public DataResDTO<NotificationResDTO> createNotification(@RequestBody NotificationReqDTO notificationReqDTO){
        log.info("Create notification with title {}", notificationReqDTO.getTitle());
        try{
            return DataResDTO.create(notificationService.createNotification(notificationReqDTO));
        }catch (Exception e){
            return DataResDTO.error(AppCode.DATA_EXISTED, "Notification has been existed!");
        }
    }
    @GetMapping(ApiDefined.Notification.ID)
    @Operation(summary = "Get notification by ID", description = "Retrieve a notification by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "notification found successfully"),
            @ApiResponse(responseCode = "404", description = "notification not found")
    })
    public DataResDTO<NotificationResDTO> getNotificationById(Long notificationId){
        log.info("Get notification by id {}", notificationId );
        try{
            return DataResDTO.ok(notificationService.getNotificationId(notificationId));
        }catch (Exception e){
            return DataResDTO.error(AppCode.NOT_FOUND, "Notification not found!");
        }
    }
    @PutMapping(ApiDefined.Notification.ID)
    @Operation(summary = "Update notification", description = "Update an existing notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification updated successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public DataResDTO<NotificationResDTO> updateUser(
            @Parameter(description = "User ID") @PathVariable Long notificationId,
            @RequestBody @Valid NotificationReqDTO notificationReqDTO){
        log.info("Update notification with id {}", notificationId);
        return DataResDTO.update(notificationService.updateNotification(notificationId,notificationReqDTO));
    }

    @DeleteMapping(ApiDefined.Notification.ID)
    @Operation(summary = "Delete notification", description = "Delete a notification by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public DataResDTO<?> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long notificationId){
        log.info("Delete notification with id {}", notificationId);
        notificationService.deleteNotification(notificationId);
        return DataResDTO.delete();
    }

}
