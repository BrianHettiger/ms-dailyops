package com.moblize.ms.dailyops.dto;

public class ResponseDTO {

    public final String status;
    public final String message;
    public final Object data;

    private ResponseDTO( ) {
        this( ResponseDTO.Status.UNKNOWN, "Unknown Request Result." );
    }
    private ResponseDTO(ResponseDTO.Status status, String message ) {
        this( status, message, false );
    }
    private ResponseDTO(ResponseDTO.Status status, String message, Object data ) {
        this.status = status.toString().toLowerCase();
        this.message = message;
        this.data = data;
    }

    enum Status {
        SUCCESS, FAILURE, INVALID, PROGRESS, CANCELLED, UNKNOWN
    }

    public static ResponseDTO result(ResponseDTO.Status status, String message, Object data ) {
        return new ResponseDTO( status, message, data );
    }

    public static ResponseDTO complete( Object data ) {
        return complete( "success", data );
    }
    public static ResponseDTO complete(String message, Object data ) {
        return new ResponseDTO( ResponseDTO.Status.SUCCESS, message , data );
    }

    public static ResponseDTO fail( ) {
        return fail( "Request could not be completed." );
    }
    public static ResponseDTO fail( String message ) {
        return new ResponseDTO( ResponseDTO.Status.FAILURE, message );
    }

    public static ResponseDTO invalid( ) {
        return invalid( "Request parameters did not meet requirements." );
    }
    public static ResponseDTO invalid( String message ) {
        return new ResponseDTO( ResponseDTO.Status.INVALID, message );
    }

    public static ResponseDTO progress( float completed, float total ) {
        return progress( completed * 100 / total );
    }
    public static ResponseDTO progress( float percentage ) {
        String message = percentage >= 100.0f ? "Process complete." : "Process working.";
        return new ResponseDTO( ResponseDTO.Status.PROGRESS, message, percentage );
    }

    public static ResponseDTO cancel( String message ) {
        return new ResponseDTO( ResponseDTO.Status.CANCELLED, message );
    }


}
