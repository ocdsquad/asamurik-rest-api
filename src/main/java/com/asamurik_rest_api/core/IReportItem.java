package com.asamurik_rest_api.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;


public interface IReportItem<T, I> {
    ResponseEntity<Object> submitReport(I itemId, I userId, String message, HttpServletRequest request);

    ResponseEntity<Object> notifyItemOwner(T reportId, HttpServletRequest request);
}

