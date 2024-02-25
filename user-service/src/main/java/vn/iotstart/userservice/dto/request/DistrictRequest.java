package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DistrictRequest {

    @NotNull(message = "Code is required")
    private String code;
    @NotNull(message = "Name is required")
    private String name;
    private String description;
    @NotNull(message = "ProvinceId is required")
    private Integer provinceId;

}
