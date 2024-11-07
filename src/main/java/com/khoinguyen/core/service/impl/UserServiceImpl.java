package com.khoinguyen.core.service.impl;

import com.khoinguyen.core.configuration.Translator;
import com.khoinguyen.core.dto.request.AddressDTO;
import com.khoinguyen.core.dto.request.UserRequestDTO;
import com.khoinguyen.core.dto.response.PageResponse;
import com.khoinguyen.core.dto.response.UserDetailResponse;
import com.khoinguyen.core.exception.ResourceNotFoundException;
import com.khoinguyen.core.model.Address;
import com.khoinguyen.core.model.User;
import com.khoinguyen.core.repository.SearchRepository;
import com.khoinguyen.core.repository.UserRepository;
import com.khoinguyen.core.service.UserService;
import com.khoinguyen.core.util.UserStatus;
import com.khoinguyen.core.util.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final SearchRepository repository;

    @Override
    public long saveUser(UserRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();

        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));
        userRepository.save(user);

        log.info("User has added successfully, userId={}", user.getId());

        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));


        userRepository.save(user);

        log.info("User has updated successfully, userId={}", userId);
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);

        log.info("User has changed successfully, userId={}", userId);
    }

    @Override
    public void deleteUser(long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
        log.info("User has deleted successfully, userId={}", userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        log.info("User has retrieved successfully, userId={}", userId);
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .type(user.getType().name())
                .build();
    }

    @Override
    public PageResponse getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy) {
        pageNo = pageNo > 0 ? pageNo - 1 : pageNo;

        List<Sort.Order> sorts = new ArrayList<>();
        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                if(matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                }
                else if(matcher.group(3).equalsIgnoreCase("desc")) {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));
        Page<User> page = userRepository.findAll(pageable);

        List<UserDetailResponse> list = page.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .type(user.getType().name())
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public PageResponse getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sortBy) {
        pageNo = pageNo > 0 ? pageNo - 1 : pageNo;

        List<Sort.Order> orders = new ArrayList<>();

        if(sortBy != null) {
            for(String s: sortBy) {
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()) {
                    if(matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    }
                    else if(matcher.group(3).equalsIgnoreCase("desc")) {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));
        Page<User> page = userRepository.findAll(pageable);

        List<UserDetailResponse> list = page.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .type(user.getType().name())
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public PageResponse<?> getAllUsersWithSortByMultipleColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        return repository.getAllUsersWithSortByMultipleColumnAndSearch(pageNo, pageSize, search, sortBy);
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Translator.toLocale("user.not.found")));
    }

    private Set<Address> convertToAddress(Set<AddressDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }
}
