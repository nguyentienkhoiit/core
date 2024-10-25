package com.khoinguyen.core.dto.response;

public class ResponseError extends ResponseData {

    ResponseError(int status, String message) {
        super(status, message, null);
    }
}
