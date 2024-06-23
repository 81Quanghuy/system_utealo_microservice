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
import java.util.List;

@Document(collection = "schedule")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Schedule implements Serializable {

    @Id
    @Field(name = "schedule_id")
    private String id;

    @Field(name = "semester")
    private String semester;

    @Field(name = "year")
    private String year;

    @Field(name = "weekOfSemester")
    private String weekOfSemester;

    @Field(name = "user_id")
    private List<String> userId;

    @Field(name = "creater_id")
    private String createrId;

    @Field(name = "schedule_details")
    private List<ScheduleDetail> scheduleDetails;

}
