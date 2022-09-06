package com.lagou.edu.util;

public class UtilForData {
    /**
     * 根据位数生成随机数
     * @param digit     位数
     * @return          生成的随机数
     */
    public static String randomCodeByDigit (int digit){

        int pow = (int) Math.pow(10, digit-1);
        int result = (int) ((Math.random() * 9 + 1) * pow);
        return result+"";
    }

    public static String randomCodeByDigit(){
        return randomCodeByDigit(6);
    }
}
