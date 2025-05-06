package ru.giv13.javacrm.project;

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
class StatusControllerTest {
    @Mock
    private StatusService statusService;

    @InjectMocks
    StatusController statusController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statusController).setControllerAdvice(ExceptionHandlerAdvice.class).build();
    }

    @Test
    void testGetAllSuccess() throws Exception {
        // Given
        List<Status> statuses = statuses = List.of(
                new Status().setId(1).setName(EStatus.NEW).setDisplayName("Новый"),
                new Status().setId(2).setName(EStatus.IN_PROGRESS).setDisplayName("В работе")
        );

        given(statusService.getAll()).willReturn(statuses);

        // When and then
        mockMvc.perform(get("/statuses").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data", Matchers.hasSize(statuses.size())))
                .andExpect(jsonPath("$.data[0].name").value(EStatus.NEW.name()))
                .andExpect(jsonPath("$.data[0].displayName").value("Новый"));
    }
}