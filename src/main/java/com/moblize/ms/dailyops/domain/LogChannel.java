package com.moblize.ms.dailyops.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@PersistenceUnit(name = "witsml")
@Table(name = "log_logcurveinfo")
@Data
public class LogChannel {

    public static final String BIT_DEPTH_CHANNEL_NAME = "BitDepth";
    public static final String HOLE_DEPTH_CHANNEL_NAME = "HoleDepth";
    public static final String HOOKLOAD_AVG_CHANNEL_NAME = "HookloadAvg";
    public static final String HOOKLOAD_MAX_CHANNEL_NAME = "HookloadMax";
    public static final String PUMP_PRESSURE_CHANNEL_NAME = "PumpPressure";
    public static final String WEIGHT_ON_BIT_MAX_CHANNEL_NAME = "WeightonBitMax";
    public static final String WEIGHT_ON_BIT_AVG_CHANNEL_NAME = "WeightonBitAvg";
    public static final String ROP_CHANNEL_NAME = "ROPAvg";
    public static final String BLOCK_POSITION_CHANNEL_NAME = "BlockPostion";
    public static final String DIFF_PRESSURE_CHANNEL_NAME = "DiffPressure";
    public static final String RPMA_CHANNEL_NAME = "RPMA";
    public static final String SURFACE_TORQUE_MAX_CHANNEL_NAME = "SurfaceTorqueMax";
    public static final String SURFACE_TORQUE_AVG_CHANNEL_NAME = "SurfaceTorqueAvg";
    public static final String MUD_FLOW_IN_CHANNEL_NAME = "MudFlowInAvg";
    public static final String BLOCK_SPEED_CHANNEL_NAME = "BlockSpeed";
    public static final String DATE_CHANNEL_NAME = "DATE";
    public static final String TIME_CHANNEL_NAME = "TIME";
    public static final String RIGSTATE_CHANNEL_NAME = "RigState";
    public static final String PUMPCYCLE_CHANNEL_NAME = "PumpCycle";
    public static final String RIG_STATE_CHANNEL_NAME = "RigState";
    public static final String NEW_RIG_STATE_KEY = "NewRigState";
    public static final String PUMP_PRESSURE_SKEW = "PumpPressureSkew";
    public static final String SURFACE_TORQUE_STANDARD_DEVIATION = "SurfaceTorqueStdDev";
    public static final String HOLE_CLEANING_P = "HoleCleaningP";
    public static final String INCLINATION_CHANNEL_NAME = "Inclination";

    public static final String MSE_CHANNEL_NAME = "MSE";
    public static final String GAMMA_RAY_1_SHIFTED_CHANNEL_NAME = "GammaRay1Shifted";

    @Id
    @Column(name = "log_logcurveinfo_pkey")
    private Long id;

    @Column(name = "log_log_fkey")
    private Long fkid;

    @Column(name = "log_mnemonic")
    private String mnemonic;

    @Column(name = "log_columnindex")
    private String columnIndex;

    @Column(name = "log_curvedescription")
    private String description;

    @Column(name = "log_typelogdata")
    private String type;

    /* The row index is really the r column index for log_log_data */
    @Column(name = "log_rowindex")
    private int rowIndex;

    @Column(name = "log_unit")
    private String unit;
}
