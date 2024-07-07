package vn.iostar.scheduleservice.dto.request;

import lombok.Data;

@Data
public class AddScheduleDetailRequest {

    private String year;
    private String semester;
    private String weekOfSemester;
    private String scheduleDetailId;
    private String userId;

}
