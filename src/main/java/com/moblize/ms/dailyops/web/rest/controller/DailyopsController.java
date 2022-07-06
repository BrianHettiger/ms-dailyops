package com.moblize.ms.dailyops.web.rest.controller;

import com.moblize.ms.dailyops.domain.PerformanceROP;
import com.moblize.ms.dailyops.domain.ScaledPlannedData;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import com.moblize.ms.dailyops.domain.mongo.PerformanceCost;
import com.moblize.ms.dailyops.domain.mongo.PerformanceWell;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import com.moblize.ms.dailyops.dto.BCWDepthPlotDTO;
import com.moblize.ms.dailyops.dto.BCWDepthPlotResponse;
import com.moblize.ms.dailyops.dto.BHA;
import com.moblize.ms.dailyops.dto.DPVARequestDTO;
import com.moblize.ms.dailyops.dto.NearByWellRequestDTO;
import com.moblize.ms.dailyops.dto.ResponseDTO;
import com.moblize.ms.dailyops.dto.TortuosityRequestDTO;
import com.moblize.ms.dailyops.service.AnalyticsWellMetaDataService;
import com.moblize.ms.dailyops.service.BCWDepthLogPlotService;
import com.moblize.ms.dailyops.service.CacheService;
import com.moblize.ms.dailyops.service.DPVAService;
import com.moblize.ms.dailyops.service.NotifyDPVAService;
import com.moblize.ms.dailyops.service.PerformanceBHAService;
import com.moblize.ms.dailyops.service.PerformanceCostService;
import com.moblize.ms.dailyops.service.PerformanceROPService;
import com.moblize.ms.dailyops.service.PerformanceWellService;
import com.moblize.ms.dailyops.service.TargetWindowDPVAService;
import com.moblize.ms.dailyops.service.TortuosityService;
import com.moblize.ms.dailyops.service.TrueROPDataService;
import com.moblize.ms.dailyops.service.WellsCoordinatesService;
import com.moblize.ms.dailyops.service.dto.DPVAResult;
import com.moblize.ms.dailyops.service.dto.PlannedPerFeetDTO;
import com.moblize.ms.dailyops.service.dto.SurveyPerFeetDTO;
import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import com.moblize.ms.dailyops.service.dto.TargetWindowPerFootDTO;
import com.moblize.ms.dailyops.service.dto.TortuosityDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
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

    @Autowired
    private AnalyticsWellMetaDataService analyticsWellMetaDataService;

    @Autowired
    private TrueROPDataService trueROPDataService;

    @Autowired
    private TargetWindowDPVAService targetWindowDPVAService;

    @Autowired
    private TortuosityService tortuosityService;
    @Autowired
    private DPVAService dpvaService;
    @Autowired
    private NotifyDPVAService notifyDPVAService;
    @Autowired
    @Lazy
    private CacheService cacheService;

    @Autowired
    private BCWDepthLogPlotService bcwDepthLogPlotService;


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getWellCoordinates")
    public ResponseDTO getWellCoordinates(
        @RequestParam("customer") String customer,
        @RequestHeader(value = "authorization", required = false) String token,
        HttpServletResponse response) {
        if (customer == null || customer == "") {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Customer cannot be null.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.getWellCoordinatesV1(customer, token));
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/api/v2/getWellCoordinates")
    public ResponseDTO getWellCoordinatesV2(
        @RequestParam("customer") String customer,
        @RequestHeader(value = "authorization", required = false) String token,
        HttpServletResponse response) {
        if (customer == null || customer == "") {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Customer cannot be null.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.getWellCoordinates(customer, token));
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getWellBHAs")
    public Map<String, List<BHA>> getWellBHAs(
        @RequestParam(required = false) String wellUid,
        HttpServletResponse response
    ) {

            return wellsCoordinatesService.getWellBHAs(wellUid);

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

    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getLastProcessUpToTime/{wellUid}")
    public Long getLastProcessUpTo(@PathVariable String wellUid, HttpServletResponse response) {
        if (wellUid == null || wellUid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return analyticsWellMetaDataService.getLastProcessUpTo(wellUid);
    }

    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getLastProcessROPTime/{wellUid}")
    public Long getLastProcessROPTime(@PathVariable String wellUid, HttpServletResponse response) {
        if (wellUid == null || wellUid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return trueROPDataService.getLastProcessUpTo(wellUid);
    }


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getTargetWindow/{wellUid}/{wellStatus}")
    public TargetWindowDPVA getTargetWindow(@PathVariable String wellUid, @PathVariable String wellStatus, HttpServletResponse response) {
        TargetWindowDPVA targetWindowDPVA =  targetWindowDPVAService.getTargetWindowDetail(wellUid);
        if (targetWindowDPVA == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return targetWindowDPVA;
    }

    @Transactional
    @PostMapping("/api/v1/saveTargetWindow/{wellStatus}")
    public TargetWindowDPVA saveTargetWindow(@RequestBody TargetWindowDPVA targetWindowDPVA, @PathVariable String wellStatus) {
        TargetWindowDPVA  targetWindow = targetWindowDPVAService.saveTargetWindowDetail(targetWindowDPVA, wellStatus);
        notifyDPVAService.notifyDPVAJobForSaveTargetWindow(targetWindow, wellStatus);
        return targetWindow;
    }

    @Transactional(readOnly =true)
    @PostMapping("/api/v1/getTortuosityIndex")
    public List<SurveyRecord> getTortuosityIndex(@RequestBody TortuosityRequestDTO tortuosityRequestDTO){
       return  tortuosityService.getTortuosityIndex(tortuosityRequestDTO);
    }

    @Transactional
    @PostMapping("/api/v1/saveSurveyDataDpva")
    public SurveyPerFeetDTO saveSurveyDataDpva(@RequestBody SurveyPerFeetDTO surveyPerFeetDTO) {
        return dpvaService.saveSurveyDataDpva(surveyPerFeetDTO);
    }

    @Transactional
    @PostMapping("/api/v1/savePlannedDataDpva")
    public PlannedPerFeetDTO savePlannedDataDpva(@RequestBody PlannedPerFeetDTO plannedPerFeetDTO) {
        return dpvaService.savePlannedDataDpva(plannedPerFeetDTO);
    }

    @Transactional
    @PostMapping("/api/v1/savePerFootTargetWindowDpva")
    public void savePerFootTargetWindowDpva(@RequestBody TargetWindowPerFootDTO targetWindowPerFootDTO) {
        try {
            if (targetWindowPerFootDTO.getWellStatus().equalsIgnoreCase("active")) {
                dpvaService.updatePerFootDPVAForActiveWell(targetWindowPerFootDTO);
            } else {
                dpvaService.updatePerFootDPVAForNonActiveWell(targetWindowPerFootDTO);
            }
        } catch (Exception e) {
            log.error("Error occur in savePerFootTargetWindowDpva ", e);
        }
    }

    @Transactional
    @PostMapping("/api/v1/saveTortuosityData")
    public TortuosityDTO saveTortuosityData(@RequestBody TortuosityDTO tortuosityDTO) {
        return dpvaService.saveTortuosityData(tortuosityDTO);
    }

    @Transactional(readOnly = true)
    @PostMapping("/api/v1/getDPVAData")
    public DPVAResult getDPVAData(@RequestBody DPVARequestDTO dpvaRequestDTO) {
        return dpvaService.getDPVAData(dpvaRequestDTO);
    }


    @Transactional
    @PostMapping("/api/v1/resetDPVAWell/{wellUid}")
    public void resetDPVAWell(@PathVariable String wellUid) {
         notifyDPVAService.resetDPVAWell(wellUid);
    }

    @Transactional
    @PostMapping("/api/v1/resetAllDPVAWell")
    public void resetAllDPVAWell() {
        notifyDPVAService.resetAllDPVAWell();
    }

    @Transactional(readOnly = true)
    @GetMapping("/api/v1/resetAllPFWell")
    public void resetAllPFWell() {
        cacheService.resetPerformanceMapData();
    }

    @Transactional(readOnly = true)
    @GetMapping("/api/v1/dpvaWellCompletedNotification/{wellUid}")
    public void dpvaWellCompletedNotification(@PathVariable String wellUid) {
        notifyDPVAService.dpvaWellCompletedNotification(wellUid);
    }


    @Transactional(readOnly = true)
    @PostMapping("/api/v1/getBCWDepthPlotLog")
    public BCWDepthPlotResponse getBCWDepthPlotLog(@RequestBody BCWDepthPlotDTO bcwDepthPlotDTO, HttpServletResponse response) {
        if (bcwDepthPlotDTO == null || bcwDepthPlotDTO.getPrimaryWellUid() == null
            || bcwDepthPlotDTO.getOffsetWellUids() == null || bcwDepthPlotDTO.getOffsetWellUids().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        } else {
            return bcwDepthLogPlotService.getBCWDepthLog(bcwDepthPlotDTO);
        }
        return null;
    }

    @DeleteMapping("/api/v1/deleteBCWDepthPlotLog")
    public Object deleteBCWDepthLogPlot(@RequestBody Map<String, String>  requestMap){
        return bcwDepthLogPlotService.deleteBCWDepthLog(requestMap.get("bcwId"), requestMap.get("uid"));
    }

    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getScaledPlannedDataList/{uid}/{customer}")
    public List<ScaledPlannedData> getScaledPlannedDataList(@PathVariable String uid, @PathVariable String customer){
        return dpvaService.getScaledPlannedDataList(uid,customer);
    }


}
