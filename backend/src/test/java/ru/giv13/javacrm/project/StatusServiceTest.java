package ru.giv13.javacrm.project;

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
class StatusServiceTest {
    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusService statusService;

    @Test
    void testGetAllSuccess() {
        // Given
        List<Status> statuses = List.of(
                new Status()
                        .setId(1)
                        .setName(EStatus.NEW)
                        .setDisplayName("Новый"),
                new Status()
                        .setId(2)
                        .setName(EStatus.IN_PROGRESS)
                        .setDisplayName("В работе")
        );

        given(statusRepository.findAll()).willReturn(statuses);

        // When
        List<Status> actualStatuses = statusService.getAll();

        // Then
        assertEquals(actualStatuses.size(), statuses.size());
        then(statusRepository).should().findAll();
    }
}