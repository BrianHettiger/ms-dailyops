package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.LogChannel;
import com.moblize.ms.dailyops.domain.mongo.MongoLog;
import com.moblize.ms.dailyops.dto.ParsedLogDataDTO;
import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import play.Logger;

import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class AvgPerStandCalculation {

    public List<SurveyRecord> getAvgPerStandCalculation(MongoLog depthLog,List<SurveyRecord> surveys) throws Exception {

        long startTime = System.currentTimeMillis();

        try {

            if (surveys.isEmpty()) {
                return surveys;
            }

            Iterator<ParsedLogDataDTO> recIter = depthLog.getParsedLogData().iterator();
            Iterator<SurveyRecord> surveyIter = surveys.iterator();

            SurveyRecord curSurvey = surveys.get(0);
            ChunkState curChunkState = new ChunkState();
            Double curHoleDepth = null;
            Double previousHoleDepth = null;
            ParsedLogDataDTO currentDataRecord = null;

            // move to the right record that matches the first survey
            while (recIter.hasNext()) {
                currentDataRecord = recIter.next();
                curHoleDepth = currentDataRecord.getIndex().doubleValue();
                if (curHoleDepth > curSurvey.getCurMeasuredDepth()) {
                    break;
                }
                previousHoleDepth = curHoleDepth;
            }

            // if we don't assign any value to previousHoleDepth from the code above
            // we give the current hole depth value to it
            if(previousHoleDepth == null){
                previousHoleDepth = curHoleDepth;
            }

            // move to the right survey that matches the hole depth
            while (surveyIter.hasNext()) {
                if (curSurvey.getPreviousMeasuredDepth() != null
                        && curHoleDepth != null
                        &&  curHoleDepth <= curSurvey.getCurMeasuredDepth()) {
                    break;
                }
                curSurvey = surveyIter.next();
            }

            while (recIter.hasNext()) {
                Double holeDepth = currentDataRecord.getIndex().doubleValue();

                if (surveyIter.hasNext()) {
                    // still in the current survey
                    if (curSurvey.getPreviousMeasuredDepth() < holeDepth && holeDepth <= curSurvey.getCurMeasuredDepth()) {
                        curChunkState = processRecord(curChunkState, currentDataRecord, previousHoleDepth);
                    }
                    else { // new survey.
                        populateDataForSurvey(curSurvey, curChunkState);
                        curSurvey = surveyIter.next();
                        curChunkState = new ChunkState();
                    }
                }else{
                    if (curSurvey.getPreviousMeasuredDepth() < holeDepth && holeDepth <= curSurvey.getCurMeasuredDepth() ) {
                        curChunkState = processRecord(curChunkState, currentDataRecord, previousHoleDepth);
                    }
                }

                previousHoleDepth = holeDepth;
                currentDataRecord = recIter.next();
            }
            populateDataForSurvey(curSurvey, curChunkState);

        } catch (Exception e) {
            log.error("Error:", e);
        }

        long processAvgTime = System.currentTimeMillis();
        Logger.info(".....................................................processAvgTime takes "
                + (float) (processAvgTime - startTime) / 1000);
        return surveys;
    }

    private void populateDataForSurvey(SurveyRecord curSurvey, ChunkState curChunkState) {
        curSurvey.setAvgRopByRotaryDrilling((curChunkState.countOfRopByRotaryDrilling > 0) ? curChunkState.sumOfRopByRotaryDrilling
                / curChunkState.countOfRopByRotaryDrilling
                : 0f);
        curSurvey.setAvgWobByRotaryDrilling((curChunkState.countOfWobByRotaryDrilling > 0) ? curChunkState.sumOfWobByRotaryDrilling
                / curChunkState.countOfWobByRotaryDrilling
                : 0f);
        curSurvey.setAvgDiffPressureByRotaryDrilling((curChunkState.countOfDiffPressureByRotaryDrilling > 0) ? curChunkState.sumOfDiffPressureByRotaryDrilling
                / curChunkState.countOfDiffPressureByRotaryDrilling
                : 0f);
        curSurvey.setAvgTorqueByRotaryDrilling((curChunkState.countOfTorqueByRotaryDrilling > 0) ? curChunkState.sumOfTorqueByRotaryDrilling
                / curChunkState.countOfTorqueByRotaryDrilling
                : 0f);
        curSurvey.setAvgRpmByRotaryDrilling((curChunkState.countOfRpmByRotaryDrilling > 0) ? curChunkState.sumOfRpmByRotaryDrilling
                / curChunkState.countOfRpmByRotaryDrilling
                : 0f);
        curSurvey.setAvgMudFlowInByRotaryDrilling((curChunkState.countOfMudFlowInByRotaryDrilling > 0) ? curChunkState.sumOfMudFlowInByRotaryDrilling
                / curChunkState.countOfMudFlowInByRotaryDrilling
                : 0f);
        curSurvey.setAvgGammaRay1ShiftedByRotaryDrilling((curChunkState.countOfGammaRay1ShiftedByRotaryDrilling > 0) ? curChunkState.sumOfGammaRay1ShiftedByRotaryDrilling
                / curChunkState.countOfGammaRay1ShiftedByRotaryDrilling
                : 0f);

        curSurvey.setAvgRopBySliding((curChunkState.countOfRopBySliding > 0) ? curChunkState.sumOfRopBySliding
                / curChunkState.countOfRopBySliding
                : 0f);
        curSurvey.setAvgWobBySliding((curChunkState.countOfWobBySliding > 0) ? curChunkState.sumOfWobBySliding
                / curChunkState.countOfWobBySliding
                : 0f);
        curSurvey.setAvgDiffPressureBySliding((curChunkState.countOfDiffPressureBySliding > 0) ? curChunkState.sumOfDiffPressureBySliding
                / curChunkState.countOfDiffPressureBySliding
                : 0f);
        curSurvey.setAvgTorqueBySliding((curChunkState.countOfTorqueBySliding > 0) ? curChunkState.sumOfTorqueBySliding
                / curChunkState.countOfTorqueBySliding
                : 0f);
        curSurvey.setAvgRpmBySliding((curChunkState.countOfRpmBySliding > 0) ? curChunkState.sumOfRpmBySliding
                / curChunkState.countOfRpmBySliding
                : 0f);
        curSurvey.setAvgMudFlowInBySliding((curChunkState.countOfMudFlowInBySliding > 0) ? curChunkState.sumOfMudFlowInBySliding
                / curChunkState.countOfMudFlowInBySliding
                : 0f);
        curSurvey.setAvgGammaRay1ShiftedBySliding((curChunkState.countOfGammaRay1ShiftedBySliding > 0) ? curChunkState.sumOfGammaRay1ShiftedBySliding
                / curChunkState.countOfGammaRay1ShiftedBySliding
                : 0f);

        Double slidePercent = curChunkState.getSlidePercentage();
        curSurvey.setSlidingPercentage(slidePercent);
    }

    private static ChunkState processRecord(ChunkState chunkState, ParsedLogDataDTO currentDataRecord, Double previousHoleDepth) {
        String rigState = currentDataRecord.getRigState();
        Float rop = currentDataRecord.getChannelValueAsFloat(LogChannel.ROP_CHANNEL_NAME);
        Float wob = currentDataRecord.getChannelValueAsFloat(LogChannel.WEIGHT_ON_BIT_MAX_CHANNEL_NAME);
        Float diffPressure = currentDataRecord
                .getChannelValueAsFloat(LogChannel.DIFF_PRESSURE_CHANNEL_NAME);
        Float torque =currentDataRecord
                .getChannelValueAsFloat(LogChannel.SURFACE_TORQUE_MAX_CHANNEL_NAME);
        Float rpm = currentDataRecord.getChannelValueAsFloat(LogChannel.RPMA_CHANNEL_NAME);
        Float mudFlowIn = currentDataRecord.getChannelValueAsFloat(LogChannel.MUD_FLOW_IN_CHANNEL_NAME);
        Float gammaRay1Shifted = currentDataRecord.getChannelValueAsFloat(LogChannel.GAMMA_RAY_1_SHIFTED_CHANNEL_NAME);

        Double holeDepth = currentDataRecord.getIndex().doubleValue();

        if (rigState == null) {
            return chunkState;
        }

        switch (rigState) {
            case "ROTATE DRILLING":
                if (rop != null) {
                    chunkState.sumOfRopByRotaryDrilling += rop;
                    chunkState.countOfRopByRotaryDrilling++;
                }
                if (wob != null) {
                    chunkState.sumOfWobByRotaryDrilling += wob;
                    chunkState.countOfWobByRotaryDrilling++;
                }
                if (diffPressure != null) {
                    chunkState.sumOfDiffPressureByRotaryDrilling += diffPressure;
                    chunkState.countOfDiffPressureByRotaryDrilling++;
                }
                if (torque != null) {
                    chunkState.sumOfTorqueByRotaryDrilling += torque;
                    chunkState.countOfTorqueByRotaryDrilling++;
                }
                if (rpm != null) {
                    chunkState.sumOfRpmByRotaryDrilling += rpm;
                    chunkState.countOfRpmByRotaryDrilling++;
                }
                if (mudFlowIn != null) {
                    chunkState.sumOfMudFlowInByRotaryDrilling += mudFlowIn;
                    chunkState.countOfMudFlowInByRotaryDrilling++;
                }
                if (gammaRay1Shifted != null) {
                    chunkState.sumOfGammaRay1ShiftedByRotaryDrilling += gammaRay1Shifted;
                    chunkState.countOfGammaRay1ShiftedByRotaryDrilling++;
                }

                chunkState.increaseRotaryFootage(holeDepth - previousHoleDepth);

                break;
            case "SLIDE DRILLING":
                if (rop != null) {
                    chunkState.sumOfRopBySliding += rop;
                    chunkState.countOfRopBySliding++;
                }
                if (wob != null) {
                    chunkState.sumOfWobBySliding += wob;
                    chunkState.countOfWobBySliding++;
                }
                if (diffPressure != null) {
                    chunkState.sumOfDiffPressureBySliding += diffPressure;
                    chunkState.countOfDiffPressureBySliding++;
                }
                if (torque != null) {
                    chunkState.sumOfTorqueBySliding += torque;
                    chunkState.countOfTorqueBySliding++;
                }
                if (rpm != null) {
                    chunkState.sumOfRpmBySliding += rpm;
                    chunkState.countOfRpmBySliding++;
                }
                if (mudFlowIn != null) {
                    chunkState.sumOfMudFlowInBySliding += mudFlowIn;
                    chunkState.countOfMudFlowInBySliding++;
                }
                if (gammaRay1Shifted != null) {
                    chunkState.sumOfGammaRay1ShiftedBySliding += gammaRay1Shifted;
                    chunkState.countOfGammaRay1ShiftedBySliding++;
                }
                chunkState.increaseSlideFootage(holeDepth - previousHoleDepth);
                break;
            default:
                Logger.debug("Not a Drilling Rig State: " + rigState + " at depth: " + holeDepth);
                break;
        }

        return chunkState;
    }

    private static class ChunkState {

        public Float sumOfRopByRotaryDrilling = 0f;
        public int countOfRopByRotaryDrilling = 0;
        public Float sumOfWobByRotaryDrilling = 0f;
        public int countOfWobByRotaryDrilling = 0;
        public Float sumOfDiffPressureByRotaryDrilling = 0f;
        public int countOfDiffPressureByRotaryDrilling = 0;
        public Float sumOfTorqueByRotaryDrilling = 0f;
        public int countOfTorqueByRotaryDrilling = 0;
        public Float sumOfRpmByRotaryDrilling = 0f;
        public int countOfRpmByRotaryDrilling = 0;
        public Float sumOfMudFlowInByRotaryDrilling = 0f;
        public int countOfMudFlowInByRotaryDrilling = 0;
        public Float sumOfGammaRay1ShiftedByRotaryDrilling = 0f;
        public int countOfGammaRay1ShiftedByRotaryDrilling = 0;

        public Float sumOfRopBySliding = 0f;
        public int countOfRopBySliding = 0;
        public Float sumOfWobBySliding = 0f;
        public int countOfWobBySliding = 0;
        public Float sumOfDiffPressureBySliding = 0f;
        public int countOfDiffPressureBySliding = 0;
        public Float sumOfTorqueBySliding = 0f;
        public int countOfTorqueBySliding = 0;
        public Float sumOfRpmBySliding = 0f;
        public int countOfRpmBySliding = 0;
        public Float sumOfMudFlowInBySliding = 0f;
        public int countOfMudFlowInBySliding = 0;
        public Float sumOfGammaRay1ShiftedBySliding = 0f;
        public int countOfGammaRay1ShiftedBySliding = 0;

        public Double footageDrilledBySliding = null;
        public Double footageDrilledByRotating = null;

        public void increaseSlideFootage(Double footage) {
            footageDrilledBySliding = footageDrilledBySliding == null ? footage : footageDrilledBySliding + footage;
        }

        public void increaseRotaryFootage(Double footage) {
            footageDrilledByRotating = footageDrilledByRotating == null ? footage : footageDrilledByRotating + footage;
        }

        private Double getTotalDrilledFootage() {
            Double total = null;
            if (footageDrilledBySliding != null) {
                total = footageDrilledBySliding;
            }
            if (footageDrilledByRotating != null) {
                total = total == null ? footageDrilledByRotating : total + footageDrilledByRotating;
            }

            return total;
        }

        public Double getSlidePercentage() {
            Double total = getTotalDrilledFootage();
            if (total != null && total > 0.0) {
                return footageDrilledBySliding != null ? footageDrilledBySliding/total : 0.0f;
            }

            return null;
        }
    }
}
