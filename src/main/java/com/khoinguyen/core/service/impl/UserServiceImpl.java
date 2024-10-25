package com.khoinguyen.core.service.impl;

import com.khoinguyen.core.dto.request.UserRequestDTO;
import com.khoinguyen.core.exception.ResourceNotFoundException;
import com.khoinguyen.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Override
    public int addUser(UserRequestDTO user) {
        log.info("UserServiceImpl addUser");
        if(!user.getFirstName().equals("Tay")) {
            throw  new ResourceNotFoundException("Tay is not exist");
        }
        return 0;
    }
}
