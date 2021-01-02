package me.kalpha.restapispring.events;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JUnit Method Params는 단위 테스트 메소드에 파라미터를 전달할 수 있도록 해준다. (원래는 안됨)
 */
class EventTest {

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

    static Stream<Arguments> testOfflineParams() {
        return Stream.of(
                Arguments.of("강남역", true),
                Arguments.of(null, false),
                Arguments.of(" ", false)
        );
    }

    @DisplayName("ParamterizedTest인 testOffline")
    @ParameterizedTest
    @MethodSource("testOfflineParams")
    public void testOffline(String location, boolean offline) {
        //Given
        Event event = Event.builder()
                .location(location)
                .build();
        //When
        event.update();
        //Then
        assertEquals(event.isOffline(), offline);
    }

    static Stream<Arguments> testFreeParams() {
        return Stream.of(
                Arguments.of(100,0,false),
                Arguments.of(0,100,false),
                Arguments.of(100,100,false),
                Arguments.of(0,0,true)
        );
    }

    @DisplayName("ParamterizedTest인 testFree")
    @ParameterizedTest
    @MethodSource("testFreeParams")
    public void testFree(int basePrice, int maxPrice, boolean free) {
        //Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        //When
        event.update();
        //Then
        assertEquals(event.isFree(), free);
    }

    @DisplayName("ParamterizedTest가 아닌 경우 testFree")
    @Test
    public void testFree_org() {
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