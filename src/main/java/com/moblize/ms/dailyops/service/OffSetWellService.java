package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.KpiDashboardClient;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.dto.BCWDTO;
import com.moblize.ms.dailyops.dto.OffSetWellByDistance;
import com.moblize.ms.dailyops.dto.OffsetWell;
import com.moblize.ms.dailyops.dto.RopDataDTO;
import com.moblize.ms.dailyops.repository.GenericCustomRepository;
import com.moblize.ms.dailyops.utils.BCWException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OffSetWellService {

    private static final int DISTANCE_INCREMENT_STEP = 5;
    private static final int DISTANCE_INCREMENT_MAX = 15;
    private static final int SET_MAX_BCW_SELECTED = 5;
    @Value("${rest.nextgen.url}")
    private String rootURL;
    @Value("${rest.nextgen.user}")
    private String nextgenUsername;
    @Value("${rest.nextgen.pwd}")
    private String nextgenPassword;

    @Autowired
    private KpiDashboardClient kpiDashboardClient;
    @Autowired
    private GenericCustomRepository customRepository;
    @PersistenceContext
    EntityManager entityManager;

    RestTemplate restTemplate = new RestTemplate();
    public OffSetWellByDistance getBCWOffSetWellList(BCWDTO bcwdto) {
        OffSetWellByDistance offSetWellByDistance = new OffSetWellByDistance();
        try {
            offSetWellByDistance = processOffsetWellByDistance(bcwdto);
        } catch (Exception e){
            log.error("Error While processing wells", e);
        }
        return offSetWellByDistance;
    }

    private OffSetWellByDistance processOffsetWellByDistance(BCWDTO bcwDTO) throws Exception{
        OffSetWellByDistance offSetWellByDistance = new OffSetWellByDistance();
        this.getOffsetWellListByDistance(bcwDTO);
        this.setAllWellUIDList(bcwDTO);
        this.getFilteredBCWOffSetList(bcwDTO);
        this.getWellsKPIDetail(bcwDTO);
        this.sortByROP(bcwDTO);
        this.setROPSelectedForMatchedWell(bcwDTO);

        if (bcwDTO.getBcwCount() < DISTANCE_INCREMENT_STEP && bcwDTO.getDistance() < DISTANCE_INCREMENT_MAX) {
            bcwDTO.setDistance(bcwDTO.getDistance() + DISTANCE_INCREMENT_STEP);
            processOffsetWellByDistance(bcwDTO);
        }

        bcwDTO.getWellListByDistance().setMiles(bcwDTO.getDistance());
        return bcwDTO.getWellListByDistance();
    }
    private void setAllWellUIDList(BCWDTO bcwDTO) {
        Set<String> wellUIDList = new LinkedHashSet<>();
        wellUIDList.add(bcwDTO.getPrimaryWellUid());
        wellUIDList.addAll(bcwDTO.getOffsetWellUids());
        bcwDTO.setWellUID(wellUIDList);
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


    private void getFilteredBCWOffSetList(BCWDTO bcwDTO) {
        bcwDTO.setOffsetWellUids(getFilteredBCWOffSet(bcwDTO));
    }

    public List<String> getFilteredBCWOffSet(BCWDTO bcwDTO) {
        List<String> offsetList = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            String sql = "CALL usp_bcw_offset('" + bcwDTO.getWellUID() + "')";
            StoredProcedureQuery usp_bcw_offset = entityManager.createStoredProcedureQuery("usp_bcw_offset");
            usp_bcw_offset.registerStoredProcedureParameter("@welid", Object[].class, ParameterMode.IN);
            usp_bcw_offset.setParameter("@welid", bcwDTO.getWellUID().toArray());
            usp_bcw_offset.getResultList();
            while (resultSet.next()) {
                offsetList.add(resultSet.getString(1));
            }
        } catch (Exception e) {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    log.error("Error occurred while close result set", e1);
                }
            }
            log.error("Error occurred while getFilteredBCWOffSet to primary well", e);
        }
        return offsetList;
    }
    private void sortByROP(BCWDTO bcwDTO) {
        Map<String, Double> result = bcwDTO.getWellROP().entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        bcwDTO.setOffsetWellUids(result.keySet().stream().collect(Collectors.toList()));
    }
    private void getWellsKPIDetail(BCWDTO bcwDTO) {
        Map<String, Double> wellROP = new HashMap<>();
        bcwDTO.getOffsetWellUids().stream().map(well -> {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("label", "bcwROP");
            parameters.put("isTimeSelected", false);
            parameters.put("fromDepth", new Double(1));
            parameters.put("toDepth", new Double(50000));
            ArrayList<String> wellUidList = new ArrayList<>();
            wellUidList.add(well);
            parameters.put("wellUidList", wellUidList);
            RopDataDTO obj = null;
            try {
                //Object obj =  kpiDashboardClient.kpiTagRopBasedOnWells(false,"all", parameters);
                obj = kpiTagRopBasedOnWells(parameters);
                if (obj == null) {
                    wellROP.put(well, 0d);
                } else {
                    wellROP.put(well, Double.valueOf(obj.getViewData().get("bcwROP").toString()));
                }
            } catch (Exception e) {
                log.error("Error occurred in to get ROP detail",e);
            }
            return null;
        });
        bcwDTO.setWellROP(wellROP);
    }
    private void setROPSelectedForMatchedWell(BCWDTO bcwDTO) {
        bcwDTO.setBcwCount(0);
        List<OffsetWell> sortedOffSet = bcwDTO.getWellListByDistance().getOffsetWells().stream().map(well -> {
                if (bcwDTO.getOffsetWellUids().contains(well.getUid()) && bcwDTO.getBcwCount() < SET_MAX_BCW_SELECTED) {
                    well.setSelected(true);
                    well.setRopPerceivedall(bcwDTO.getWellROP().get(well.getUid()));
                    bcwDTO.setBcwCount(bcwDTO.getBcwCount()+1);
                } else {
                    well.setSelected(false);
                    well.setRopPerceivedall(-1d);
                }
                return well;
            }
        ).sorted(Comparator.comparingDouble(OffsetWell::getRopPerceivedall).reversed()).collect(Collectors.toList());
        bcwDTO.getWellListByDistance().setOffsetWells(sortedOffSet);
    }
    public RopDataDTO kpiTagRopBasedOnWells(Map<String, Object> parameters) {
        final Long startIndex = System.currentTimeMillis();
        final String resetUrl = "http://172.31.2.228:9104/" + "kpiTagRopBasedOnWells?addDepthRange=false&sectionName=all";
        final HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(parameters, createHeaders(nextgenUsername, nextgenPassword));
        return restTemplate.exchange(resetUrl, HttpMethod.POST, request, new ParameterizedTypeReference<RopDataDTO>(){}).getBody();
    }

    public OffSetWellByDistance getOffSetWell(final BCWDTO bcwdto) {
        final Long startIndex = System.currentTimeMillis();
        final String resetUrl = rootURL + "drillingRoadmap/getOffsetWells";
        final HttpEntity<BCWDTO> request = new HttpEntity<BCWDTO>(bcwdto, createHeaders(nextgenUsername, nextgenPassword));
        return restTemplate.exchange(resetUrl, HttpMethod.POST, request, new ParameterizedTypeReference<OffSetWellByDistance>(){}).getBody();
    }
    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = org.apache.commons.codec.binary.Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
            set("Content-Type", "application/json");
        }};
    }
}
