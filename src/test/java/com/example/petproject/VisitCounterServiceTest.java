package com.example.petproject;

import static org.junit.jupiter.api.Assertions.*;
import com.example.petproject.service.VisitCounterService;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisitCounterServiceTest {

    private VisitCounterService visitCounterService;

    @BeforeEach
    void setUp() {
        visitCounterService = new VisitCounterService();
    }

    @Test
    void testIncrementSingleUri() {
        String uri = "/home";
        // Before any increments, count should be 0.
        assertEquals(0, visitCounterService.getCount(uri), "Initial count for an unknown URI should be 0");

        // Increment the URI and check the count.
        visitCounterService.increment(uri);
        assertEquals(1, visitCounterService.getCount(uri), "Count should be 1 after a single increment");
        visitCounterService.increment(uri);
        assertEquals(2, visitCounterService.getCount(uri), "Count should be 2 after two increments");
    }

    @Test
    void testGetCountForNonExistingUri() {
        String uri = "/non-existing";
        // The count for a non-existing URI should be 0.
        assertEquals(0, visitCounterService.getCount(uri), "Non-existing URI count should be 0");
    }

    @Test
    void testGetAllCounts() {
        String uri1 = "/home";
        String uri2 = "/about";
        // Increment counts for different URIs.
        visitCounterService.increment(uri1);
        visitCounterService.increment(uri1);
        visitCounterService.increment(uri2);

        Map<String, Integer> allCounts = visitCounterService.getAllCounts();

        // Verify the counts in the returned map.
        assertEquals(2, allCounts.get(uri1), "Expected count for /home is 2");
        assertEquals(1, allCounts.get(uri2), "Expected count for /about is 1");
        assertEquals(2, allCounts.size(), "The counts map should contain exactly 2 entries");
    }

    @Test
    void testReset() {
        String uri = "/home";
        visitCounterService.increment(uri);
        // Verify count is non-zero after an increment.
        assertEquals(1, visitCounterService.getCount(uri), "Count should be 1 after a single increment");

        // Reset the counters.
        visitCounterService.reset();

        // After reset, getCount should return 0 and the map should be empty.
        assertEquals(0, visitCounterService.getCount(uri), "After reset, count should be 0");
        assertTrue(visitCounterService.getAllCounts().isEmpty(), "After reset, the counts map should be empty");
    }

    @Test
    void testConcurrentIncrement() throws InterruptedException {
        String uri = "/concurrent";
        int numberOfThreads = 10;
        int incrementsPerThread = 100;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        try {
            // Submit concurrent tasks that increment the same URI.
            for (int i = 0; i < numberOfThreads; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        visitCounterService.increment(uri);
                    }
                });
            }
        } finally {
            executor.shutdown();
            boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
            assertTrue(terminated, "Executor did not terminate in time");
        }

        // The final count should match the total increments.
        int expectedCount = numberOfThreads * incrementsPerThread;
        assertEquals(expectedCount, visitCounterService.getCount(uri), "Concurrent increments did not result in the expected count");
    }
}

