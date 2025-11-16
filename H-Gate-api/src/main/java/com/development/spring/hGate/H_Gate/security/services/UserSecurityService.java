package com.development.spring.hGate.H_Gate.security.services;

import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import com.development.spring.hGate.H_Gate.security.mappers.UserSecurityMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    @Autowired
    private UserSecurityMapper userSecurityMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginAttemptService loginAttemptService;

    private final Logger logger = LoggerFactory.getLogger(UserSecurityService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Users> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            logger.debug("Username " + username + " not found.");
            throw new UsernameNotFoundException(username);
        } else {
            logger.debug("Username " + username + " found!");
            return userSecurityMapper.mapToUserSecurityDetails(userOptional.get());
        }
    }

}