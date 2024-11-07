package com.khoinguyen.core.controller;

import com.khoinguyen.core.configuration.Translator;
import com.khoinguyen.core.dto.request.UserRequestDTO;
import com.khoinguyen.core.dto.response.PageResponse;
import com.khoinguyen.core.dto.response.ResponseData;
import com.khoinguyen.core.dto.response.ResponseError;
import com.khoinguyen.core.dto.response.UserDetailResponse;
import com.khoinguyen.core.exception.ResourceNotFoundException;
import com.khoinguyen.core.service.UserService;
import com.khoinguyen.core.util.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
@Tag(name = "User Controller")
public class UserController {

    private final UserService userService;

    private static final String ERROR_MESSAGE = "errorMessage={}";

    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping(value = "/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO request) {
        log.info("Request add user, {} {}", request.getFirstName(), request.getLastName());

        try {
            long userId = userService.saveUser(request);

            String message = Translator.toLocale("user.add.success");
            return ResponseData.<Long>builder()
                    .status(HttpStatus.CREATED.value())
                    .message(message)
                    .data(userId)
                    .build();
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return ResponseError.<Long>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Add user fail")
                    .build();
        }
    }

    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable @Min(1) int userId, @Valid @RequestBody UserRequestDTO user) {
        log.info("Request update userId={}", userId);

        try {
            userService.updateUser(userId, user);
            return ResponseData.<Void>builder()
                    .status(HttpStatus.ACCEPTED.value())
                    .message(Translator.toLocale("user.upd.success"))
                    .build();
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return ResponseError.<Long>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Update user fail")
                    .build();
        }
    }

    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{userId}")
    public ResponseData<?> updateStatus(@Min(1) @PathVariable int userId, @RequestParam UserStatus status) {
        try {
            userService.changeStatus(userId, status);
            return ResponseData.<Void>builder()
                    .status(HttpStatus.ACCEPTED.value())
                    .message(Translator.toLocale("user.change.success"))
                    .build();
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return ResponseError.<Long>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Change user fail")
                    .build();
        }
    }

    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int userId) {
        log.info("Request delete userId={}", userId);

        try {
            userService.deleteUser(userId);
            return ResponseData.<Void>builder()
                    .status(HttpStatus.NO_CONTENT.value())
                    .message(Translator.toLocale("user.del.success"))
                    .build();
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return ResponseError.<Long>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Delete user fail")
                    .build();
        }
    }

    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    @GetMapping("/{userId}")
    public ResponseData<?> getUser(@PathVariable @Min(1) int userId) {
        log.info("Request get user detail, userId={}", userId);

        try {
            return ResponseData.<UserDetailResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Get successfully")
                    .data(userService.getUser(userId))
                    .build();
        } catch (ResourceNotFoundException e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return ResponseError.<Long>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<PageResponse> getAllUser(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                      @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                      @RequestParam(required = false) String sortBy) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);

        PageResponse<PageResponse> users = userService.getAllUsersWithSortBy(pageNo, pageSize, sortBy);
        return ResponseData.<PageResponse>builder()
                .status(HttpStatus.OK.value())
                .message("users")
                .data(users)
                .build();
    }

    @Operation(summary = "Get list of users per pageNo with sort by multiple columns",
            description = "Send a request via this API to get user list by pageNo and pageSize with sort by multiple columns")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ResponseData<PageResponse> getAllUsersWithSortByMultipleColumn(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                               @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                               @RequestParam(required = false) String... sortBy) {
        log.info("Request get user list with sort by multiple columns, pageNo={}, pageSize={}", pageNo, pageSize);

        PageResponse<PageResponse> users = userService.getAllUsersWithSortByMultipleColumns(pageNo, pageSize, sortBy);
        return ResponseData.<PageResponse>builder()
                .status(HttpStatus.OK.value())
                .message("users")
                .data(users)
                .build();
    }

    @Operation(summary = "Get list of users per pageNo with sort by multiple columns amd search",
            description = "Send a request via this API to get user list by pageNo and pageSize with sort by multiple columns")
    @GetMapping("/list-with-sort-by-multiple-columns-search")
    public ResponseData<PageResponse> getAllUsersWithSortByMultipleColumnAndSearch(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                                   @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                                   @RequestParam(required = false) String search,
                                                                                   @RequestParam(required = false) String sortBy) {
        log.info("Request get user list with sort by multiple columns, pageNo={}, pageSize={} and search", pageNo, pageSize);

        PageResponse<?> users = userService.getAllUsersWithSortByMultipleColumnAndSearch(pageNo, pageSize, search, sortBy);
        return ResponseData.<PageResponse>builder()
                .status(HttpStatus.OK.value())
                .message("users")
                .data(users)
                .build();
    }
}
