package vn.iostar.postservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class SessionModel {
    public Object username;
    private String nameRole;
    private Object password;
    List<String> permissionsName;
}
