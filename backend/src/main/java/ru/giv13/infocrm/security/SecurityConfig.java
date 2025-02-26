package ru.giv13.infocrm.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.giv13.infocrm.user.*;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final DelegatedAuthenticationEntryPoint delegatedAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(delegatedAuthenticationEntryPoint))
                .build();
    }

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
                User.builder().name("Админ").username("admin").email("admin@mail.ru").roles(Set.of(adminRole)).password(passwordEncoder().encode("admin")).build(),
                User.builder().name("Юзер").username("user").email("user@mail.ru").roles(Set.of(userRole)).password(passwordEncoder().encode("user")).build()
        );
        userRepository.saveAll(users);
        return users;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
