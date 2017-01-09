package com.leedane.cn.util;
import java.util.HashMap;
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
     * 检验字符串是否是链接
     * @param origin
     * @return
     */
    public static boolean isLink(String origin){

        if(!StringUtil.isNull(origin)){
            Pattern p = Pattern.compile("(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*");
            String group;
            Matcher m=p.matcher(origin);
            while(m.find()){
                group = m.group().trim();
                if(StringUtil.isNotNull(group)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检验字符串的光标位置是否在标签内
     * @param string
     * @param focusIndex
     * @param tagLeft
     * @param tagRight
     * @return
     */
    public static boolean foucsIsInTag(String string, int focusIndex, String tagLeft, String tagRight){

        if(isNull(string) || focusIndex < 0 || string.length() < focusIndex)
            return false;

        //从左边找a的开始索引
        String left = string.substring(0, focusIndex);
        String right = string.substring(focusIndex, string.length());
        int leftLastTagIndex = left.lastIndexOf(tagRight);
        int leftFirstTagIndex = left.lastIndexOf(tagLeft);
        int rightFirtTagIndex = right.indexOf(tagLeft);
        int rightLastTagIndex = right.indexOf(tagRight);

        if((leftLastTagIndex == -1 && leftFirstTagIndex > 0) || leftLastTagIndex < leftFirstTagIndex){
            if((rightFirtTagIndex == -1 && rightLastTagIndex > 0) || rightFirtTagIndex > rightLastTagIndex){
                return true;
            }
        }
        return false;
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
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     */
    public static String TruncateUrlPage(String strURL){
        String strAllParam=null;
        String[] arrSplit=null;
        strURL=strURL.trim().toLowerCase();
        arrSplit=strURL.split("[?]");
        if(strURL.length()>1) {
            if(arrSplit.length>1) {
                if(arrSplit[1]!=null) {
                    strAllParam=arrSplit[1];
                }
            }
        }
        return strAllParam;
    }
    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param urlStr  url地址
     * @return  url请求参数部分
     */
    public static Map<String, String> getUrlParams(String urlStr){
        Map<String, String> mapRequest = new HashMap<>();

        String[] arrSplit = null;
        String strUrlParam = TruncateUrlPage(urlStr);
        if(strUrlParam==null) {
            return mapRequest;
        }

        arrSplit=strUrlParam.split("[&]");
        for(String strSplit:arrSplit)
        {
            String[] arrSplitEqual=null;
            arrSplitEqual= strSplit.split("[=]");
            //解析出键值
            if(arrSplitEqual.length>1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }
            else {
                if(arrSplitEqual[0]!="") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
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

    /**
     * 格式化距离
     * @param distance 单位为米
     * @return
     */
    public static String formatDistance(int distance){
        if(distance < 1)
            return "1米左右";
        return ((distance/1000) > 0 ? (distance/1000)+ "公里": "") +  ((distance%1000) > 0 ? (distance%1000) +"米": "");
    }

    public static void main(String[] args) {
        //System.out.println(StringUtil.changeNotNullAndUtf8("赵本山代表作被指\"丑化\"农民 丢弃农村传统底蕴"));
        System.out.println(StringUtil.isNumeric("-5.00"));
        System.out.println(StringUtil.isIntNumeric("5"));
    }

}