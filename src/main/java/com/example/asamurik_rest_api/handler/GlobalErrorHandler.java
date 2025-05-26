package com.example.asamurik_rest_api.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalErrorHandler {


    public static ResponseEntity<Object> dataGagalDisimpan(String errorCode , HttpServletRequest request){
        return new ResponseHandler().handleResponse("FAILED TO SAVE !!", HttpStatus.INTERNAL_SERVER_ERROR,null,errorCode,request);
    }

    public static ResponseEntity<Object> dataGagalDiubah(String errorCode , HttpServletRequest request){
        return new ResponseHandler().handleResponse("DATA GAGAL DIUBAH", HttpStatus.INTERNAL_SERVER_ERROR,null,errorCode,request);
    }

    public static ResponseEntity<Object> dataGagalDihapus(String errorCode , HttpServletRequest request){
        return new ResponseHandler().handleResponse("DATA GAGAL DIHAPUS", HttpStatus.INTERNAL_SERVER_ERROR,null,errorCode,request);
    }

    public static ResponseEntity<Object> terjadiKesalahan(String errorCode , HttpServletRequest request){
        return new ResponseHandler().handleResponse("TERJADI KESALAHAN", HttpStatus.INTERNAL_SERVER_ERROR,null,errorCode,request);
    }

    public static ResponseEntity<Object> dataTidakDitemukan(String errorCode , HttpServletRequest request){
        return new ResponseHandler().handleResponse("DATA TIDAK DITEMUKAN", HttpStatus.BAD_REQUEST,null,errorCode,request);
    }
    public static ResponseEntity<Object> objectIsNull(String errorCode , HttpServletRequest request){
        return new ResponseHandler().handleResponse("OBJECT NULL !!", HttpStatus.BAD_REQUEST,null,errorCode,request);
    }
}
