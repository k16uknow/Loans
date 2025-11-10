package com.u.know.loans.controller;

public record ApiResponse<T>(
    boolean success,
    T data,
    ApiError error) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(String code, String error){
        return new ApiResponse<>(false, null, new ApiError(code, error));
    }
}
