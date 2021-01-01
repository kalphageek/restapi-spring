package me.kalpha.restapispring.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    @Autowired
    EventRepository eventRepository;

    public Event save(Event event) {
        event.update();
        return eventRepository.save(event);
    }
}
