package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.AlarmDetailClient;
import com.moblize.ms.dailyops.client.KpiDashboardClient;
import com.moblize.ms.dailyops.client.WitsmlLogsClient;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.DepthLogResponse;
import com.moblize.ms.dailyops.domain.mongo.MongoLog;
import com.moblize.ms.dailyops.dto.*;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.moblize.ms.dailyops.service.dto.HoleSection;
import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import com.moblize.ms.dailyops.utils.DrillerDashboardBuildAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.util.*;

@Slf4j
@Service
public class DrllingRoadMapMobileService {

    @Value("${api.central.nextgen.user}")
    private String username;
    @Value("${api.central.nextgen.pwd}")
    private String password;
    private static final String DEFAULT_WELLBORE_ID = "Wellbore1";
    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Autowired
    private BCWDepthLogPlotService bcwDepthLogPlotService;
    @Autowired
    private WitsmlLogsClient witsmlLogsClient;
    @Autowired
    private KpiDashboardClient kpiDashboardClient;
    @Autowired
    private AlarmDetailClient alarmDetailClient;

    static RestTemplate restTemplate = new RestTemplate();

    private String getUserNameInSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = (String) auth.getPrincipal();
        log.info("getUserNameInSession: userName:{}", userName);
        userName = URLDecoder.decode(userName);
        return userName;
    }

    public DrillingRoadmapJsonResponse readMobile(DrillingRoadMapSearchDTO drillingRoadMapSearchDTO) {
        DrillingRoadmapJsonResponse drillingRoadmapJsonResponses = new DrillingRoadmapJsonResponse();
        try {
            String userName = getUserNameInSession();
            drillingRoadmapJsonResponses = getDrillingRoadmapResponses(drillingRoadMapSearchDTO, userName);
        } catch (Exception e) {
            log.error("Error occurred while serving Drilling Drag Average data API", e);
            throw new RuntimeException("Error occurred while serving Drilling Drag Average data API", e);
        }
        return drillingRoadmapJsonResponses;
    }

    public DrillingRoadmapJsonResponse getDrillingRoadmapResponses(DrillingRoadMapSearchDTO requestDTO, String userName) {

        String currentMeasuredDepth = "";
        String currenttvd = "";
        String currentInclination = "";
        String currentAzimuth = "";
        String currentWellMudWeight = "";
        String paceSetterFormationWellUid = "";
        String paceSetterFormationWellName = "";
        String paceSetterWellboreUid = "";
        String currentSection = "";
        String currentRigState = "";
        String currentWellFormation = "";
        DrillingRoadmapJsonResponse response = new DrillingRoadmapJsonResponse();

        try {

            currentMeasuredDepth = getMeasuredDepth(requestDTO);

            SurveyRecord surveyRecord = alarmDetailClient.getLastSurveyData(requestDTO.getPrimaryWellUid(),
                mongoWellRepository.findByUid(requestDTO.getPrimaryWellUid()).getStatusWell());
            if (surveyRecord != null) {
                currenttvd = surveyRecord.getTvd() != null ? surveyRecord.getTvd().toString() : "";
                currentInclination = surveyRecord.getIncl() != null ? surveyRecord.getIncl().toString() : "";
                currentAzimuth = surveyRecord.getAzimuth() != null ? surveyRecord.getAzimuth().toString() : "";
            }


            currentRigState = getRigState(requestDTO, currentMeasuredDepth);

            currentWellMudWeight = getWellMudWeight(requestDTO, userName);

            BCWDepthPlotDTO bcwDepthPlotDTO = new BCWDepthPlotDTO(null, "bcwData", requestDTO.getPrimaryWellUid(), requestDTO.getOffsetWellUids(), 0, 0);
            List<DrillingRoadMapWells> drillingRoadMap = bcwDepthLogPlotService.getDrillingRoadmap(bcwDepthPlotDTO);

            response.setBcwData(drillingRoadMap);

            DrillingRoadMapWells currrentWellBcwFormationMap = response.getPrimaryWellDrillingRoadMap() == null ? null : getDrillingRoadMapWells(response);

            if (currrentWellBcwFormationMap != null) {
                currentWellFormation = currrentWellBcwFormationMap.getFormationName();
                Optional<DrillingRoadMapWells> paceSetterFormation = response.getFormationBcwData().stream().filter(formation ->
                    formation.getFormationName().equalsIgnoreCase(currrentWellBcwFormationMap.getFormationName())
                ).findFirst();

                if (paceSetterFormation.isPresent()) {
                    paceSetterFormationWellUid = paceSetterFormation.get().getWellUid();
                    MongoWell mongoWell = mongoWellRepository.findFirstByUid(paceSetterFormationWellUid);
                    paceSetterFormationWellName = mongoWell.getName();
                    paceSetterWellboreUid = DEFAULT_WELLBORE_ID;
                    response.setPaceSetterFormationMap(paceSetterFormation.get());
                }
            }

            currentSection = getCurrentSection(requestDTO, currentMeasuredDepth);

            response.setCurrentMeasuredDepth(currentMeasuredDepth);
            response.setCurrenttvd(currenttvd);
            response.setCurrentInclination(currentInclination);
            response.setCurrentAzimuth(currentAzimuth);
            response.setCurrentWellDepth(currentMeasuredDepth);
            response.setCurrentWellEndIndex(currentMeasuredDepth);
            response.setPaceSetterWellUid(paceSetterFormationWellUid);
            response.setPaceSetterWellName(paceSetterFormationWellName);
            response.setPaceSetterWellboreUid(paceSetterWellboreUid);
            response.setCurrentWellFormation(currentWellFormation);
            response.setCurrentSection(currentSection);
            response.setCurrentWellMudWeight(currentWellMudWeight);
            response.setCurrentRigState(currentRigState);
            //response.setDaysVsAEF(getDaysVsAFE(requestDTO));
            response.setCurrrentWellBcwFormationMap(currrentWellBcwFormationMap);


        } catch (Exception exception) {
            log.error("Error while processing the DrillingRoadMapPayLoad [process] for the payload :" + requestDTO.toString(), exception);
        }
        return response;
    }

    private DrillingRoadMapWells getDrillingRoadMapWells(DrillingRoadmapJsonResponse drillingRoadmapJsonResponses) {
        List<DrillingRoadMapWells> primaryWellDrillingRoadMap = new ArrayList<>(drillingRoadmapJsonResponses.getPrimaryWellDrillingRoadMap());
        primaryWellDrillingRoadMap.sort(new Comparator<DrillingRoadMapWells>() {
            @Override
            public int compare(DrillingRoadMapWells drillingRoadMapWells1, DrillingRoadMapWells drillingRoadMapWells2) {
                int md1 = Integer.parseInt(drillingRoadMapWells1.getMD());
                int md2 = Integer.parseInt(drillingRoadMapWells2.getMD());
                if (md1 == md2) {
                    return 0;
                }
                return md1 < md2 ? -1 : 1;
            }
        });
        return primaryWellDrillingRoadMap != null && !primaryWellDrillingRoadMap.isEmpty() ? primaryWellDrillingRoadMap.get(primaryWellDrillingRoadMap.size() - 1) : null;
    }

    private String getMeasuredDepth(DrillingRoadMapSearchDTO drillingRoadMapSearchDTO) {
        MongoLog logs = witsmlLogsClient.getDepthLog(drillingRoadMapSearchDTO.getPrimaryWellUid());
        return logs.getUidWellbore();
    }

    private String getCurrentSection(DrillingRoadMapSearchDTO drillingRoadMapSearchDTO, String currentMeasuredDepth) {
        String currentSection;
        List<HoleSection> sections = kpiDashboardClient.getHoleSections(drillingRoadMapSearchDTO.getPrimaryWellUid());
        final Float finalCurrentMeasuredDepth = Float.parseFloat(currentMeasuredDepth);
        Optional<HoleSection> holesection = sections.stream().filter(section -> finalCurrentMeasuredDepth > section.getFromDepth() && finalCurrentMeasuredDepth <= section.getToDepth()).findFirst();
        currentSection = holesection.isPresent() ? holesection.get().getSection().name() : "";
        return currentSection;
    }

    public String getRigState(DrillingRoadMapSearchDTO drillingRoadMapSearchDTO, String currentMeasuredDepth) {
        List<DepthLogResponse> data = null;
        if (drillingRoadMapSearchDTO != null) {
            String url = "http://172.31.2.228:5006/api/v1/" + "log?wellUid=" + drillingRoadMapSearchDTO.getPrimaryWellUid() + "&type=depth&startIndex="
                + (Double.parseDouble(currentMeasuredDepth) - 50) + "&endIndex=" + currentMeasuredDepth + "&needToConvertRange=true";
            data = restTemplate.exchange(url, HttpMethod.GET, createHeaders(), new ParameterizedTypeReference<LogResponse>() {
            }).getBody().getData();
        }
        if (data != null) {
            return data.get(data.size() - 1).getRigState();
        } else {
            return null;
        }
    }


    public String getWellMudWeight(DrillingRoadMapSearchDTO drillingRoadMapSearchDTO, String userName) {
        HashMap<String, Object> data = null;
        if (drillingRoadMapSearchDTO != null) {
            String url = "http://172.31.2.228:9001/api/v1/" + "mudAnalysis?wellUid=" + drillingRoadMapSearchDTO.getPrimaryWellUid() + "&userName=" + userName;
            data = restTemplate.exchange(url, HttpMethod.GET, createHeaders(), new ParameterizedTypeReference<HashMap<String, Object>>() {
            }).getBody();
        }
        if (data != null) {
            return null;
        } else {
            return null;
        }
    }

    private HttpEntity<String> createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String authStr = username + ":" + password;
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
        headers.add("Authorization", "Basic " + base64Creds);
        return new HttpEntity<>("HEADERS", headers);
    }
}
