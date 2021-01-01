package me.kalpha.restapispring.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

/**
 * 입력값 오류를 검증하기 위한 Class
 * 입력값 오류가 있으면 rejectValue를 사용해 에러내용을 등록한다.
 */
@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
            errors.rejectValue("basePrice", "wrongValue", "basePrice 또는 maxPrice 가 잘못되었습니다.");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime가 ㅠeginEventDateTime / ㅠeginEnrollmentDateTime / ㅊloseEnrollmentDateTime 보다 빠를 수 없습니다.");
        }
    }
}
