package com.grabid.api;

public interface APIStatus {
    int SUCCESS = 200;
    int CREATED = 201;
    int NOT_FOUND = 404;
    int DELETE_SUCCESS = 204;
    int DELETE_ERROR = 404;
    int NO_CONTENT = 204;
    int UNPROCESSABLE = 422;
    int SERVER_ERROR = 500;
    int SESSION_EXPIRE = 401;
    int REVIEW = 202;
    int INVALID_CARD = 406;
}
