package cn.zy.charg.util;


import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtil {

    public static String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(.{8,20})$";
    public static String REGEX_LICENSE = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]{1}[A-Z]{1}([A-Z0-9]{5,6}|[A-Z0-9]{4}[挂学警港澳]{1})$";
    public static String REGEX_PHONENUMBER = "^1\\d{10}$";

    public static String getMapToString(Map<String,String> map,String separator){
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyArray.length; i++) {
            if (map.get(keyArray[i]).trim().length() > 0) {
                sb.append(keyArray[i]).append("=").append(map.get(keyArray[i]).trim());
            }
            if(i != keyArray.length-1){
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static String getMapToString2(Map<String,String[]> map,String separator){
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyArray.length; i++) {
            String temp = getArrayToString(map.get(keyArray[i]));
            if (temp.length() > 0) {
                sb.append(keyArray[i]).append("=").append(temp);
            }
            if(i != keyArray.length-1){
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public static String getArrayToString(String[] s){
        String temp="";
        for(int i = 0;i<s.length;i++){
            temp+=s[i].trim();
            if(i!=s.length-1){
                temp+=",";
            }
        }
        return temp;
    }
    public static boolean matchString(String s,String regex){
        return Pattern.compile(regex).matcher(s).matches();
    }
    /*字符串判空，null/空/空格 返回false*/
    public static boolean isEmptyStr(String s){
        if(s==null||s.equals("")||s.trim().equals("")){
            return true;
        }
        return false;
    }

    //生成随机字符串
    public static String getRandomString(int stringLength) {
        String string = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < stringLength; i++) {
            int index = (int) Math.floor(Math.random() * string.length());//向下取整0-25
            sb.append(string.charAt(index));
        }
        return sb.toString();
    }
}
