package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.WellFormation;
import com.moblize.ms.dailyops.dto.DrillingRoadMapWells;
import com.moblize.ms.dailyops.repository.GenericCustomRepository;
import com.moblize.ms.dailyops.utils.MetricsLogger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
@Component
public class WellFormationDAO {

    @Autowired
    private GenericCustomRepository genericCustomRepository;

    @PersistenceContext
    EntityManager entityManager;

    private static final String well_formation = "well_formation";

    public  List<DrillingRoadMapWells> getBCWDataFormation(List<String> wellUids, List<String> primaryWellFormation) {
        List<WellFormation> ret = new ArrayList<>();
        List<DrillingRoadMapWells> result = new ArrayList<>();
        try {
            Long startTime = System.currentTimeMillis();

            String highestValueQuery ="select formationName,max(highestRopAvg) as highestRopAvg FROM WellFormation " +
                "   where wellUID in (:wellUid) " +
                "   and formationName in(:formationTypes) " +
                "  group by formationName ";
            Query highestQuery = genericCustomRepository.find(highestValueQuery);
            highestQuery.setParameter("wellUid", wellUids);
            highestQuery.setParameter("formationTypes", primaryWellFormation);
            List<Object[]>resultList = highestQuery.getResultList();
            StringBuilder bcwQueryStr = new StringBuilder();
            bcwQueryStr .append(" select wf FROM WellFormation  wf  where wellUID in (:wellUid)  ");

            String subString ="";
                for(Object[] formation : resultList){
                    if(subString =="") {
                        subString = "(formationName ='"+formation[0].toString()+"' and highestRopAvg="+Float.parseFloat(formation[1].toString())+")";
                    }else{
                        subString += " or (formationName ='"+formation[0].toString()+"' and highestRopAvg="+Float.parseFloat(formation[1].toString())+")";
                    }
                }
            bcwQueryStr.append( "   and ( ");
            bcwQueryStr.append(subString);
            bcwQueryStr.append( " ) ");
            bcwQueryStr.append(" order by startDepth desc");

            Query bcwQuery = genericCustomRepository.find(bcwQueryStr.toString());
            bcwQuery.setParameter("wellUid", wellUids);
            ret =  (List<WellFormation>)bcwQuery.getResultList();


            ret.forEach(entity -> {
                DrillingRoadMapWells wellsFormation = new DrillingRoadMapWells();
                wellsFormation.setWellUid(entity.getWellUID());
                wellsFormation.setMD(String.valueOf((int)entity.getStartDepth()));
                wellsFormation.setFormationName(entity.getFormationName());
                wellsFormation.setMudFlowInAvg(String.valueOf((int)entity.getMudFlowAvg()));
                wellsFormation.setSurfaceTorqueMax(String.valueOf((int)entity.getSurfaceTorqueMax()));
                wellsFormation.setPumpPress(String.valueOf((int)entity.getPumpPressureAvg()));
                wellsFormation.setWeightonBitMax(String.valueOf((int)entity.getWeightOnBitMax()));
                wellsFormation.setROPAvg(String.valueOf((int)entity.getHighestRopAvg()));
                wellsFormation.setHoleSize(String.valueOf((float)entity.getHoleSize()));
                wellsFormation.setRPMA(String.valueOf((int)entity.getRpmaAvg()));
                wellsFormation.setDiffPressure(String.valueOf((int)entity.getDiffPressureAvg()));
                wellsFormation.setAnnotationText("");
                result.add(wellsFormation);
            });

            MetricsLogger.dbTime(well_formation, startTime, System.currentTimeMillis());
            MetricsLogger.dbCount(well_formation, result.size());
        } catch (Exception e) {
            log.error( "Error:",e);
        }
        return result;
    }
    @Async
    public CompletableFuture<List<DrillingRoadMapWells>> getPrimaryWellDrillingData(String wellUid) {
        List<WellFormation> ret = new ArrayList<>();
        List<DrillingRoadMapWells> result = new ArrayList<>();
        try {
            Long startTime = System.currentTimeMillis();
            StringBuilder bcwQueryStr = new StringBuilder();
            bcwQueryStr .append(" select wf FROM WellFormation  wf  where wellUID = (:wellUid)  ");
            bcwQueryStr.append(" order by startDepth ASC");

            Query bcwQuery = genericCustomRepository.find(bcwQueryStr.toString());
            bcwQuery.setParameter("wellUid", wellUid);
            ret =  (List<WellFormation>)bcwQuery.getResultList();

            result = ret.stream().map(entity -> {
                DrillingRoadMapWells wellsFormation = new DrillingRoadMapWells();
                wellsFormation.setWellUid(entity.getWellUID());
                wellsFormation.setMD(String.valueOf((int)entity.getStartDepth()));
                wellsFormation.setFormationName(entity.getFormationName());
                wellsFormation.setMudFlowInAvg(String.valueOf((int)entity.getMudFlowAvg()));
                wellsFormation.setSurfaceTorqueMax(String.valueOf((int)entity.getSurfaceTorqueMax()));
                wellsFormation.setPumpPress(String.valueOf((int)entity.getPumpPressureAvg()));
                wellsFormation.setWeightonBitMax(String.valueOf((int)entity.getWeightOnBitMax()));
                wellsFormation.setROPAvg(String.valueOf((int)entity.getHighestRopAvg()));
                wellsFormation.setHoleSize(String.valueOf((float)entity.getHoleSize()));
                wellsFormation.setRPMA(String.valueOf((int)entity.getRpmaAvg()));
                wellsFormation.setDiffPressure(String.valueOf((int)entity.getDiffPressureAvg()));
                wellsFormation.setAnnotationText("");
                return wellsFormation;
            }).collect(Collectors.toList());

            MetricsLogger.dbTime(well_formation, startTime, System.currentTimeMillis());
            MetricsLogger.dbCount(well_formation, result.size());
            log.info("Query: PrimaryWellDrillingData calculated for wells took : {}s, size: {}", System.currentTimeMillis() - startTime, result.size());
        } catch (Exception e) {
            log.error( "Error:",e);
        }
        return CompletableFuture.completedFuture(result);
    }

    public  List<DrillingRoadMapWells> getBcwData(List<String> wellUid) {
        List<WellFormation> ret = new ArrayList<>();
        List<DrillingRoadMapWells> result = new ArrayList<>();
        try {
            Long startTime = System.currentTimeMillis();
            StringBuilder bcwQueryStr = new StringBuilder();
            bcwQueryStr .append(" select wf FROM WellFormation  wf  where wellUID in (:wellUid) order by startDepth ASC");

            Query bcwQuery = genericCustomRepository.find(bcwQueryStr.toString());
            bcwQuery.setParameter("wellUid", wellUid);
            ret =  (List<WellFormation>)bcwQuery.getResultList();

            result = ret.stream().map(entity -> {
                DrillingRoadMapWells wellsFormation = new DrillingRoadMapWells();
                wellsFormation.setWellUid(entity.getWellUID());
                wellsFormation.setMD(String.valueOf((int)entity.getStartDepth()));
                wellsFormation.setFormationName(entity.getFormationName());
                wellsFormation.setMudFlowInAvg(String.valueOf((int)entity.getMudFlowAvg()));
                wellsFormation.setSurfaceTorqueMax(String.valueOf((int)entity.getSurfaceTorqueMax()));
                wellsFormation.setPumpPress(String.valueOf((int)entity.getPumpPressureAvg()));
                wellsFormation.setWeightonBitMax(String.valueOf((int)entity.getWeightOnBitMax()));
                wellsFormation.setROPAvg(String.valueOf((int)entity.getHighestRopAvg()));
                wellsFormation.setHoleSize(String.valueOf((float)entity.getHoleSize()));
                wellsFormation.setRPMA(String.valueOf((int)entity.getRpmaAvg()));
                wellsFormation.setDiffPressure(String.valueOf((int)entity.getDiffPressureAvg()));
                wellsFormation.setAnnotationText("");
                return wellsFormation;
            }).collect(Collectors.toList());

            MetricsLogger.dbTime(well_formation, startTime, System.currentTimeMillis());
            MetricsLogger.dbCount(well_formation, result.size());
            log.info("Query: BcwData calculated for wells took : {}s, size: {}", System.currentTimeMillis() - startTime, result.size());
        } catch (Exception e) {
            log.error( "Error:",e);
        }
        return result;
    }
}
