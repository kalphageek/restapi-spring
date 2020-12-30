package me.kalpha.restapispring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @WebMvcTest는 Web용 slice 테스트여서 Repository는 bean으로 등록하지 않는다.
 * 이를 보완하기우해 EventRepository를 MockBean으로 등록할 수 있다.
 */

@WebMvcTest
public class EventControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * MockBean은 save 되더라도 null이다
     */
    @MockBean
    EventRepository eventRepository;

    @DisplayName("Event 생성 테스트")
    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("Rest API 개발 Test")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 12, 29, 19,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 12, 29, 19,10))
                .beginEventDateTime(LocalDateTime.of(2020, 12, 29, 19,10))
                .endEventDateTime(LocalDateTime.of(2020, 12, 30, 19,10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .id(10)
                .build();

        /**
         * post("/api/events")가 호출되면 eventRepository.save(event)가 호출될텐데 이 때 event를 리턴하라.
         *      (eventRepository는 MockBean이라서 실제로는 null을 리턴하며,
         *      그러면 jsonPath를 나오는 값도 null이라서 이를 대체하기 위해 Mockito를 사용한다)
         */
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated()) //201
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE));
    }
}