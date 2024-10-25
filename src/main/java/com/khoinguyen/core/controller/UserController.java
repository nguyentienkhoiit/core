package com.khoinguyen.core.controller;

import com.khoinguyen.core.configuration.Translator;
import com.khoinguyen.core.dto.request.UserRequestDTO;
import com.khoinguyen.core.dto.response.ResponseData;
import com.khoinguyen.core.service.UserService;
import com.khoinguyen.core.util.FakeData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
@Tag(name = "User Controller")
public class UserController {

    private final UserService userService;

    private static final List<UserRequestDTO> users;

    static {
        users = FakeData.generateFakeUsers(10);
    }

    @Operation(summary = "Add User", description = "API Create new user")
    @PostMapping("/")
    public ResponseData<?> addUser(@Valid @RequestBody UserRequestDTO user) {
        String message = Translator.toLocale("user.add.success");
        return ResponseData.<Integer>builder()
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(userService.addUser(user))
                .build();
    }

    @Operation(summary = "Update User", description = "API Update user")
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable @Min(1) int userId, @Valid @RequestBody UserRequestDTO user) {
        return ResponseData.<Void>builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("User updated successfully")
                .build();
    }

    @Operation(summary = "Update status User", description = "API Update status user")
    @PatchMapping("/{userId}")
    public ResponseData<?> updateStatus(@Min(1) @PathVariable int userId, @RequestParam boolean status) {
        return ResponseData.<Void>builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("User changed " + status + " with user id: " + userId)
                .build();
    }

    @Operation(summary = "Delete User", description = "API Delete user")
    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int userId) {
        return ResponseData.<Void>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("delete user with id = " + userId)
                .build();
    }

    @Operation(summary = "Get User", description = "API Get user")
    @GetMapping("/{userId}")
    public ResponseData<?> getUser(@PathVariable @Min(1) int userId) {
        UserRequestDTO user = users.get(userId);
        return ResponseData.<UserRequestDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Get successfully")
                .data(user)
                .build();
    }

    @Operation(summary = "Get List User", description = "API Get List user")
    @GetMapping("/list")
    public ResponseData<List<UserRequestDTO>> getAllUser(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                         @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize) {
        log.info("getAllUser");
        return ResponseData.<List<UserRequestDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Get successfully")
                .data(users)
                .build();
    }
}
