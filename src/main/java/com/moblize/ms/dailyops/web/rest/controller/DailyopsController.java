package com.moblize.ms.dailyops.web.rest.controller;

import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.dto.ResponseDTO;
import com.moblize.ms.dailyops.service.WellsCoordinatesService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
public class DailyopsController {

    @Autowired
    private WellsCoordinatesService wellsCoordinatesService;

    @SneakyThrows
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

    @SneakyThrows
    @Transactional
    @PostMapping("/api/v1/wellSurveyPlannedLatLong/create")
    public ResponseDTO save(@Valid @RequestBody WellSurveyPlannedLatLong wellSurveyPlannedLatLong, HttpServletResponse response) {
        if (wellSurveyPlannedLatLong == null || wellSurveyPlannedLatLong.getUid() == null
            || wellSurveyPlannedLatLong.getPlannedData() == null || wellSurveyPlannedLatLong.getPlannedData().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Request parameters did not meet requirements.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.saveWellSurveyPlannedLatLong(wellSurveyPlannedLatLong));
        }
    }

    @SneakyThrows
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

    @SneakyThrows
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

    @SneakyThrows
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

    @SneakyThrows
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

    @SneakyThrows
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


}
