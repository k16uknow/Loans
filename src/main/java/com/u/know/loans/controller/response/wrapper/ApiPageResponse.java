package com.u.know.loans.controller.response.wrapper;

import java.util.List;

public record ApiPageResponse<T> (
        int page,
        int size,
        long total,
        int itemsInPage,
        List<T> data,
        ApiError error)
{
    public static <T> ApiPageResponse<T> page(int page, int size, long total, int itemsInPage, List<T> data) {
        return new ApiPageResponse<>(page, size, total, itemsInPage, data, null);
    }

    public static <T> ApiPageResponse<T> failure(String code, String error) {
        return new ApiPageResponse<>(0, 0, 0, 0, null, new ApiError(code, error));
    }

}
