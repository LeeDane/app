package com.leedane.cn.util;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串相关工具类
 * Created by LeeDane on 2015/10/7.
 */
public class StringUtil {

    /**
     * 要是null，就将null转成""
     * @param origin
     * @return
     */
    public static String changeNotNull(Object origin){
        if(origin == null){
            return "";
        }else{
            String s = String.valueOf(origin);
            if(s.equals("null")){
                return "";
            }
            return s;
        }
    }

    /**
     * 判断字符串是否为空，为空返回true
     * @param origin 源字符串
     * @return
     */
    public static boolean isNull(String origin) {

        return origin == null || origin.trim() == "" || origin.trim().equals("")  || origin.equals("null") ? true : false;
    }

    /**
     * 判断字符串是否为空，不为空返回true
     * @param origin 源字符串
     * @return
     */
    public static boolean isNotNull(String origin) {
        return !isNull(origin);
    }
    /**
     * 判断字符串是否全部都是整型数字
     * 注意：浮点数在此方法返回为false
     * @param origin  源字符串
     * @return
     */
    public static boolean isIntNumeric(String origin){
        Pattern pattern = Pattern.compile("^-?[0-9]+");
        Matcher isNum = pattern.matcher(origin);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否全部都是数字(整型和浮点型)
     * @param origin  源字符串
     * @return
     */
    public static boolean isNumeric(String origin){
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]*");
        Matcher isNum = pattern.matcher(origin);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 将字符串转成int类型
     * @param origin
     * @return
     */
    public static int stringToInt(String origin){
        int v = 0;
        if(StringUtil.isIntNumeric(origin)){
            v = Integer.parseInt(String.valueOf(origin));
        }
        return v;
    }

    /**
     * 将对象转化成boolean类型,出错为false
     * @param obj  boolean型的对象
     * @return
     */
    public static boolean changeObjectToBoolean(Object obj) {
        try {
            return Boolean.parseBoolean(String.valueOf(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 截取字符串中指定的值后获取剩下的部分
     * @param origin  原始字符串
     * @param key  要去掉的值
     * @return
     */
    public static String getExtraValue(String origin, String key){
        if(StringUtil.isNull(origin)) return null;

        return origin.replaceAll(key, "");
    }

    /**
     * 将对象转化成int类型
     * @param obj  整形的对象
     * @return
     */
    public static int changeObjectToInt(Object obj) {
        try {
            return Integer.parseInt(String.valueOf(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将对象转化成float类型
     * @param obj  float的对象
     * @return
     */
    public static float changeObjectToFloat(Object obj) {
        try {
            return Float.parseFloat(String.valueOf(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将对象转化成long类型
     * @param obj  长整形的对象
     * @return
     */
    public static long changeObjectToLong(Object obj) {
        try {
            return Long.parseLong(String.valueOf(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    /**
     * 从Map集合中获取key对应的值，没有该key返回""
     * @param map json对象
     * @param key  键名称
     * @return
     */
    public static String getStringValue(Map<String, Object> map, String key){
        if(map.containsKey(key)){
            return String.valueOf(map.get(key));
        }
        return "";
    }

    /**
     * 判断一个地址是否是7牛网站
     * @param url
     * @return
     */
    public static boolean isQiniuUrl(String url){
        if(StringUtil.isNull(url))
            return false;
        return url.contains("7xnv8i.com1.z0.glb.clouddn.com");
    }

    /**
     * 将boolean类型的成功与否对象转成通用的字符串
     * @param success
     * @return
     */
    public static String getSuccessOrNoStr(boolean success){
        if(success){
            return "成功";
        }else{
            return "失败";
        }
    }

    /**
     * 获取字符串后缀(这里只获取最后是.后的字符串)
     * @param name  原始名称
     * @return
     */
    public static String getSuffixs(String name){
        if(StringUtil.isNull(name)){
            return null;
        }
        return name.substring(name.lastIndexOf(".") + 1, name.length());
    }

    /**
     * 获取文件名(包括后缀)
     * @param name  原始名称
     * @return
     */
    public static String getFileName(String name){
        if(StringUtil.isNull(name)){
            return null;
        }
        return name.substring(name.lastIndexOf("/") + 1, name.length());
    }

    /**
     * 获取文本的[]里面的ID(int)作为列表输出
     * @param content
     * @return
     */
    public static Set<Integer> getImgIdList(String content){
        Set<Integer> imgIds = new HashSet<Integer>();
        if(isNotNull(content)){
            Pattern p=Pattern.compile("\\[([^\\[\\]]+)\\]");
            Matcher m=p.matcher(content);
            String group = null;
            while(m.find()){
                group = m.group().trim();
                if(isNotNull(group) && group.startsWith("[") && group.endsWith("]")){
                    group = group.substring(1, group.length() -1);
                    if(changeObjectToInt(group) > 0)
                        imgIds.add(changeObjectToInt(group));
                }
            }
        }
        return imgIds;
    }

    /**
     * 将true或者false对应转化成1或者0
     * @param b
     * @return
     */
    public static int changeTrueOrFalseToInt(boolean b){
        if(b)
            return 1;
        return 0;
    }

    /**
     * 将1或者0对应转化成true或者false
     * @param i  只有i == 1才是true，其他是false
     * @return
     */
    public static boolean changeIntToTrueOrFalse(int i){
        if(i == 1)
            return true;
        return false;
    }

    public static void main(String[] args) {
        //System.out.println(StringUtil.changeNotNullAndUtf8("赵本山代表作被指\"丑化\"农民 丢弃农村传统底蕴"));
        System.out.println(StringUtil.isNumeric("-5.00"));
        System.out.println(StringUtil.isIntNumeric("5"));
    }

}