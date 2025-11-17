package com.development.spring.hGate.H_Gate.security.services;

import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    @Autowired
    private UserRepository userRepository;

    private static final String USER_ID_NOT_FOUND = "User with id %d not found.";

    public Users getById(Integer id) {
        Optional<Users> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw buildUserWithIdNotFoundException(id);
        }
        return user.get();
    }

    private ResponseStatusException buildUserWithIdNotFoundException(Integer id) {
        String message = String.format(USER_ID_NOT_FOUND, id);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
