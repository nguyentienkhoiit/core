package com.khoinguyen.core.dto.response;

import com.khoinguyen.core.util.Gender;
import com.khoinguyen.core.util.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private String type;

    private UserStatus status;

    public UserDetailResponse(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
