package com.moblize.ms.dailyops.web.rest.controller;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.WellPerformanceMetaData;
import com.moblize.ms.dailyops.dto.ResponseDTO;
import com.moblize.ms.dailyops.service.WellPerformanceMetaDataService;
import com.moblize.ms.dailyops.service.WellsCoordinatesService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
public class PerformanceMetaDataController {

    @Autowired
    private WellPerformanceMetaDataService metaDataService;
    @Autowired
    private WellsCoordinatesService wellsCoordinatesService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Transactional
    @PostMapping("/api/v1/metadata")
    public ResponseDTO save(@Valid @RequestBody WellPerformanceMetaData metaData, HttpServletResponse response) {
        if (metaData == null || metaData.getWellUid() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Request parameters did not meet requirements.");
        } else {
            return ResponseDTO.complete(metaDataService.save(metaData));
        }
    }


    @Transactional
    @PutMapping("/api/v1/metadata")
    public ResponseDTO update(@Valid @RequestBody WellPerformanceMetaData metaData, HttpServletResponse response) {
        if (metaData == null || metaData.getWellUid() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Request parameters did not meet requirements.");
        } else {
            return ResponseDTO.complete(metaDataService.update(metaData));
        }
    }


    @Transactional(readOnly = true)
    @GetMapping("/api/v1/metadata/{wellUid}")
    public ResponseDTO find(@PathVariable String wellUid, HttpServletResponse response) {
        if (null == wellUid || wellUid.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Well UID cannot be empty.");
        } else {
            return ResponseDTO.complete(metaDataService.getByWellUid(wellUid));
        }
    }


    @Transactional
    @DeleteMapping("/api/v1/metadata/{wellUid}")
    public ResponseDTO delete(@PathVariable String wellUid, HttpServletResponse response) {
        if (null == wellUid || wellUid.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Well UID cannot be empty.");
        } else {
            metaDataService.deleteWellPerformanceMetaData(wellUid);
            return ResponseDTO.complete("Well Performance metadata has deleted successfully", wellUid);
        }
    }

    @Transactional(readOnly = true)
    @Async
    @PostMapping("notifyPMComplete")
    public void notifyPMComplete(
        @RequestBody Set<String> wells
    ) {
        wellsCoordinatesService.sendWellUpdates(wells);
    }
}
