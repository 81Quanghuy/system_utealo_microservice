package vn.iostar.scheduleservice.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleDetailRequest {
    private String courseName;
    private String instructorName;
    private String roomName;
    private Date dayOfWeek;
    private Date startTime;
    private Date endTime;
    private String startPeriod;
    private String endPeriod;
    private String note;
    private String template;
}
