package com.example.asamurik_rest_api.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalSuccessHandler {
    public static ResponseEntity<Object> dataBerhasilDisimpan(HttpServletRequest request){
        return new ResponseHandler().handleResponse("SAVE SUCCESS !!", HttpStatus.CREATED,null,null,request);
    }
    public static ResponseEntity<Object> dataBerhasilDisimpan(String message, HttpServletRequest request){
        return new ResponseHandler().handleResponse(message, HttpStatus.CREATED,null,null,request);
    }
    public static ResponseEntity<Object> dataBerhasilDiubah(HttpServletRequest request){
        return new ResponseHandler().handleResponse("DATA BERHASIL DIUBAH", HttpStatus.OK,null,null,request);
    }
    public static ResponseEntity<Object> dataBerhasilDihapus(HttpServletRequest request){
        return new ResponseHandler().handleResponse("DATA BERHASIL DIHAPUS", HttpStatus.OK,null,null,request);
    }
    public static ResponseEntity<Object> dataDitemukan(Object data,HttpServletRequest request){
        return new ResponseHandler().handleResponse("DATA DITEMUKAN", HttpStatus.OK,data,null,request);
    }
}
