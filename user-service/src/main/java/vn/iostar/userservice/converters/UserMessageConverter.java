package vn.iostar.userservice.converters;

import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.messaging.converter.DefaultContentTypeResolver;

import vn.iostar.userservice.entity.User;

import java.util.Collections;

public class UserMessageConverter extends JsonMessageConverter {
    public UserMessageConverter() {
        super();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("*");
        typeMapper.setIdClassMapping(Collections.singletonMap("user", User.class));
        this.setTypeMapper(typeMapper);
    }

}
