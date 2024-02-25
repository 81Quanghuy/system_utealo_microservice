package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.*;
import com.trvankiet.app.entity.*;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapperServiceImpl implements MapperService {
    @Override
    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().toString())
                .gender(user.getGender().toString())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(user.getDob())
                .avatarUrl(user.getAvatarUrl())
                .coverUrl(user.getCoverUrl())
                .credentialDto(this.mapToCredentialDto(user.getCredential()))
                .district(user.getDistrict() != null ? user.getDistrict() : null)
                .province(user.getProvince() != null ? user.getProvince() : null)
                .school(user.getSchool() != null ? user.getSchool() : null)
                .grade(user.getGrade() != null ? user.getGrade() : null)
                .subjects(user.getSubjects())
                .parents(user.getParents().isEmpty() ?
                        null : user.getParents().stream().map(this::mapToAnotherUserDto).toList())
                .children(user.getStudents().isEmpty() ?
                        null : user.getStudents().stream().map(this::mapToAnotherUserDto).toList())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public CredentialDto mapToCredentialDto(Credential credential) {
        return CredentialDto.builder()
                .username(credential.getUsername())
                .password(credential.getPassword())
                .isEnabled(credential.getIsEnabled())
                .isAccountNonExpired(credential.getIsAccountNonExpired())
                .isAccountNonLocked(credential.getIsAccountNonLocked())
                .isCredentialsNonExpired(credential.getIsCredentialsNonExpired())
                .lockedAt(credential.getLockedAt())
                .lockedReason(credential.getLockedReason())
                .role(credential.getRole().getName())
                .provider(credential.getProvider().getName())
                .createdAt(credential.getCreatedAt())
                .updatedAt(credential.getUpdatedAt())
                .build();
    }

    @Override
    public TokenDto mapToTokenDto(Token token) {
        return TokenDto.builder()
                .token(token.getToken())
                .type(token.getType().getCode())
                .is_expired(token.getIsExpired())
                .is_revoked(token.getIsRevoked())
                .expiredAt(token.getExpiredAt())
                .createdAt(token.getCreatedAt())
                .updatedAt(token.getUpdatedAt())
                .build();
    }

    @Override
    public RelationshipDto mapToRelationDto(Relationship relationship) {
        return RelationshipDto.builder()
                .id(relationship.getId())
                .parentDto(this.mapToSimpleUserDto(relationship.getParent()))
                .studentDto(this.mapToSimpleUserDto(relationship.getChild()))
                .isAccepted(relationship.getIsAccepted())
                .build();
    }

    @Override
    public AnotherUserDto mapToAnotherUserDto(User user) {
        return AnotherUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().toString())
                .gender(user.getGender().toString())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(user.getDob())
                .avatarUrl(user.getAvatarUrl())
                .coverUrl(user.getCoverUrl())
                .credentialDto(this.mapToCredentialDto(user.getCredential()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public SimpleUserDto mapToSimpleUserDto(User user) {
        return SimpleUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().toString())
                .gender(user.getGender().toString())
                .email(user.getEmail())
                .dob(DateUtil.date2String(user.getDob(), AppConstant.LOCAL_DATE_FORMAT))
                .build();
    }

    @Override
    public AddressDto mapToAddressDto(Province province) {
        return AddressDto.builder()
                .id(province.getId())
                .code(province.getCode())
                .name(province.getName())
                .description(province.getDescription())
                .createdAt(DateUtil.date2String(province.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT_WITHOUT_MILLIS))
                .updatedAt(province.getUpdatedAt() == null
                        ? null : DateUtil.date2String(province.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT_WITHOUT_MILLIS))
                .build();
    }

    @Override
    public AddressDto mapToAddressDto(District district) {
        return AddressDto.builder()
                .id(district.getId())
                .code(district.getCode())
                .name(district.getName())
                .description(district.getDescription())
                .createdAt(DateUtil.date2String(district.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT_WITHOUT_MILLIS))
                .updatedAt(district.getUpdatedAt() == null
                        ? null : DateUtil.date2String(district.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT_WITHOUT_MILLIS))
                .build();
    }

    @Override
    public AddressDto mapToAddressDto(School school) {
        return AddressDto.builder()
                .id(school.getId())
                .code(school.getCode())
                .name(school.getName())
                .description(school.getDescription())
                .createdAt(DateUtil.date2String(school.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT_WITHOUT_MILLIS))
                .updatedAt(school.getUpdatedAt() == null
                        ? null : DateUtil.date2String(school.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT_WITHOUT_MILLIS))
                .build();
    }
}
