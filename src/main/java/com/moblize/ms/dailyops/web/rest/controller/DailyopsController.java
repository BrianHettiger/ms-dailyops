package com.moblize.ms.dailyops.web.rest.controller;

import com.moblize.ms.dailyops.dto.ResponseDTO;
import com.moblize.ms.dailyops.service.WellsCoordinatesService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class DailyopsController {

    @Autowired
    private WellsCoordinatesService wellsCoordinatesService;

    @SneakyThrows
    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getWellCoordinates")
    public ResponseDTO getWellCoordinates(@RequestParam("customer")String customer, HttpServletResponse response){
        if (customer == null || customer =="") {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseDTO.invalid("Customer cannot be null.");
        } else {
            return ResponseDTO.complete(wellsCoordinatesService.getWellCoordinates(customer));
        }
    }
}
