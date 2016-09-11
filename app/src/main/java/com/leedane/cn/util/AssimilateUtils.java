package com.leedane.cn.util;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 后台很多String数据需要做本地化处理
 * Created by LeeDane on 16/9/8.
 */
public class AssimilateUtils {

    // @thanatosx
    public static final Pattern PatternAtUserWithHtml = Pattern.compile(
            "<a href=['\"]http://my\\.oschina\\.net/([0-9a-zA-Z_]+)['\"][^<>]*>(@[^@<>\\s]+)</a>"
    );
    public static final Pattern PatternAtUser = Pattern.compile(
            "@[^@\\s:]+"
    );

    // #Java#
    public static final Pattern PatternSoftwareTagWithHtml = Pattern.compile(
            "<a href=['\"]([^'\"]*)['\"][^<>]*>(#[^#@<>\\s]+#)</a>"
    );
    public static final Pattern PatternSoftwareTag = Pattern.compile(
            "#([^#@<>\\s]+)#"
    );

    // @user links
    public static final Pattern PatternAtUserAndLinks = Pattern.compile(
            "<a href=['\"]http://my\\.oschina\\.net/([0-9a-zA-Z_]+)['\"][^<>]*>(@[^@<>\\s]+)</a>|<a href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // links
    public static final Pattern PatternLinks = Pattern.compile(
            "<a href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    private interface Action1{
        void call(String str);
    }

    /**
     * 高亮@User
     * @param context Context
     * @param content string
     * @return
     */
    public static Spannable highlightAtUser(Context context, CharSequence content){
        return highlightAtUser(context, new SpannableString(content));
    }

    /**
     * @see #highlightAtUser(Context, Spannable)
     * @param context Context
     * @param spannable string
     * @return
     */
    public static Spannable highlightAtUser(Context context, Spannable spannable){
        String str = spannable.toString();
        Matcher matcher = PatternAtUser.matcher(str);
        while (matcher.find()){
            ForegroundColorSpan span = new ForegroundColorSpan(0XFF6888AD);
            spannable.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 与 {@link #highlightAtUser(Context, Spannable)} 不同的是这个方法是针对<a>标签包裹的@User
     * @param context Context
     * @param content string
     * @return
     */
    public static Spannable assimilateOnlyAtUser(final Context context, CharSequence content){
        return assimilate(content, PatternAtUserWithHtml, 1, 2, new Action1() {
            @Override
            public void call(String str) {
                //OtherUserHomeActivity.show(context, str);
                ToastUtil.success(context, str);
            }
        });
    }

    /**
     *
     * @param context Context
     * @param content string
     * @return
     */
    public static Spannable assimilateOnlyTag(final Context context, CharSequence content){
        return assimilate(content, PatternSoftwareTagWithHtml, 1, 2, new Action1() {
            @Override
            public void call(String str) {
                //UIHelper.showUrlRedirect(context, str);
                ToastUtil.success(context, str);
            }
        });
    }

    /**
     * 格式化link链接
     *
     * 注意: 最好在最后处理这个过程,否则有些需要特殊处理的就会被它格式化掉
     *
     * @param context Context
     * @param content CharSequence
     * @return A spannable object
     */
    public static Spannable assimilateOnlyLink(final Context context, CharSequence content){
        return assimilate(content, PatternLinks, 1, 2, new Action1() {
            @Override
            public void call(String str) {
                //UIHelper.showUrlRedirect(context, str);
                ToastUtil.success(context, str);
            }
        });
    }

    @Deprecated
    public static Spannable assimilateTagAndAtUser(final Context context, String content){

        // 现将#software#的标签去掉
        content = content.replaceAll("<a[^<>]*>(#[^#@<>\\s]+#)</a>", "$1");

        Matcher matcher;
        Map<String, Pair<Integer, Integer>> maps = new HashMap<>();

        while (true){
            matcher = PatternAtUserAndLinks.matcher(content);
            if (matcher.find()){
                String group1 = matcher.group(1);
                String group2 = matcher.group(2);
                String group3 = matcher.group(3);
                String group4 = matcher.group(4);
                if (group1 != null && group2 != null){
                    content = matcher.replaceFirst(group2);
                    maps.put(group1, new Pair<>(matcher.start(), matcher.start() + group2.length()));
                }else if (group3 != null && group4 != null){
                    content = matcher.replaceFirst(group4);
                    maps.put(group3, new Pair<>(matcher.start(), matcher.start() + group4.length()));
                }else {
                    content = matcher.replaceFirst("");
                }
                continue;
            }
            break;
        }

        Spannable spannable = new SpannableString(content);

        for (final Map.Entry<String, Pair<Integer, Integer>> entry : maps.entrySet()){
            final String substr = entry.getKey();
            final Pair<Integer, Integer> pair = entry.getValue();
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (substr.startsWith("http://") || substr.startsWith("https://")){
                        //UIHelper.openBrowser(context, substr);
                        ToastUtil.success(context, substr);
                    }else {
                        //OtherUserHomeActivity.show(context, substr);
                        ToastUtil.success(context, substr);
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);
                    ds.setUnderlineText(false);
                }
            };
            spannable.setSpan(span, pair.first, pair.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        //再处理#software#
        matcher = PatternSoftwareTag.matcher(content);
        while (matcher.find()){
            final String tag = matcher.group(1);
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putString("topic", tag);
                    //UIHelper.showSimpleBack(context, SimpleBackPage.TWEET_TOPIC_LIST, bundle);
                    ToastUtil.success(context, tag);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);
                    ds.setUnderlineText(false);
                }
            };
            spannable.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 本地化处理
     * @param content 处理内容
     * @param pattern 匹配规则
     * @param index0 使用的组号
     * @param index1 显示的组号
     * @param action 回调函数
     * @return Spannable
     */
    @Deprecated
    private static Spannable assimilate(String content, Pattern pattern, int index0, int index1, final Action1 action){
        Matcher matcher;
        Map<String, Pair<Integer, Integer>> maps = new HashMap<>();

        while (true){
            matcher = pattern.matcher(content);
            if (matcher.find()){
                String group0 = matcher.group(index0);
                String group1 = matcher.group(index1);
                content = matcher.replaceFirst(group1);
                maps.put(group0, new Pair<>(matcher.start(), matcher.start() + group1.length()));
                continue;
            }
            break;
        }

        Spannable spannable = new SpannableString(content);

        for (final Map.Entry<String, Pair<Integer, Integer>> entry : maps.entrySet()){
            final String substr = entry.getKey();
            final Pair<Integer, Integer> pair = entry.getValue();
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    action.call(substr);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);
                    ds.setUnderlineText(false);
                }
            };
            spannable.setSpan(span, pair.first, pair.second, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return spannable;
    }

    /**
     * 本地化处理
     * @param sequence 处理内容
     * @param pattern 匹配规则
     * @param index0 使用的组号
     * @param index1 显示的组号
     * @param action 回调函数
     * @return Spannable
     */
    private static Spannable assimilate(CharSequence sequence, Pattern pattern, int index0, int index1, final Action1 action){
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        Matcher matcher;
        while (true) {
            matcher = pattern.matcher(builder.toString());
            if (matcher.find()){
                final String group0 = matcher.group(index0);
                final String group1 = matcher.group(index1);
                builder.replace(matcher.start(), matcher.end(), group1);
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        action.call(group0);
                    }
                };
                builder.setSpan(span, matcher.start(), matcher.start() + group1.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                continue;
            }
            break;
        }
        return builder;
    }

}
