package me.kalpha.restapispring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @SpringBootTest에서 MockMvc를 쓸데는 @AutoConfigureMockMvc를 함께 적용한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
                .id(100)//자동설정 값. 받으면 안됨
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
                .free(true)//계산 값. 받으면 안됨
                .offline(true)//계산 값. 받으면 안됨
                .eventStatus(Event.EventStatus.PUBLISHED)//자동설정 값. 받으면 안됨
                .build();

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("eventStatus").value(Event.EventStatus.DRAFT.name()))
                .andExpect(jsonPath("offline").value(Matchers.not(true)))
                .andExpect(jsonPath("free").value(Matchers.not(true)));

    }

    /**
     * andExpect(status().isBadRequest()) --> "Controller에서 Dto를 받는데, Dto가 받을 수 없는 값이 있으면 에러를 발생시켜라"
     * application.yml에 아래가 설정되어 있으면 체크가 가능해진다.
     * spring:
     *   jackson:
     *     deserialization:
     *       .fail-on-unknown-properties: true
     */
    @Test
    public void createEvent_badRequest() throws Exception {
        Event event = Event.builder()
                .id(100)//자동설정 값. 받으면 안됨
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
                .free(true)//계산 값. 받으면 안됨
                .offline(true)//계산 값. 받으면 안됨
                .eventStatus(Event.EventStatus.PUBLISHED)//자동설정 값. 받으면 안됨
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
