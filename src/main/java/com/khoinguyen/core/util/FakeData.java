package com.khoinguyen.core.util;

import com.khoinguyen.core.dto.request.AddressRequestDto;
import com.khoinguyen.core.dto.request.UserRequestDTO;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class FakeData {
    public static List<UserRequestDTO> generateFakeUsers(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> UserRequestDTO.builder()
                        .id(i)
                        .firstName("FirstName " + i)
                        .lastName("LastName " + i)
                        .email("user" + i + "@example.com")
                        .phone("012345678" + i)
                        .dateOfBirth("01/01/1990")
                        .gender(Gender.MALE)  // Use appropriate gender
                        .username("username" + i)
                        .password("password" + i)
                        .type("USER")
                        .addresses(generateFakeAddresses(i))
                        .status(UserStatus.ACTIVE)
                        .build()
                ).toList();
    }

    public static Set<AddressRequestDto> generateFakeAddresses(int i) {
        return Collections.singleton(
                AddressRequestDto.builder()
                        .apartmentNumber("Apartment " + i)
                        .floor("Floor " + i)
                        .building("Building A")
                        .streetNumber("Street " + i)
                        .street("Main St")
                        .city("City " + i)
                        .country("Country " + i)
                        .addressType(1)
                        .build()
        );
    }
}
