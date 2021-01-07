package me.kalpha.restapispring.events;

import me.kalpha.restapispring.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @SpringBootTest에서 MockMvc를 쓸적에는 @AutoConfigureMockMvc를 함께 적용한다.
 */
public class EventControllerTest extends BaseControllerTest {
    @Autowired
    WebApplicationContext wac;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    ModelMapper modelMapper;

    @DisplayName("정상 : Event 생성")
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
//                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("eventStatus").value(Event.EventStatus.DRAFT.name()))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("free").value(false))
// .andDo에서 Test하기 때문에 제외
//                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_links.query-events").exists())
//                .andExpect(jsonPath("_links.update-event").exists())
                //Rest Docs Snippet에 Link관련 문서조각 추가 생성
                .andDo(document("create-event",
                        //links.adoc 생성
                        links(
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("query-events").description("link to query events api"),
                                linkWithRel("update-event").description("link to update a event api"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        //request-headers.adoc 생성
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
                        ),
                        //request-fields.adoc 생성
                        requestFields(
                                fieldWithPath("name").description("새 Event명"),
                                fieldWithPath("description").description("새 Event설명"),
                                fieldWithPath("beginEnrollmentDateTime").description("등록 시작일시"),
                                fieldWithPath("closeEnrollmentDateTime").description("등록 마감일시"),
                                fieldWithPath("beginEventDateTime").description("Event 시작일시"),
                                fieldWithPath("endEventDateTime").description("Event 종료일시"),
                                fieldWithPath("location").description("새 Event 장소"),
                                fieldWithPath("basePrice").description("새 Event 초기금액"),
                                fieldWithPath("maxPrice").description("새 Event 최대금액"),
                                fieldWithPath("limitOfEnrollment").description("등록 최대금액")
                        ),
                        //response-headers.adoc 생성
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Response Location"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
                        ),
                        //response-fields.adoc 생성, 에러무시
//                        relaxedResponseFields(
                        responseFields(
                                fieldWithPath("id").description("새 Event Id"),
                                fieldWithPath("name").description("새 Event명"),
                                fieldWithPath("description").description("새 Event설명"),
                                fieldWithPath("beginEnrollmentDateTime").description("등록 시작일시"),
                                fieldWithPath("closeEnrollmentDateTime").description("등록 마감일시"),
                                fieldWithPath("beginEventDateTime").description("Event 시작일시"),
                                fieldWithPath("endEventDateTime").description("Event 종료일시"),
                                fieldWithPath("location").description("새 Event 장소"),
                                fieldWithPath("basePrice").description("새 Event 초기금액"),
                                fieldWithPath("maxPrice").description("새 Event 최대금액"),
                                fieldWithPath("limitOfEnrollment").description("등록 최대금액"),
                                fieldWithPath("free").description("무료 여부"),
                                fieldWithPath("offline").description("Online/Offline 여부"),
                                fieldWithPath("eventStatus").description("Event 상태"),
                                fieldWithPath("_links.self.href").description("link to self api"),
                                fieldWithPath("_links.query-events.href").description("link to query events api"),
                                fieldWithPath("_links.update-event.href").description("link to update a event api"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
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
    public void createEvent_wrongProperties() throws Exception {
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
    public void createEvent_emptyInput() throws Exception {
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
    public void createEvent_wrongInput() throws Exception {
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
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @DisplayName("정상 : 3개씩 2번째 페이지, 총 8개")
    @Test
    public void queryEvents() throws Exception {
        IntStream.range(0,8).forEach(this::generateEvent);

        mockMvc.perform(get("/api/events")
                    .param("page", "1")
                    .param("size", "3")
                    .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @DisplayName("정상 : 기존의 Event 조회")
    @Test
    public void getEvent() throws Exception {
        Event event = generateEvent(100);

        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }

    @DisplayName("에러 : 없는 Event 조회")
    @Test
    public void getEvent404() throws Exception {
        mockMvc.perform(get("/api/events/{id}", 1001))
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName("정상 : Event 수정")
    @Test
    public void updateEvent() throws Exception {
        Event generateEvent = generateEvent(101);
        String modifiedName = generateEvent.getName() + " Updated";
        generateEvent.setName(modifiedName);
        EventDto eventDto = modelMapper.map(generateEvent, EventDto.class);

        mockMvc.perform(put("/api/events/{id}", generateEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                    .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(modifiedName))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-event"))
        ;
    }

    @DisplayName("에러 : 수정값이 비어있는 경우")
    @Test
    public void updateEvent_emptyInput() throws Exception {
        Event event = generateEvent(200);
        EventDto eventDto = new EventDto();

        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                    .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("에러 : 수정값이 잘못된 경우")
    @Test
    public void updateEvent_wrongInput() throws Exception {
        Event event = generateEvent(200);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                    .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("에러 : 존재하지 않는 Event 수정")
    @Test
    public void updateEvent_notFound() throws Exception {
        Event event = generateEvent(300);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        mockMvc.perform(put("/api/events/{id}", 300001)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                    .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }


    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("Event " + i)
                .description("Auto generated event")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 12, 29, 19,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 12, 29, 19,10))
                .beginEventDateTime(LocalDateTime.of(2020, 12, 29, 19,10))
                .endEventDateTime(LocalDateTime.of(2020, 12, 30, 19,10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("판교역")
                .free(false)
                .offline(true)
                .eventStatus(Event.EventStatus.DRAFT)
                .build();
        return eventRepository.save(event);
    }
}
