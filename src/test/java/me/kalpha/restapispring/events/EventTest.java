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

    @Test
    public void testOffline() {
        //Given
        Event event = Event.builder()
                .location("강남역")
                .build();
        //When
        event.update();
        //Then
        assertEquals(event.isOffline(), true);

        //Given
        event = Event.builder()
                .build();
        //When
        event.update();
        //Then
        assertEquals(event.isOffline(), false);
    }

    @Test
    public void testFree() {
        //Given
        Event event = Event.builder()
                .basePrice(100)
                .build();
        //When
        event.update();
        //Then
        assertEquals(event.isFree(), false);

        //Given
        event = Event.builder()
                .maxPrice(100)
                .build();
        //When
        event.update();
        //Then
        assertEquals(event.isFree(), false);

        //Given
        event = Event.builder()
                .build();
        //When
        event.update();
        //Then
        assertEquals(event.isFree(), true);
    }
}