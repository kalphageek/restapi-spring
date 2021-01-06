package me.kalpha.restapispring.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    EventRepository eventRepository;

    public Event save(EventDto eventDto, Event event) {
        modelMapper.map(eventDto, event);
        event.update();
        return eventRepository.save(event);
    }

    public Event save(EventDto eventDto) {
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        return eventRepository.save(event);
    }

    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Optional<Event> getEvent(Integer id) {
        return eventRepository.findById(id);
    }
}
