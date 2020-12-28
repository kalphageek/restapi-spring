package me.kalpha.restapispring.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .build();
        assertNotNull(event);
    }

    @Test
    public void javaBean() {
        //Given
        String name = "Spring";
        //When
        Event event = new Event();
        event.setName("Spring");
        //Then
        assertEquals(event.getName(), name);
    }
}