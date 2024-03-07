package vn.iostar.groupservice.dto.request;

import lombok.Data;

@Data
public class ModifyEventRequest {
    private String eventName;
    private String eventDescription;
    private String startDate;
    private String endDate;
}
