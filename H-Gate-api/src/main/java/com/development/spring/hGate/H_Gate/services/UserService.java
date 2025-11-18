package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.components.UserSpecificationsFactory;
import com.development.spring.hGate.H_Gate.dtos.UserRegistrationDTO;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.utils.ComparableWrapper;
import com.development.spring.hGate.H_Gate.libs.utils.Pair;
import com.development.spring.hGate.H_Gate.mappers.UserMapper;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import com.development.spring.hGate.H_Gate.security.services.LoginAttemptService;
import com.development.spring.hGate.H_Gate.security.services.SessionService;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;

@Service
public class UserService extends BasicService {

    private final LoginAttemptService loginAttemptService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserSpecificationsFactory userSpecificationsFactory;
    private final EmailService emailService;
    private static final String USER_ID_NOT_FOUND = "User with id %d not found.";

    private final Map<String, Function<Users, ComparableWrapper>> sortingFields = new HashMap<>() {{
        put("name", user -> user.getNome() != null ? new ComparableWrapper(user.getNome()) : null);
        put("surname", user -> user.getCognome() != null ? new ComparableWrapper(user.getCognome()) : null);
        put("roles", user -> user.getRoles() != null ? new ComparableWrapper(user.getRoles()) : null);
    }};


    public UserService(LoginAttemptService loginAttemptService, UserMapper userMapper, SessionService sessionService, UserRepository userRepository, PasswordEncoder passwordEncoder, UserSpecificationsFactory userSpecificationsFactory, EmailService emailService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSpecificationsFactory = userSpecificationsFactory;
        this.emailService = emailService;
        this.loginAttemptService = loginAttemptService;
    }

    public Users create(Users user) {
        user.setId(null);
        String tempPassword = PasswordTokenService.generateRandomString();
        user.setPassword(tempPassword);
        Users newUser = save(user);
       // emailService.sendTempPasswordEmail(user.getEmail(), tempPassword);

        return newUser;
    }

    public Users resetPassword(Integer id) {
        Optional<Users> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            Users user = optUser.get();
            String tempPassword = PasswordTokenService.generateRandomString();
            user.setPassword(tempPassword);
            userRepository.save(user);
            //emailService.sendTempPasswordEmail(user.getEmail(), tempPassword);
            return user;
        }
        return null;
    }

    @Transactional
    public Page<Users> searchAdvanced(Optional<Filter<Users>> filter, Pageable pageable) {
        try {

            Pair<Boolean, String> sortingInfo = isSortedOnNonDirectlyMappedField(sortingFields, pageable);
            boolean isSorted = sortingInfo.getFirst();
            String sortingProperty = sortingInfo.getSecond();
            Page<Users> usersPage;

            if(isSorted) {
                List<Users> users = filter.map(userFilter ->
                        userRepository.findAll(getSpecificationForAdvancedSearch(userFilter))
                ).orElseGet(userRepository::findAll);

                usersPage = getPage(sortingFields, users, pageable, sortingProperty);

            } else {
                usersPage = filter.map(userFilter ->
                        userRepository.findAll(getSpecificationForAdvancedSearch(userFilter), pageable)
                ).orElseGet(() -> userRepository.findAll(pageable));

            }

            usersPage = removeDuplicates(usersPage);
            return applyRoleVisibilityFilter(usersPage);

        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Page<Users> applyRoleVisibilityFilter(Page<Users> usersPage) {

        List<Users> filteredUsers = new ArrayList<>();

        for (Users user : usersPage.getContent()) {
            if (!user.getRoles().contains(Role.AMMINISTRATORE))
                filteredUsers.add(user);
        }
        return new PageImpl<>(filteredUsers, usersPage.getPageable(), usersPage.getTotalElements());
    }



    private Specification<Users> getSpecificationForAdvancedSearch(Filter<Users> userFilter){
        return userFilter.toSpecification(userSpecificationsFactory);
    }

    public Users update(Users user) {

        Optional<Users> oldUserOptional = userRepository.findById(user.getId());
        if (oldUserOptional.isEmpty())
            throw buildEntityWithIdNotFoundException(user.getId(), USER_ID_NOT_FOUND);

        return save(user);
    }

    public Users partialUpdate(Users user) {

        Optional<Users> oldUserOptional = userRepository.findById(user.getId());
        if (oldUserOptional.isEmpty()) {
            throw buildEntityWithIdNotFoundException(user.getId(), USER_ID_NOT_FOUND);
        }
        Users oldUser = oldUserOptional.get();

//        if(!hasUserPermissionToChangeRoles(oldUser.getRoles()))
//            throw buildDumCannotModifyPermissionsException();
//
//        if(isUserEnabled(user))
//            loginAttemptService.clearCache(oldUser.getEmail());

        userMapper.updateModel(user, oldUser);
        return saveFromPartialUpdate(oldUser, user);
    }

    public Users save(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return save(userRepository, user);
    }

    public Users getById(Integer id) {
        return getById(userRepository, id, USER_ID_NOT_FOUND);
    }

    public void deleteById(Integer id) {
        Optional<Users> oldUserOptional = userRepository.findById(id);

        if (oldUserOptional.isEmpty())
            throw buildEntityWithIdNotFoundException(id, USER_ID_NOT_FOUND);

        Users oldUser = oldUserOptional.get();


        deleteById(userRepository, id);
    }

    public List<Users> getAll(){
        return getAll(userRepository);
    }

    private Users saveFromPartialUpdate(Users oldUser, Users user) {
        if (user.getPassword() != null) {
            oldUser.setPassword(passwordEncoder.encode(oldUser.getPassword()));
        }
        try {
            return userRepository.save(oldUser);
        } catch (DataIntegrityViolationException ex) {
            String message = CONSTRAINT_VIOLATION;
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    public boolean existsByUserId(Long userId){
        Optional<Users> optionalUser = userRepository.findByUserId(userId);
        return optionalUser.isPresent();
    }

    public Users firstRegistration(UserRegistrationDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use.");
        }

        Set<Role> assignedRoles = new HashSet<>();
        assignedRoles.add(Role.PAZIENTE);

        Users user = Users.builder()
                .nome(dto.getNome())
                .cognome(dto.getCognome())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPasswordHash()))
                .roles(assignedRoles)
                .build();

//        Cliente cliente = Cliente.builder()
//                .codiceFiscale(dto.getCodiceFiscale())
//                .dataRegistrazione(new Date())
//                .indirizzo(dto.getIndirizzo())
//                .telefono(dto.getTelefono())
//                .dataNascita(dto.getDataNascita())
//                .user(user)
//                .build();
//
//        user.setCliente(cliente);

        return save(user);
    }

}
