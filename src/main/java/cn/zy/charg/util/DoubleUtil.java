package cn.zy.charg.util;

public class DoubleUtil {
    public static Double getDecimalTo2(Double d){
        return (double) Math.round(d * 100) / 100;
    }
    public static Double getDecimal(Double d,int n){
        int pow =(int) Math.pow(10, n);
        return (double) Math.round(d * pow) / pow;
    }
}
