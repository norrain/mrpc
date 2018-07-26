package com.mrpc.core.utils;

import java.util.Objects;

/**
 * @Author mark.z
 */
public class Validation {

    /**
     *  判断数字必须大于某个数字 否则抛异常
     */
    public static void requireAboveNum(Number number,Number aboveNum){
        Objects.requireNonNull(number);
        Objects.requireNonNull(aboveNum);
        if (number.longValue() <= aboveNum.longValue())
            throw new RuntimeException(String.format("%s必须大于%s",number,aboveNum));
    }

    /**
     *  判断数字必须大于某个数字 否则抛异常
     */
    public static void requireAboveNum(Number number,Number aboveNum,String message){
        Objects.requireNonNull(number);
        Objects.requireNonNull(aboveNum);
        if (number.longValue() <= aboveNum.longValue())
            throw new RuntimeException(message);
    }
}
