package com.trvankiet.app.controller.user;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressControler {

    private final AddressService addressService;

    @GetMapping("/provinces")
    public ResponseEntity<GenericResponse> getAllProvinces() {
        log.info("AddressControler, getAllProvinces");
        return addressService.getAllProvinces();
    }

    @GetMapping("/districtsByProvince")
    public ResponseEntity<GenericResponse> districtsByProvince(@RequestParam("pId") Integer provinceId) {
        log.info("AddressControler, districtsByProvince");
        return addressService.getDistricts(provinceId);
    }

    @GetMapping("/schoolsByDistrict")
    public ResponseEntity<GenericResponse> schoolsByDistrict(@RequestParam("dId") Integer districtId) {
        log.info("AddressControler, schoolsByDistrict");
        return addressService.getSchools(districtId);
    }

}
