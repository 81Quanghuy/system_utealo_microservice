package vn.iotstart.userservice.service;


import vn.iotstart.userservice.entity.User;

public interface MapperService {
    UserDto mapToUserDto(User user);
    CredentialDto mapToCredentialDto(Credential credential);
    TokenDto mapToTokenDto(Token token);
    RelationshipDto mapToRelationDto(Relationship relationship);
    AnotherUserDto mapToAnotherUserDto(User user);
    SimpleUserDto mapToSimpleUserDto(User user);

    AddressDto mapToAddressDto(Province province);
    AddressDto mapToAddressDto(District district);
    AddressDto mapToAddressDto(School school);
}
