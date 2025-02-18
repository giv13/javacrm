package ru.giv13.infocrm.user.dto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import ru.giv13.infocrm.user.User;

import java.util.stream.Collectors;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User source) {
        return new UserDto(
                source.getName(),
                source.getUsername(),
                source.getEmail(),
                source.getImage(),
                source.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())
        );
    }
}
