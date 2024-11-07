package com.khoinguyen.core.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.khoinguyen.core.dto.validator.EnumPattern;
import com.khoinguyen.core.dto.validator.EnumValue;
import com.khoinguyen.core.dto.validator.GenderSubset;
import com.khoinguyen.core.dto.validator.PhoneNumber;
import com.khoinguyen.core.util.Gender;
import com.khoinguyen.core.util.UserStatus;
import com.khoinguyen.core.util.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import static com.khoinguyen.core.util.Gender.*;

@Builder
@Getter
public class UserRequestDTO {
    private int id;

    @NotBlank(message = "firstName must be not blank") // Khong cho phep gia tri blank
    private String firstName;

    @NotBlank(message = "lastName must be not null") // Khong cho phep gia tri null
    private String lastName;

    @Email(message = "email invalid format") // Chi chap nhan nhung gia tri dung dinh dang email
    private String email;

    //@Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber(message = "phone invalid format")
    private String phone;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    //@Pattern(regexp = "^male|female|other$", message = "gender must be one in {male, female, other}")
    @GenderSubset(anyOf = {MALE, FEMALE, OTHER})
    private Gender gender;

    @NotNull(message = "username must be not null")
    private String username;

    @NotNull(message = "password must be not null")
    private String password;

    @NotNull(message = "type must be not null")
    @EnumValue(name = "type", enumClass = UserType.class)
    private String type;

    @NotEmpty(message = "addresses can not empty")
    private Set<AddressDTO> addresses;

    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;
}
