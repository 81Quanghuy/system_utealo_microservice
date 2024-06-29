package vn.iostar.scheduleservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "schedule_detail")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ScheduleDetail implements Serializable {

    @Id
    @Field(name = "schedule_detail_id")
    private String id;

    @Field(name = "course_name")
    private String courseName;

    @Field(name = "instructor_name")
    private String instructorName;

    @Field(name = "room_name")
    private String roomName;

    @Field(name = "day_of_week")
    private String dayOfWeek;

    @Field(name = "start_time")
    private String startTime;

    @Field(name = "end_time")
    private String endTime;

    @Field(name = "start_period")
    private String startPeriod;

    @Field(name = "end_period")
    private String endPeriod;

    @Field(name = "note")
    private String note;

    @Field(name = "basis")
    private String basis;

    @Field(name = "number")
    private String number;
}
