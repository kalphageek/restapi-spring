package me.kalpha.restapispring.events;

import me.kalpha.restapispring.index.IndexController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * produces = MediaTypes.HAL_JSON_VALUE ==> 입력값이 HAL_JSON 이다.
 * Test에서 한글이 깨져서 produces를 임시로 변경 하였다.
 */
@RestController
//@RequestMapping(value = "/api/events", produces = "application/hal+json; charset=UTF-8")
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
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
         * JSR 303 에러 검증 (@NotNULL, @NotEmpty, @Min, @Max, 등 )
         */
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        /**
         * 입력값 에러 검증 by EventValidator
         */
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
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
                .add(linkTo(this.getClass()).withRel("query-events"))
                .add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(selfLinkBuilder.toUri()).body(eventModel);
    }

    private ResponseEntity<EntityModel> badRequest(Errors errors) {
        EntityModel<Errors> errorsModel = EntityModel.of(errors)
                .add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        return ResponseEntity.badRequest().body(errorsModel);
    }
}
