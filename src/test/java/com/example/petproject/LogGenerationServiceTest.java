package com.example.petproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import com.example.petproject.dto.LogTask;
import com.example.petproject.enums.LogTaskStatus;
import  com.example.petproject.service.LogGenerationService;
import com.example.petproject.utils.LogFileProcessor;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogGenerationServiceTest {
    @Mock
    private LogFileProcessor logFileProcessor;

    @InjectMocks
    private LogGenerationService logGenerationService;

    @Test
    void testStartTaskWithValidDates() {
        // Arrange
        String from = "2023-01-01";
        String to = "2023-01-02";

        // Act
        String taskId = logGenerationService.startTask(from, to);

        // Assert: Task is created correctly
        assertNotNull(taskId, "Task ID should not be null");
        LogTask task = logGenerationService.getStatus(taskId);
        assertNotNull(task, "Fetched task should not be null");
        assertEquals(taskId, task.getId(), "Task id should match the returned id");
        assertEquals(LogTaskStatus.PENDING, task.getStatus(), "Task status should be PENDING");

        // Verify that processRange is called with expected arguments.
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(logFileProcessor, times(1))
                .processRange(eq(from), eq(to), eq(taskId), mapCaptor.capture());

        // Check that the map passed into processRange contains our task.
        Map<String, LogTask> passedMap = mapCaptor.getValue();
        assertTrue(passedMap.containsKey(taskId), "The tasks map should contain the newly created task");
    }

    @Test
    void testStartTaskWithInvalidDateFormat() {
        // Arrange
        String from = "invalid-date";
        String to = "2023-01-02";

        // Act & Assert: Expect an IllegalArgumentException for invalid date formats.
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> logGenerationService.startTask(from, to));
        assertTrue(exception.getMessage().contains("Неверные даты или формат"),
                "Exception message should indicate an error with date format");
    }

    @Test
    void testStartTaskWithInvalidDateRange() {
        // Arrange: "from" date is after "to" date.
        String from = "2023-01-03";
        String to = "2023-01-02";

        // Act & Assert: Expect error for a wrong date range.
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> logGenerationService.startTask(from, to));
        assertTrue(exception.getMessage().contains("Неверный диапазон дат: from > to"),
                "Exception message should indicate an invalid date range");
    }

    @Test
    void testGetStatusForNonExistentTask() {
        // Act & Assert: Retrieving a non-existing task ID should return null.
        LogTask task = logGenerationService.getStatus("non-existent-id");
        assertNull(task, "Expected null when fetching a task that does not exist");
    }
}
