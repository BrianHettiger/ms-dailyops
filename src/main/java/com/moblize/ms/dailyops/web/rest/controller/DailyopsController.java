package com.moblize.ms.dailyops.web.rest.controller;

import com.moblize.ms.dailyops.domain.PerformanceROP;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import com.moblize.ms.dailyops.domain.mongo.PerformanceCost;
import com.moblize.ms.dailyops.domain.mongo.PerformanceWell;
import com.moblize.ms.dailyops.dto.BHA;
import com.moblize.ms.dailyops.dto.NearByWellRequestDTO;
import com.moblize.ms.dailyops.dto.ResponseDTO;
import com.moblize.ms.dailyops.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class DailyopsController {

    @Autowired
    private WellsCoordinatesService wellsCoordinatesService;

    @Autowired
    private PerformanceROPService performanceROPService;
    @Autowired
    private PerformanceCostService performanceCostService;
    @Autowired
    private PerformanceBHAService performanceBHAService;
    @Autowired
    private PerformanceWellService performanceWellService;


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getWellCoordinates")
    public ResponseDTO getWellCoordinates(@RequestParam("customer") String customer, HttpServletResponse response) {
        if (customer == null || customer == "") {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Customer cannot be null.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.getWellCoordinatesV1(customer));
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/api/v2/getWellCoordinates")
    public ResponseDTO getWellCoordinatesV2(@RequestParam("customer") String customer, HttpServletResponse response) {
        if (customer == null || customer == "") {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Customer cannot be null.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.getWellCoordinates(customer));
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getWellBHAs")
    public Map<String, List<BHA>> getWellBHAs(HttpServletResponse response) {

            return wellsCoordinatesService.getWellBHAs();

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
        if (wellSurveyPlannedLatLong == null || wellSurveyPlannedLatLong.getUid() == null) {
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

    @Transactional(readOnly = true)
    @GetMapping("/api/v1/performanceCost/read")
    public ResponseDTO findPerformanceCost(@RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            return ResponseDTO.complete(performanceCostService.findPerformanceCost(uid));
        }
    }


    @Transactional
    @PostMapping("/api/v1/performanceCost/create")
    public ResponseDTO savePerformanceCost(@Valid @RequestBody PerformanceCost performanceCost) {
        return ResponseDTO.complete(performanceCostService.savePerformanceCost(performanceCost));
    }


    @Transactional
    @DeleteMapping("/api/v1/performanceCost/remove")
    public ResponseDTO deletePerformanceCost(@Valid @RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            performanceCostService.deletePerformanceCost(uid);
            return ResponseDTO.complete("Well PerformanceCost data has deleted successfully", uid);
        }
    }


    @Transactional
    @PutMapping("/api/v1/performanceCost/update")
    public ResponseDTO updatePerformanceCost(@Valid @RequestBody PerformanceCost performanceCost) {
        return ResponseDTO.complete(performanceCostService.updatePerformanceCost(performanceCost));
    }


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/performanceBHA/read")
    public ResponseDTO findPerformanceBHA(@RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            return ResponseDTO.complete(performanceBHAService.findPerformanceBHA(uid));
        }
    }


    @Transactional
    @PostMapping("/api/v1/performanceBHA/create")
    public ResponseDTO savePerformanceBHA(@Valid @RequestBody PerformanceBHA performanceBHA) {
        return ResponseDTO.complete(performanceBHAService.savePerformanceBHA(performanceBHA));
    }


    @Transactional
    @DeleteMapping("/api/v1/performanceBHA/remove")
    public ResponseDTO deletePerformanceBHA(@Valid @RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            performanceBHAService.deletePerformanceBHA(uid);
            return ResponseDTO.complete("Well PerformanceBHA data has deleted successfully", uid);
        }
    }


    @Transactional
    @PutMapping("/api/v1/performanceBHA/update")
    public ResponseDTO updatePerformanceBHA(@Valid @RequestBody PerformanceBHA performanceWell) {
        return ResponseDTO.complete(performanceBHAService.updatePerformanceBHA(performanceWell));
    }


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/performanceWell/read")
    public ResponseDTO findPerformanceWell(@RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            return ResponseDTO.complete(performanceWellService.findPerformanceWell(uid));
        }
    }


    @Transactional
    @PostMapping("/api/v1/performanceWell/create")
    public ResponseDTO savePerformanceWell(@Valid @RequestBody PerformanceWell performanceWell) {
        return ResponseDTO.complete(performanceWellService.savePerformanceWell(performanceWell));
    }


    @Transactional
    @DeleteMapping("/api/v1/performanceWell/remove")
    public ResponseDTO deletePerformanceWell(@Valid @RequestParam String uid, HttpServletResponse response) {
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("UID cannot be empty.");
        } else {
            performanceWellService.deletePerformanceWell(uid);
            return ResponseDTO.complete("Well PerformanceWell data has deleted successfully", uid);
        }
    }


    @Transactional
    @PutMapping("/api/v1/performanceWell/update")
    public ResponseDTO updatePerformanceWell(@Valid @RequestBody PerformanceWell performanceWell) {
        return ResponseDTO.complete(performanceWellService.updatePerformanceWell(performanceWell));
    }
}
