package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.request.AddressRequest;
import com.trvankiet.app.dto.request.DistrictRequest;
import com.trvankiet.app.dto.request.SchoolRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/addresses/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminAddressController {

    private final AddressService addressService;

    @GetMapping("/provinces")
    public ResponseEntity<GenericResponse> getAllProvinces(@RequestHeader("Authorization") String token,
                                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("AdminAddressController, getAllProvinces");
        return addressService.getAllProvincesForAdmin(token, page, size);
    }

    @PostMapping("/add-province")
    public ResponseEntity<GenericResponse> addProvince(@RequestHeader("Authorization") String token,
                                                       @RequestBody AddressRequest addressRequest) {
        log.info("AdminAddressController, addProvince");
        return addressService.addProvince(token, addressRequest);
    }

    @PutMapping("/update-province/{id}")
    public ResponseEntity<GenericResponse> updateProvince(@RequestHeader("Authorization") String token,
                                                          @PathVariable("id") Integer id,
                                                          @RequestBody AddressRequest addressRequest) {
        log.info("AdminAddressController, updateProvince");
        return addressService.updateProvince(token, id, addressRequest);
    }

    @DeleteMapping("/delete-province/{id}")
    public ResponseEntity<GenericResponse> deleteProvince(@RequestHeader("Authorization") String token,
                                                          @PathVariable("id") Integer id) {
        log.info("AdminAddressController, deleteProvince");
        return addressService.deleteProvince(token, id);
    }

    @GetMapping("/districtsByProvince")
    public ResponseEntity<GenericResponse> districtsByProvince(@RequestHeader("Authorization") String token,
                                                               @RequestParam("pId") Integer provinceId) {
        log.info("AdminAddressController, districtsByProvince");
        return addressService.getDistrictsForAdmin(token, provinceId);
    }

    @PostMapping("/add-district")
    public ResponseEntity<GenericResponse> addDistrict(@RequestHeader("Authorization") String token,
                                                       @RequestBody DistrictRequest addressRequest) {
        log.info("AdminAddressController, addDistrict");
        return addressService.addDistrict(token, addressRequest);
    }

    @PutMapping("/update-district/{id}")
    public ResponseEntity<GenericResponse> updateDistrict(@RequestHeader("Authorization") String token,
                                                          @PathVariable("id") Integer id,
                                                          @RequestBody DistrictRequest addressRequest) {
        log.info("AdminAddressController, updateDistrict");
        return addressService.updateDistrict(token, id, addressRequest);
    }

    @DeleteMapping("/delete-district/{id}")
    public ResponseEntity<GenericResponse> deleteDistrict(@RequestHeader("Authorization") String token,
                                                          @PathVariable("id") Integer id) {
        log.info("AdminAddressController, deleteDistrict");
        return addressService.deleteDistrict(token, id);
    }

    @GetMapping("/schoolsByDistrict")
    public ResponseEntity<GenericResponse> schoolsByDistrict(@RequestHeader("Authorization") String token,
                                                             @RequestParam("dId") Integer districtId) {
        log.info("AdminAddressController, schoolsByDistrict");
        return addressService.getSchoolsForAdmin(token, districtId);
    }

    @PostMapping("/add-school")
    public ResponseEntity<GenericResponse> addSchool(@RequestHeader("Authorization") String token,
                                                     @RequestBody SchoolRequest addressRequest) {
        log.info("AdminAddressController, addSchool");
        return addressService.addSchool(token, addressRequest);
    }

    @PutMapping("/update-school/{id}")
    public ResponseEntity<GenericResponse> updateSchool(@RequestHeader("Authorization") String token,
                                                        @PathVariable("id") Integer id,
                                                        @RequestBody SchoolRequest addressRequest) {
        log.info("AdminAddressController, updateSchool");
        return addressService.updateSchool(token, id, addressRequest);
    }

    @DeleteMapping("/delete-school/{id}")
    public ResponseEntity<GenericResponse> deleteSchool(@RequestHeader("Authorization") String token,
                                                        @PathVariable("id") Integer id) {
        log.info("AdminAddressController, deleteSchool");
        return addressService.deleteSchool(token, id);
    }
}
