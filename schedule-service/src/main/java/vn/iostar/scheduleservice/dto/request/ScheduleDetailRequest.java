package vn.iostar.scheduleservice.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleDetailRequest {
    private String courseName;
    private String instructorName;
    private String roomName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String startPeriod;
    private String endPeriod;
    private String note;
    private String template;
    private String scheduleDetailId;
}
