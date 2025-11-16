package com.development.spring.hGate.H_Gate.security.mappers;


import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.security.models.UserSecurityDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel="spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSecurityMapper {

    @Mapping(target = "username", source = "user.email")
    @Mapping(target = "authorities", source = "user.roles")
    @Mapping(target = "isEnabled", expression = "java(true)")
    @Mapping(target = "isCredentialsNonExpired", expression = "java(true)")
    @Mapping(target = "isAccountNonLocked", expression = "java(true)")
    @Mapping(target = "isAccountNonExpired", expression = "java(true)")
    UserSecurityDetails mapToUserSecurityDetails(Users user);

}
