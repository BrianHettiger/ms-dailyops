package com.moblize.ms.dailyops.web.rest.controller;

import com.moblize.ms.dailyops.service.WellsCoordinatesService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DailyopsController {

    @Autowired
    private WellsCoordinatesService wellsCoordinatesService;

    @SneakyThrows
    @Transactional(readOnly = true)
    @GetMapping("/api/v1/getWellCoordinates")
    public Object getWellCoordinates(@RequestParam("customer")String customer){

        return wellsCoordinatesService.getWellCoordinates(customer);
    }
}
