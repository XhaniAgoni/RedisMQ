package com.example.redisservice.entity;

import com.example.redisservice.entity.enumobject.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RedisReturnObject对象", description = "redis返回结果对象")
public class Result<T> {

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回key数据")
    private String key;

    @ApiModelProperty(value = "返回数据")
    private T data;



    public Result(){}

    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        if (data != null)
            result.setData(data);
        return result;
    }

    protected static <T> Result<T> build( String key,T data) {
        Result<T> result = new Result<T>();
        if (data != null)
            result.setData(data);
            result.setKey(key);
        return result;
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body); // 封装查询结果
        result.setCode(resultCodeEnum.getCode()); // 设置数据的状态
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum, String key) {
        Result<T> result = build(key,body); // 封装查询结果
        result.setCode(resultCodeEnum.getCode()); // 设置数据的状态
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Result<T> build(Integer code, String message) {
        Result<T> result = build(null);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static<T> Result<T> ok(){
        return Result.ok(null);
    }

    /**
     * 操作成功
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Result<T> ok(T data){
        Result<T> result = build(data);
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> ok(String key,T data){
        Result<T> result = build(key,data);
        return build(data,ResultCodeEnum.SUCCESS,key);
    }

    public static<T> Result<T> fail(){
        return Result.fail(null);
    }

    public static<T> Result<T> refererror(){
        return Result.refererror(null);
    }


    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Result<T> fail(T data){
        Result<T> result = build(data);
        return build(data, ResultCodeEnum.FAIL);
    }

    public static<T> Result<T> refererror(T data){
        Result<T> result = build(data);
        return build(data, ResultCodeEnum.REFERENCE_ERROR);
    }

    public static<T> Result<T> refererror(String key,T data){
        Result<T> result = build(key,data);
        return build(data, ResultCodeEnum.REFERENCE_ERROR,key);
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }

    public boolean isOk() {
        if(this.getCode().intValue() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            return true;
        }
        return false;
    }
}
