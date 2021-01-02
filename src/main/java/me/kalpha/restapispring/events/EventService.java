package me.kalpha.restapispring.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    EventRepository eventRepository;

    public Event save(EventDto eventDto) {
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        return eventRepository.save(event);
    }
}
