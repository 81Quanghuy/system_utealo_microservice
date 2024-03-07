package vn.iostar.groupservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.groupservice.dto.request.CreateEventRequest;
import vn.iostar.groupservice.dto.request.ModifyEventRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;

import java.text.ParseException;

public interface EventService {
    ResponseEntity<GenericResponse> createEvent(String userId, CreateEventRequest createEventRequest) throws ParseException;

    ResponseEntity<GenericResponse> getEvents(String userId, String groupId);

    ResponseEntity<GenericResponse> updateEvent(String userId, String eventId, ModifyEventRequest modifyEventRequest) throws ParseException;

    ResponseEntity<GenericResponse> deleteEvent(String userId, String eventId);

    ResponseEntity<GenericResponse> getHomeEvents(String userId);
}
