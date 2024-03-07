package vn.iostar.groupservice.dto.request;

import lombok.Data;

@Data
public class CreateEventRequest {

    private String groupId;
    private String eventName;
    private String eventDescription;
    private String startDate;
    private String endDate;

}
