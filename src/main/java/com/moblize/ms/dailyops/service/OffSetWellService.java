package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dto.BCWDTO;
import com.moblize.ms.dailyops.dto.OffSetWellByDistance;
import com.moblize.ms.dailyops.utils.BCWException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OffSetWellService {

    private static final int DISTANCE_INCREMENT_STEP = 5;
    private static final int DISTANCE_INCREMENT_MAX = 15;
    private static final int SET_MAX_BCW_SELECTED = 5;

    RestTemplate restTemplate = new RestTemplate();
    public OffSetWellByDistance getBCWOffSetWellList(BCWDTO bcwdto) {

        OffSetWellByDistance offSetWellByDistance = new OffSetWellByDistance();

        return null;
    }

    private OffSetWellByDistance getOffsetWellListByDistance(BCWDTO bcwDTO) throws BCWException {

        OffSetWellByDistance wellListByDistance = getOffSetWell(bcwDTO);
        if (wellListByDistance != null && wellListByDistance.getOffsetWells() != null && wellListByDistance.getOffsetWells().isEmpty()) {
            if (bcwDTO.getDistance() < DISTANCE_INCREMENT_MAX) {
                log.info("No Offset well found in {}s miles range", bcwDTO.getDistance());
                bcwDTO.setDistance(bcwDTO.getDistance() + DISTANCE_INCREMENT_STEP);
                wellListByDistance = getOffsetWellListByDistance(bcwDTO);
            } else {
                throw new BCWException(String.format("No Offset well found in %s miles range", bcwDTO.getDistance()));
            }
        }
        if (wellListByDistance == null || wellListByDistance.getOffsetWells() == null) {
            throw new BCWException(String.format("No OffSet record found for primary well : %s", bcwDTO.getPrimaryWellUid()));
        }
        bcwDTO.setWellListByDistance(wellListByDistance);
        bcwDTO.setOffsetWellUids(wellListByDistance.getOffsetWells().stream().map(well -> well.getUid()).collect(Collectors.toList()));
        return wellListByDistance;
    }

    private OffSetWellByDistance getOffSetWell(BCWDTO bcwDTO) {
        OffSetWellByDistance offSetWellByDistance = new OffSetWellByDistance();
        String url = "https://t4.moblize.com/proact1/api/v1/" + "getBCWOffSetWellList";
        return offSetWellByDistance;
    }

    private HttpEntity<String> createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        String authStr = username + ":" + password;
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
        headers.add("Authorization", "Basic " + base64Creds);
        return new HttpEntity<>("HEADERS", headers);
    }
}
