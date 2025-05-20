package com.example.asamurik_rest_api.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IService<T, I> {

    ResponseEntity<Object> save(T t, HttpServletRequest request);

    ResponseEntity<Object> update(I id, T t, HttpServletRequest request);

    ResponseEntity<Object> delete(I id, HttpServletRequest request);

    ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request);

    ResponseEntity<Object> findById(I id, HttpServletRequest request);

    ResponseEntity<Object> findByParam(Pageable pageable, String columnName, String value, HttpServletRequest request);

}
