package me.kalpha.restapispring.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * produces = MediaTypes.HAL_JSON_VALUE ==> 입력값이 HAL_JSON 이다.
 */
@RestController
@RequestMapping(value = "/api/events", produces = "application/hal+json; charset=UTF-8")
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @Autowired
    public EventController(EventService eventService, EventValidator eventValidator) {
        this.eventService = eventService;
        this.eventValidator = eventValidator;
    }

    /**
     * @Valid Annotation 파라미터 우측에 Errors 객체를 파라미터로 넘기면 거기에 Validation한 결과를 넣어준다.
     * @param eventDto
     * @param errors
     * @return
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        /**
         * JSR 303 에러 검증
         */
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        /**
         * 입력값 에러 검증 by EventValidator
         */
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        /**
         * eventDto 저장
         */
        Event event = eventService.save(eventDto);

        /**
         * Spring Hateoas 적용
         */
        WebMvcLinkBuilder selfLinkBuilder = linkTo(this.getClass()).slash(event.getId());
        EntityModel<Event> eventModel = EntityModel.of(event)
                .add(selfLinkBuilder.withSelfRel())
                .add(selfLinkBuilder.withRel("update-event"))
                .add(linkTo(this.getClass()).withRel("query-events"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(eventModel);
    }
}
