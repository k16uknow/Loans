package com.u.know.loans.controller.response.wrapper;

public record ApiResponse<T>(
    boolean success,
    T data,
    ApiError failure) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> failure(String code, String error){
        return new ApiResponse<>(false, null, new ApiError(code, error));
    }
}
