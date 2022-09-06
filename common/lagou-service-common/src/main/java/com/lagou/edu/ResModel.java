package com.lagou.edu;

import lombok.Data;

@Data
public class ResModel<D> {

    private String code;
    private String message;
    private D data;

    private ResModel(String code, String message, D data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public <T>ResModel SUCCESS( String code, String message, T data ){

        return new ResModel(code,message,data);
    }

    public static ResModel SUCCESS( String code, String message  ){
        return new ResModel(code,message,null);
    }

    public static ResModel SUCCESS(  String message  ){
        return new ResModel("1000",message,null);
    }

    public static <T>ResModel SUCCESS(  String message, T data ){
        return new ResModel("1000",message,data);
    }

    public static <T>ResModel SUCCESS( T data ){
        return new ResModel("1000","操作成功",data);
    }

    public static ResModel<?> SUCCESS(){
        return new ResModel("1000","操作成功",null);
    }

    public static ResModel FAIL( String code, String message  ){
        return new ResModel(code,message,null);
    }

    public static ResModel FAIL(  String message  ){
        return new ResModel("4000",message,null);
    }

    public static <T>ResModel<T> FAIL(  T failData  ){
        return new ResModel("4000","操作失败",failData);
    }

    public static ResModel FAIL(){
        return new ResModel("4000","操作失败",null);
    }

}
