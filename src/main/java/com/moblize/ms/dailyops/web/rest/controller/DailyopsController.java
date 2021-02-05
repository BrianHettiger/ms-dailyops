package com.moblize.ms.dailyops.web.rest.controller;

import com.moblize.ms.dailyops.domain.PerformanceROP;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.dto.NearByWellRequestDTO;
import com.moblize.ms.dailyops.dto.ResponseDTO;
import com.moblize.ms.dailyops.service.PerformanceROPService;
import com.moblize.ms.dailyops.service.WellsCoordinatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
public class DailyopsController {

    @Autowired
    private WellsCoordinatesService wellsCoordinatesService;

    @Autowired
    private PerformanceROPService performanceROPService;


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getWellCoordinates")
    public ResponseDTO getWellCoordinates(@RequestParam("customer") String customer, HttpServletResponse response) {
        if (customer == null || customer == "") {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Customer cannot be null.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.getWellCoordinates(customer));
        }
    }


    @Transactional
    @PostMapping("/api/v1/wellSurveyPlannedLatLong/create")
    public ResponseDTO save(@Valid @RequestBody WellSurveyPlannedLatLong wellSurveyPlannedLatLong, HttpServletResponse response) {
        if (wellSurveyPlannedLatLong == null || wellSurveyPlannedLatLong.getUid() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Request parameters did not meet requirements.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.saveWellSurveyPlannedLatLong(wellSurveyPlannedLatLong));
        }
    }


    @Transactional
    @PostMapping("/api/v1/wellSurveyPlannedLatLong/createAll")
    public ResponseDTO saveAll(@Valid @RequestBody List<WellSurveyPlannedLatLong> wellSurveyPlannedLatLongAll, HttpServletResponse response) {
        if (wellSurveyPlannedLatLongAll == null || wellSurveyPlannedLatLongAll.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Request parameters did not meet requirements.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.saveWellSurveyPlannedLatLong(wellSurveyPlannedLatLongAll));
        }
    }


    @Transactional
    @PutMapping("/api/v1/wellSurveyPlannedLatLong/update")
    public ResponseDTO update(@Valid @RequestBody WellSurveyPlannedLatLong wellSurveyPlannedLatLong, HttpServletResponse response) {
        if (wellSurveyPlannedLatLong == null || wellSurveyPlannedLatLong.getUid() == null
            || wellSurveyPlannedLatLong.getDrilledData() == null || wellSurveyPlannedLatLong.getDrilledData().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Request parameters did not meet requirements.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.updateWellSurveyPlannedLatLong(wellSurveyPlannedLatLong));
        }
    }


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/wellSurveyPlannedLatLong/read")
    public ResponseDTO find(@RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid == "") {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.findWellSurveyPlannedLatLong(uid));
        }
    }


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/wellSurveyPlannedLatLong/readAll")
    public ResponseDTO findAll(@RequestParam List<String> uid, HttpServletResponse response) {
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID list cannot be empty.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.findWellSurveyPlannedLatLong(uid));
        }
    }


    @Transactional
    @DeleteMapping("/api/v1/wellSurveyPlannedLatLong/remove")
    public ResponseDTO delete(@RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid == "") {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            wellsCoordinatesService.deleteWellSurveyPlannedLatLong(uid);
            return ResponseDTO.complete("Well SurveyPlanned coordinate data has deleted successfully", uid);
        }
    }


    @Transactional
    @PutMapping("/api/v1/getNearByWell")
    public ResponseDTO getNearByWells(@Valid @RequestBody NearByWellRequestDTO nearByWell, HttpServletResponse response) {

        return ResponseDTO.complete(wellsCoordinatesService.getNearByWell(nearByWell.getPrimaryWell(), nearByWell.getDistance(), nearByWell.getCustomer(), nearByWell.getLimit()));
    }


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/performanceROP/read")
    public ResponseDTO findPerformanceROP(@RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            return ResponseDTO.complete(performanceROPService.findPerformanceROP(uid));
        }
    }


    @Transactional
    @PostMapping("/api/v1/performanceROP/create")
    public ResponseDTO savePerformanceROP(@Valid @RequestBody PerformanceROP performanceROP) {
        return ResponseDTO.complete(performanceROPService.savePerformanceROP(performanceROP));
    }


    @Transactional
    @DeleteMapping("/api/v1/performanceROP/remove")
    public ResponseDTO deletePerformanceROP(@Valid @RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            performanceROPService.deletePerformanceROP(uid);
            return ResponseDTO.complete("Well PerformanceROP data has deleted successfully", uid);
        }
    }


    @Transactional
    @PutMapping("/api/v1/performanceROP/update")
    public ResponseDTO updatePerformanceROP(@Valid @RequestBody PerformanceROP performanceROP) {
        return ResponseDTO.complete(performanceROPService.updatePerformanceROP(performanceROP));
    }


}
