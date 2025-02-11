package ru.giv13.infocrm.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.giv13.infocrm.model.*;
import ru.giv13.infocrm.repository.PermissionRepository;
import ru.giv13.infocrm.repository.RoleRepository;
import ru.giv13.infocrm.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public Set<User> usersDemo(PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository) {
        Set<Permission> userPermissions = new HashSet<>(Set.of(
            Permission.builder().name(EPermisson.PROJECT_READ).displayName("Проекты:чтение").build(),
            Permission.builder().name(EPermisson.PROJECT_CREATE).displayName("Проекты:создание").build(),
            Permission.builder().name(EPermisson.PROJECT_UPDATE).displayName("Проекты:обновление").build()
        ));
        permissionRepository.saveAll(userPermissions);
        Role userRole = Role.builder().name(ERole.USER).displayName("Пользователь").permissions(userPermissions).build();
        roleRepository.save(userRole);

        Set<Permission> adminPermissions = new HashSet<>(Set.of(
            Permission.builder().name(EPermisson.PROJECT_DELETE).displayName("Проекты:удаление").build(),
            Permission.builder().name(EPermisson.USER_READ).displayName("Пользователи:чтение").build(),
            Permission.builder().name(EPermisson.PROJECT_CREATE).displayName("Пользователи:создание").build(),
            Permission.builder().name(EPermisson.PROJECT_UPDATE).displayName("Пользователи:обновление").build(),
            Permission.builder().name(EPermisson.USER_DELETE).displayName("Пользователи:удаление").build()
        ));
        permissionRepository.saveAll(adminPermissions);
        adminPermissions.addAll(userPermissions);
        Role adminRole = Role.builder().name(ERole.ADMIN).displayName("Администратор").permissions(adminPermissions).build();
        roleRepository.save(adminRole);

        Set<User> users = Set.of(
            User.builder().name("Админ").email("admin@mail.ru").roles(Set.of(adminRole)).password(passwordEncoder().encode("admin")).build(),
            User.builder().name("Юзер").email("user@mail.ru").roles(Set.of(userRole)).password(passwordEncoder().encode("user")).build()
        );
        userRepository.saveAll(users);
        return users;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
