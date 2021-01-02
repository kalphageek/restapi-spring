package me.kalpha.restapispring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kalpha.restapispring.common.RestDocsConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @SpringBootTest에서 MockMvc를 쓸데는 @AutoConfigureMockMvc를 함께 적용한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("정상 : Event생성")
    @Test
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
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
                .build();

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("eventStatus").value(Event.EventStatus.DRAFT.name()))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event"))
        ;
    }

    /**
     * andExpect(status().isBadRequest()) --> "Controller에서 Dto를 받는데, Dto가 받을 수 없는 속성이 있으면 에러를 발생시켜라"
     * application.yml에 아래가 설정되어 있으면 체크가 가능해진다.
     *      spring:
     *        jackson:
     *          deserialization:
     *            fail-on-unknown-properties: true
     * @throws Exception
     */
    @DisplayName("에러 : 잘못된 속성을 가지는 요청")
    @Test
    public void createEvent_badRequest() throws Exception {
        Event event = Event.builder()
                .id(100) //자동설정값
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
                .free(true) //계산값
                .offline(false) //계산값
                .eventStatus(Event.EventStatus.PUBLISHED) //자동설정값
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    /**
     * 값이 없는데도 불구하고 Status 201이 리턴됨 -> 이를 Validation하기 위해 JSR 303 Annotation을 이용해 Validation한다.
    * @throws Exception
     */
    @DisplayName("에러 : 입력값이 비어있는 경우")
    @Test
    public void createEvent_badRequest_emptyInput() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;
    }


    /**
     * Errors Body의 JSON값 레코드가 2개이상 이다
     * $[0].field ==> 그 중 첫번째 레코드의 field 항목을 나타낸다.
     *
     * @throws Exception
     */
    @DisplayName("에러 : 잘못된 값 입력")
    @Test
    public void createEvent_badRequest_wrongInput() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("Rest API 개발 Test")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 12, 29, 19,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 12, 29, 19,10))
                .beginEventDateTime(LocalDateTime.of(2020, 12, 29, 19,10))
                .endEventDateTime(LocalDateTime.of(2020, 12, 20, 19,10))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                // Field error
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        ;
    }
}
