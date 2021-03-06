package me.kalpha.restapispring.events;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
public class Event {
    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;

    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void update() {
        if (location == null || location.isBlank() ) {
            offline = false;
        } else {
            offline = true;
        }

        if (basePrice == 0 && maxPrice == 0) {
            free = true;
        } else {
            free = false;
        }
    }

    enum EventStatus {
        DRAFT, PUBLISHED, BEGAN_ENROLLMEND, CLOSED_ENROLLMENT, STARTED, ENDED
    }
}

