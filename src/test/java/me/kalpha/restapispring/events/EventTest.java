package me.kalpha.restapispring.events;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventTest {

    @Disabled
    @DisplayName("Builder 패턴 테스트")
    @Test
    public void builder() {
        Event event = Event.builder()
                .build();
        assertNotNull(event);
    }

    @DisplayName("Java Original Bean 설정 테스트")
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