package com.moblize.ms.dailyops.web.rest.controller;

import com.moblize.ms.dailyops.domain.mongo.WellPerformanceMetaData;
import com.moblize.ms.dailyops.dto.ResponseDTO;
import com.moblize.ms.dailyops.service.WellPerformanceMetaDataService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class PerformanceMetaDataController {

    @Autowired
    private WellPerformanceMetaDataService metaDataService;

    
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
}
