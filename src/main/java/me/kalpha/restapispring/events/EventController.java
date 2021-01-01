package me.kalpha.restapispring.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
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

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @Autowired
    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
        this.modelMapper = modelMapper;
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
         * 입력감 에러 검증 by EventValidator
         */
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = eventRepository.save(event);
        URI uri = linkTo(this.getClass()).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(uri).body(event);
    }
}
