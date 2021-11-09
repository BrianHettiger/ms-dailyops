package com.moblize.ms.dailyops.utils;

public final class JSONResult {

    public final String status;
    public final String message;
    public final Object data;

    public JSONResult( ) {
        this( Status.UNKNOWN, "Unknown Request Result." );
    }
    public JSONResult(Status status, String message ) {
        this( status, message, false );
    }
    public JSONResult(Status status, String message, Object data ) {
        this.status = status.toString().toLowerCase();
        this.message = message;
        this.data = data;
    }

    enum Status {
        SUCCESS, FAILURE, INVALID, PROGRESS, CANCELLED, UNKNOWN
    }

    public static JSONResult result( Status status, String message, Object data ) {
        return new JSONResult( status, message, data );
    }

    public static JSONResult complete( Object data ) {
        return complete( "success", data );
    }
    public static JSONResult complete( String message, Object data ) {
        return new JSONResult( Status.SUCCESS, message , data );
    }

    public static JSONResult fail( ) {
        return fail( "Request could not be completed." );
    }
    public static JSONResult fail( String message ) {
        return new JSONResult( Status.FAILURE, message );
    }

    public static JSONResult invalid( ) {
        return invalid( "Request parameters did not meet requirements." );
    }
    public static JSONResult invalid( String message ) {
        return new JSONResult( Status.INVALID, message );
    }

    public static JSONResult progress( float completed, float total ) {
        return progress( completed * 100 / total );
    }
    public static JSONResult progress( float percentage ) {
        String message = percentage >= 100.0f ? "Process complete." : "Process working.";
        return new JSONResult( Status.PROGRESS, message, percentage );
    }

    public static JSONResult cancel( String message ) {
        return new JSONResult( Status.CANCELLED, message );
    }

}
