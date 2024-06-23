package vn.iostar.scheduleservice.dto.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ScheduleRequest {

    private List<String> userId;
    private String semester;
    private String year;
    private String weekOfSemester;
    private List<ScheduleDetailRequest> scheduleDetails;

}
