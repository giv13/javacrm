package ru.giv13.javacrm.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.giv13.javacrm.system.ExceptionHandlerAdvice;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {
    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).setControllerAdvice(ExceptionHandlerAdvice.class).build();
    }

    @Test
    void testGetAllSuccess() throws Exception {
        // Given
        List<Role> roles = List.of(
                new Role().setId(1).setName(ERole.USER).setDisplayName("Пользователь"),
                new Role().setId(2).setName(ERole.ADMIN).setDisplayName("Администратор")
        );

        given(roleService.getAll()).willReturn(roles);

        // When and then
        mockMvc.perform(get("/roles").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data", Matchers.hasSize(roles.size())))
                .andExpect(jsonPath("$.data[0].name").value(ERole.USER.name()))
                .andExpect(jsonPath("$.data[0].displayName").value("Пользователь"));
    }
}