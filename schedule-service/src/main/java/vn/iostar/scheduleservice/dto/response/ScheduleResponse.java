package vn.iostar.scheduleservice.dto.response;

import lombok.Data;
import vn.iostar.scheduleservice.entity.Schedule;
import vn.iostar.scheduleservice.entity.ScheduleDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ScheduleResponse {
    private String scheduleId;
    private String semester;
    private String year;
    private String weekOfSemester;
    private List<String> userId;
    private List<ScheduleDetail> scheduleDetails;

    public ScheduleResponse(Schedule schedule) {
        this.scheduleId = schedule.getId();
        this.semester = schedule.getSemester();
        this.year = schedule.getYear();
        this.weekOfSemester = schedule.getWeekOfSemester();
        if(schedule.getUserId() != null) {
            this.userId = schedule.getUserId();
        } else {
            this.userId = new ArrayList<>();
        }
        if(schedule.getScheduleDetails() != null) {
            this.scheduleDetails = schedule.getScheduleDetails();
        } else {
            this.scheduleDetails = new ArrayList<>();
        }
    }
}
