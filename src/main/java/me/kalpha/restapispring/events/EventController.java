package me.kalpha.restapispring.events;

import me.kalpha.restapispring.common.ErrorsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        /**
         * 입력값 에러 검증 by EventValidator
         */
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }

        Event event = eventService.save(eventDto);
        /**
         * Spring Hateoas 적용
         */
        WebMvcLinkBuilder selfLinkBuilder = linkTo(this.getClass()).slash(event.getId());
        EntityModel<Event> eventModel = EventModel.modelOf(event);
        eventModel.add(selfLinkBuilder.withRel("update-event"))
                .add(linkTo(this.getClass()).withRel("query-events"))
                .add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(selfLinkBuilder.toUri()).body(eventModel);
    }

    /**
     *
     * @param pageable
     * @param assembler 를 통해 EventModel을 설정
     * @return
     */
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<Event> page = eventService.findAll(pageable);
        PagedModel pagedResource = assembler.toModel(page, e -> EventModel.modelOf((Event) e));
        pagedResource.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedResource);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> eventOptional = eventService.getEvent(id);
        if (eventOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<Event> eventModel = EventModel.modelOf(eventOptional.get());
        eventModel.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors) {
        Optional<Event> eventOptional = eventService.getEvent(id);
        if (eventOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        Event existEvent = eventOptional.get();
        Event event = eventService.save(eventDto, existEvent);
        EntityModel<Event> eventModel = EventModel.modelOf(event);
        eventModel.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventModel);
    }
}
