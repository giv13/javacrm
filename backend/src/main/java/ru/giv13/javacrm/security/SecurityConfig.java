package ru.giv13.javacrm.security;

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
import ru.giv13.javacrm.project.*;
import ru.giv13.javacrm.user.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    public List<User> usersDemo(PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository, StatusRepository statusRepository, ProjectRepository projectRepository) {
        Set<Permission> userPermissions = new HashSet<>(Set.of(
                (new Permission()).setName(EPermisson.PROJECT_READ).setDisplayName("Проекты:чтение"),
                (new Permission()).setName(EPermisson.PROJECT_CREATE).setDisplayName("Проекты:создание"),
                (new Permission()).setName(EPermisson.PROJECT_UPDATE).setDisplayName("Проекты:обновление")
        ));
        permissionRepository.saveAll(userPermissions);
        Role userRole = (new Role()).setName(ERole.USER).setDisplayName("Пользователь").setPermissions(userPermissions);
        roleRepository.save(userRole);

        Set<Permission> adminPermissions = new HashSet<>(Set.of(
                (new Permission()).setName(EPermisson.PROJECT_DELETE).setDisplayName("Проекты:удаление"),
                (new Permission()).setName(EPermisson.USER_READ).setDisplayName("Пользователи:чтение"),
                (new Permission()).setName(EPermisson.USER_CREATE).setDisplayName("Пользователи:создание"),
                (new Permission()).setName(EPermisson.USER_UPDATE).setDisplayName("Пользователи:обновление"),
                (new Permission()).setName(EPermisson.USER_DELETE).setDisplayName("Пользователи:удаление")
        ));
        permissionRepository.saveAll(adminPermissions);
        adminPermissions.addAll(userPermissions);
        Role adminRole = (new Role()).setName(ERole.ADMIN).setDisplayName("Администратор").setPermissions(adminPermissions);
        roleRepository.save(adminRole);

        List<User> users = List.of(
                (new User()).setName("Админ").setUsername("admin").setEmail("admin@mail.ru").setRoles(Set.of(userRole, adminRole)).setPassword(passwordEncoder().encode("admin")),
                (new User()).setName("Юзер").setUsername("user").setEmail("user@mail.ru").setRoles(Set.of(userRole)).setPassword(passwordEncoder().encode("user"))
        );
        userRepository.saveAll(users);

        List<Status> statuses = new ArrayList<>(List.of(
                (new Status()).setName(EStatus.NEW).setDisplayName("Новый"),
                (new Status()).setName(EStatus.IN_PROGRESS).setDisplayName("В работе"),
                (new Status()).setName(EStatus.COMPLETED).setDisplayName("Завершенный"),
                (new Status()).setName(EStatus.ARCHIVED).setDisplayName("Архивный")
        ));
        statusRepository.saveAll(statuses);

        List<Project> projects = new ArrayList<>(List.of(
                (new Project()).setName("Проект 1").setDescription("Описание проекта 1").setStatus(statuses.get(0)).setResponsible(users.get(0)).setParticipants(Set.of(users.get(0), users.get(1))),
                (new Project()).setName("Проект 2").setDescription("Описание проекта 2").setStatus(statuses.get(1)).setResponsible(users.get(1)),
                (new Project()).setName("Проект 3").setDescription("Описание проекта 3").setStatus(statuses.get(2)).setResponsible(users.get(1)).setParticipants(Set.of(users.get(0))),
                (new Project()).setName("Проект 4").setDescription("Описание проекта 4").setStatus(statuses.get(3)).setResponsible(users.get(0))
        ));
        projectRepository.saveAll(projects);

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
