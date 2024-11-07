package com.khoinguyen.core.service;

import com.khoinguyen.core.dto.request.UserRequestDTO;
import com.khoinguyen.core.dto.response.PageResponse;
import com.khoinguyen.core.dto.response.UserDetailResponse;
import com.khoinguyen.core.util.UserStatus;
import jakarta.validation.constraints.Min;

public interface UserService {
    long saveUser(UserRequestDTO user);

    void updateUser(long userId, UserRequestDTO user);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sortBy);

    PageResponse<?> getAllUsersWithSortByMultipleColumnAndSearch(int pageNo, int pageSize, String search, String sortBy);
}
