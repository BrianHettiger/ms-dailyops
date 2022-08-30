package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.WellWellboreDao;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.DepthLogResponse;
import com.moblize.ms.dailyops.domain.mongo.MongoLog;
import com.moblize.ms.dailyops.dto.*;
import com.moblize.ms.dailyops.repository.HoleSectionRepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.moblize.ms.dailyops.service.dto.HoleSection;
import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import com.moblize.ms.dailyops.utils.DrillerDashboardBuildAnalysis;
import com.moblize.ms.dailyops.utils.uom.UOM;
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
    private AvgPerStandUtil avgPerStandUtil;
    @Autowired
    private WellWellboreDao wellWellboreDao;
    @Autowired
    private WitsmlLogService service;
    @Autowired
    private HoleSectionRepository holeSectionRepository;
    @Autowired
    private DayVsDepthDAO dayVsDepthDAO;
    @Autowired
    private Logs logs;
    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Autowired
    private BCWDepthLogPlotService bcwDepthLogPlotService;
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
        try{
            String userName = getUserNameInSession();
            drillingRoadmapJsonResponses = getDrillingRoadmapResponses(drillingRoadMapSearchDTO, userName);
        } catch (Exception e) {
            log.error("Error occurred while serving Drilling Drag Average data API", e);
            throw new RuntimeException("Error occurred while serving Drilling Drag Average data API", e);
        }
        return drillingRoadmapJsonResponses;
    }

    public DrillingRoadmapJsonResponse getDrillingRoadmapResponses(DrillingRoadMapSearchDTO drillingRoadMapSearchDTO, String userName) {

        String currentMeasuredDepth = "";
        String currenttvd = "";
        String currentInclination = "";
        String currentAzimuth = "";
        String currentWellMudWeight = "";
        String paceSetterFormationWellUid = "";
        String paceSetterFormationWellName ="";
        String paceSetterWellboreUid="";
        String currentSection = "";
        String currentRigState = "";
        String currentWellFormation = "";
        DrillingRoadmapJsonResponse drillingRoadmapJsonResponses = new DrillingRoadmapJsonResponse();

        try {

            currentMeasuredDepth = getMeasuredDepth(drillingRoadMapSearchDTO, currentMeasuredDepth);

            // need to optimize AvgPerStandCalculation
            List<SurveyRecord> surveyList = avgPerStandUtil.processAvgPerStandBasedOnSurvey(drillingRoadMapSearchDTO.getPrimaryWellUid(), DrillerDashboardBuildAnalysis.CHANNELS);
            surveyList = UOM.convertFromMoblizeUnits(surveyList, UOM.SURVEY_RECORD_ATTRIBUTES);
            if (!surveyList.isEmpty()) {
                currenttvd = surveyList.get(surveyList.size() - 1).getTvd().toString();
                currentInclination = surveyList.get(surveyList.size() - 1).getIncl().toString();
                currentAzimuth = surveyList.get(surveyList.size() - 1).getAzimuth().toString();
            }


            currentRigState = getRigState(drillingRoadMapSearchDTO, currentMeasuredDepth);

            currentWellMudWeight = getWellMudWeight(drillingRoadMapSearchDTO, userName);

            BCWDepthPlotDTO bcwDepthPlotDTO = new BCWDepthPlotDTO(null,"bcwData", drillingRoadMapSearchDTO.getPrimaryWellUid(), drillingRoadMapSearchDTO.getOffsetWellUids(),0,0);
            List<DrillingRoadMapWells> drillingRoadmap = bcwDepthLogPlotService.getDrillingRoadmap(bcwDepthPlotDTO);

            drillingRoadmapJsonResponses = process(drillingRoadMapSearchDTO);

            DrillingRoadmapJsonResponse.DrillingRoadMapWells currrentWellBcwFormationMap = drillingRoadmapJsonResponses.getPrimaryWellDrillingRoadMap() == null ? null : getDrillingRoadMapWells(drillingRoadmapJsonResponses);

            if (currrentWellBcwFormationMap != null) {
                currentWellFormation = currrentWellBcwFormationMap.getFormationName();
                Optional<DrillingRoadmapJsonResponse.DrillingRoadMapWells> paceSetterFormation = drillingRoadmapJsonResponses.getFormationBcwData().stream().filter(formation ->
                    formation.getFormationName().equalsIgnoreCase(currrentWellBcwFormationMap.getFormationName())
                ).findFirst();

                if(paceSetterFormation.isPresent()) {
                    paceSetterFormationWellUid = paceSetterFormation.get().getWellUid();
                    MongoWell mongoWell = mongoWellRepository.findFirstByUid(paceSetterFormationWellUid);
                    paceSetterFormationWellName = mongoWell.getName();
                    paceSetterWellboreUid=DEFAULT_WELLBORE_ID;
                    drillingRoadmapJsonResponses.setPaceSetterFormationMap( paceSetterFormation.get());
                }
            }

            currentSection = getCurrentSection(drillingRoadMapSearchDTO, currentMeasuredDepth);

            drillingRoadmapJsonResponses.setCurrentMeasuredDepth(currentMeasuredDepth);
            drillingRoadmapJsonResponses.setCurrenttvd(currenttvd);
            drillingRoadmapJsonResponses.setCurrentInclination(currentInclination);
            drillingRoadmapJsonResponses.setCurrentAzimuth(currentAzimuth);
            drillingRoadmapJsonResponses.setCurrentWellDepth(currentMeasuredDepth);
            drillingRoadmapJsonResponses.setCurrentWellEndIndex(currentMeasuredDepth);
            drillingRoadmapJsonResponses.setPaceSetterWellUid(paceSetterFormationWellUid);
            drillingRoadmapJsonResponses.setPaceSetterWellName(paceSetterFormationWellName);
            drillingRoadmapJsonResponses.setPaceSetterWellboreUid(paceSetterWellboreUid);
            drillingRoadmapJsonResponses.setCurrentWellFormation(currentWellFormation);
            drillingRoadmapJsonResponses.setCurrentSection(currentSection);
            drillingRoadmapJsonResponses.setCurrentWellMudWeight(currentWellMudWeight);
            drillingRoadmapJsonResponses.setCurrentRigState(currentRigState);
            drillingRoadmapJsonResponses.setDaysVsAEF(getDaysVsAFE(drillingRoadMapSearchDTO));
            drillingRoadmapJsonResponses.setCurrrentWellBcwFormationMap(currrentWellBcwFormationMap);


        } catch (Exception exception) {
            log.error("Error while processing the DrillingRoadMapPayLoad [process] for the payload :" + drillingRoadMapSearchDTO.toString(), exception);
        }
        return drillingRoadmapJsonResponses;
    }

    private DrillingRoadmapJsonResponse.DrillingRoadMapWells getDrillingRoadMapWells(DrillingRoadmapJsonResponse drillingRoadmapJsonResponses) {
        List<DrillingRoadmapJsonResponse.DrillingRoadMapWells> primaryWellDrillingRoadMap = new ArrayList<>(drillingRoadmapJsonResponses.getPrimaryWellDrillingRoadMap());
        primaryWellDrillingRoadMap.sort(new Comparator<DrillingRoadmapJsonResponse.DrillingRoadMapWells>() {
            @Override
            public int compare(DrillingRoadmapJsonResponse.DrillingRoadMapWells drillingRoadMapWells1, DrillingRoadmapJsonResponse.DrillingRoadMapWells drillingRoadMapWells2) {
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

    private String getMeasuredDepth(DrillingRoadMapSearchDTO drillingRoadMapSearchDTO, String currentMeasuredDepth) {
        List<MongoLog> logs = service.getMongoWitsmlLogs(drillingRoadMapSearchDTO.getPrimaryWellUid());

        for (MongoLog log : logs) {
            if (log != null && "MEASURED_DEPTH".equals(log.getIndexType())) {
                UOM.Range depthRange = UOM.convertFromMoblizeUnits(new UOM.Range(log), UOM.DEPTH_LOG_ATTRIBUTES);
                log.setStartIndex(depthRange.getStartIndex().floatValue());
                log.setEndIndex(depthRange.getEndIndex().floatValue());
                currentMeasuredDepth = depthRange.getEndIndex().toString();
            }
        }
        return currentMeasuredDepth;
    }

    private String getCurrentSection(DrillingRoadMapSearchDTO drillingRoadMapSearchDTO, String currentMeasuredDepth) {
        String currentSection;//Holesection
        Long wellboreId = wellWellboreDao.getWellobreIdFromWellUid(drillingRoadMapSearchDTO.getPrimaryWellUid());
        List<HoleSection> sections = holeSectionRepository.findByWellboreId(wellboreId);
        sections = UOM.convertFromMoblizeUnits(sections, UOM.HOLE_SECTION_ATTRIBUTES);
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
        if(data!=null){
            return data.get(data.size()-1).getRigState();
        }
        else{
            return null;
        }
    }


    public String getWellMudWeight(DrillingRoadMapSearchDTO drillingRoadMapSearchDTO, String userName) {
        HashMap<String,Object> data = null;
        if (drillingRoadMapSearchDTO != null) {
            String url = "http://172.31.2.228:9001/api/v1/" + "mudAnalysis?wellUid=" + drillingRoadMapSearchDTO.getPrimaryWellUid() + "&userName=" + userName;
            data = restTemplate.exchange(url, HttpMethod.GET, createHeaders(), new ParameterizedTypeReference<HashMap<String,Object>>() {
            }).getBody();
        }
        if(data!=null){
            return null;
        }
        else{
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
