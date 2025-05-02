package ru.giv13.javacrm.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void testGetAllSuccess() {
        // Given
        List<Role> roles = List.of(
                new Role()
                        .setId(1)
                        .setName(ERole.USER)
                        .setDisplayName("Пользователь"),
                new Role()
                        .setId(2)
                        .setName(ERole.ADMIN)
                        .setDisplayName("Администратор")
        );

        given(roleRepository.findAll()).willReturn(roles);

        // When
        List<Role> actualRoles = roleService.getAll();

        // Then
        assertEquals(actualRoles.size(), roles.size());
        then(roleRepository).should().findAll();
    }
}