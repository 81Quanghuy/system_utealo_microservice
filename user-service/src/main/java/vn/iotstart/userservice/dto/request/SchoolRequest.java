package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class SchoolRequest {

        private String code;
        private String name;
        private String description;
        private Integer districtId;

}
