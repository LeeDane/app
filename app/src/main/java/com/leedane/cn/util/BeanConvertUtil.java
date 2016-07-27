package com.leedane.cn.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.bean.HttpResponseAttentionBean;
import com.leedane.cn.bean.HttpResponseBlogBean;
import com.leedane.cn.bean.HttpResponseChatBgSelectWebBean;
import com.leedane.cn.bean.HttpResponseChatDetailBean;
import com.leedane.cn.bean.HttpResponseCircleOfFriendBean;
import com.leedane.cn.bean.HttpResponseCollectionBean;
import com.leedane.cn.bean.HttpResponseCommentOrTransmitBean;
import com.leedane.cn.bean.HttpResponseCommonBean;
import com.leedane.cn.bean.HttpResponseFanBean;
import com.leedane.cn.bean.HttpResponseFileBean;
import com.leedane.cn.bean.HttpResponseFriendBean;
import com.leedane.cn.bean.HttpResponseGalleryBean;
import com.leedane.cn.bean.HttpResponseLoginHistoryBean;
import com.leedane.cn.bean.HttpResponseMoodBean;
import com.leedane.cn.bean.HttpResponseMoodImagesBean;
import com.leedane.cn.bean.HttpResponseMyFriendsBean;
import com.leedane.cn.bean.HttpResponseNotificationBean;
import com.leedane.cn.bean.HttpResponseScoreBean;
import com.leedane.cn.bean.HttpResponseZanBean;
import com.leedane.cn.bean.HttpResponseZanUserBean;
import com.leedane.cn.bean.search.HttpResponseSearchBlogBean;
import com.leedane.cn.bean.search.HttpResponseSearchMoodBean;
import com.leedane.cn.bean.search.HttpResponseSearchUserBean;
import com.leedane.cn.financial.bean.HttpResponseFinancialBean;

/**
 * Bean转化工具类
 * Created by LeeDane on 2015/10/7.
 */
public class BeanConvertUtil {
    public static final String TAG = "BeanConvertUtil";
    private static Gson gson = new GsonBuilder()
   // .excludeFieldsWithoutExposeAnnotation() //不导出实体中没有用@Expose注解的属性
     //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
    .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
    //.setPrettyPrinting() //对json结果格式化

    //.setVersion(1.0)    //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
    //@Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
    //@Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
    .create();
    /**
     * 将字符串转化成List<BlogBean>集合
     * @param str
     * @return
     */
    public static HttpResponseBlogBean strConvertToBlogBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseBlogBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseBlogBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseBlogBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<BlogBean>集合
     * @param str
     * @return
     */
    public static HttpResponseMoodBean strConvertToMoodBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseMoodBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseMoodBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseMoodBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<CommentOrTransmitBean>集合
     * @param str
     * @return
     */
    public static HttpResponseCommentOrTransmitBean strConvertToCommentOrTransmitBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseCommentOrTransmitBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseCommentOrTransmitBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseCommentOrTransmitBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<ScoreBean>集合
     * @param str
     * @return
     */
    public static HttpResponseScoreBean strConvertToScoreBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseScoreBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseScoreBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseScoreBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<LoginHistoryBean>集合
     * @param str
     * @return
     */
    public static HttpResponseLoginHistoryBean strConvertToLoginHistoryBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseLoginHistoryBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseLoginHistoryBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseLoginHistoryBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<ChatDetailBean>集合
     * @param str
     * @return
     */
    public static HttpResponseChatDetailBean strConvertToChatDetailBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseChatDetailBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseChatDetailBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseChatDetailBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<ZanBean>集合
     * @param str
     * @return
     */
    public static HttpResponseZanBean strConvertToZanBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseZanBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseZanBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseZanBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<ZanUserBean>集合
     * @param str
     * @return
     */
    public static HttpResponseZanUserBean strConvertToZanUserBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseZanUserBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseZanUserBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseZanUserBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<GalleryBean>集合
     * @param str
     * @return
     */
    public static HttpResponseGalleryBean strConvertToGalleryBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseGalleryBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseGalleryBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseGalleryBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<FileBean>集合
     * @param str
     * @return
     */
    public static HttpResponseFileBean strConvertToFileBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseFileBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseFileBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseFileBean对象转换失败");
        }
        return null;
    }

    /**
     * 将字符串转化成List<ChatBgSelectWebBean>集合
     * @param str
     * @return
     */
    public static HttpResponseChatBgSelectWebBean strConvertToChatBgSelectWebBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseChatBgSelectWebBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseChatBgSelectWebBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseChatBgSelectWebBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseCommonBean对象
     * @param str
     * @return
     */
    public static HttpResponseCommonBean strConvertToCommonBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseCommonBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseCommonBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseCommonBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseMoodImagesBean对象
     * @param str
     * @return
     */
    public static HttpResponseMoodImagesBean strConvertToMoodImagesBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseMoodImagesBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseMoodImagesBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseMoodImagesBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseAttentionBean对象
     * @param str
     * @return
     */
    public static HttpResponseAttentionBean strConvertToAttentionBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseAttentionBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseAttentionBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseAttentionBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseAttentionBean对象
     * @param str
     * @return
     */
    public static HttpResponseFinancialBean strConvertToFinancialBeanBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseFinancialBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseFinancialBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseFinancialBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseCollectionBean对象
     * @param str
     * @return
     */
    public static HttpResponseCollectionBean strConvertToCollectionBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseCollectionBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseCollectionBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseCollectionBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseFanBean对象
     * @param str
     * @return
     */
    public static HttpResponseFanBean strConvertToFanBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseFanBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseFanBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseFanBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseCircleOfFriendBean对象
     * @param str
     * @return
     */
    public static HttpResponseCircleOfFriendBean strConvertToCircleOfFriendBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseCircleOfFriendBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseCircleOfFriendBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseCircleOfFriendBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseFriendBean对象
     * @param str
     * @return
     */
    public static HttpResponseFriendBean strConvertToFriendBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseFriendBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseFriendBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseFriendBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseNotificationBean对象
     * @param str
     * @return
     */
    public static HttpResponseNotificationBean strConvertToNotificationBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseNotificationBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseNotificationBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseNotificationBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseMyFriendsBean对象
     * @param str
     * @return
     */
    public static HttpResponseMyFriendsBean strConvertToMyFriendsBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseMyFriendsBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseMyFriendsBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseMyFriendsBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseSearchUserBean对象
     * @param str
     * @return
     */
    public static HttpResponseSearchUserBean strConvertToSearchUserBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseSearchUserBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseSearchUserBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseSearchUserBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseSearchBlogBean对象
     * @param str
     * @return
     */
    public static HttpResponseSearchBlogBean strConvertToSearchBlogBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseSearchBlogBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseSearchBlogBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseSearchBlogBean对象转换失败");
        }
        return null;
    }

    /**
     * 将响应请求的字符串转化成HttpResponseSearchMoodBean对象
     * @param str
     * @return
     */
    public static HttpResponseSearchMoodBean strConvertToSearchMoodBeans(String str){
        if(StringUtil.isNull(str)) return null;
        try{
            Log.d(TAG, "响应HttpResponseSearchMoodBean对象开始转换。。。");
            return gson.fromJson(str, HttpResponseSearchMoodBean.class);
        }catch (Exception e){
            Log.d(TAG, "响应HttpResponseSearchMoodBean对象转换失败");
        }
        return null;
    }
}
