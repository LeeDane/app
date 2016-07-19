package com.leedane.cn.helper;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.leedane.cn.activity.ZanUserActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.ZanUserBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.StringUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 点赞用户列表的展示类
 * Created by leedane on 2016/6/1.
 */
public class PraiseUserHelper {

    private String tableName;
    private int tableId;

    public PraiseUserHelper(String tableName, int tableId){
        this.tableName = tableName;
        this.tableId = tableId;
    }

    /**
     * 展示赞的用户列表
     * @throws JSONException
     */
    private List<ZanUserBean> buildZanUser( String praiseList, int zanNumber){

        List<ZanUserBean> zanUserBeans = new ArrayList<>();
        if(StringUtil.isNotNull(praiseList) && zanNumber > 0){
            String[] users = praiseList.split(";");
            String[] u;
            ZanUserBean zanUserBean;
            for(String user: users){
                if(StringUtil.isNotNull(user)){
                    zanUserBean = new ZanUserBean();
                    u = user.split(",");
                    zanUserBean.setAccount(u[1]);
                    zanUserBean.setCreateUserId(StringUtil.changeObjectToInt(u[0]));
                    zanUserBeans.add(zanUserBean);
                }
            }
        }

        return zanUserBeans;
    }

    public void setLikeUsers(Context contet, TextView likeUser,  String praiseList, int zanUserNumber) {
        List<ZanUserBean> zanUserBeans = buildZanUser(praiseList, zanUserNumber);
        // 构造多个超链接的html, 通过选中的位置来获取用户名
        if (zanUserBeans.size() > 0) {
            likeUser.setVisibility(View.VISIBLE);
            likeUser.setMovementMethod(LinkMovementMethod.getInstance());
            likeUser.setFocusable(false);
            likeUser.setLongClickable(false);
            likeUser.setText(addClickablePart(contet, zanUserBeans, zanUserNumber), TextView.BufferType.SPANNABLE);
        } else {
            likeUser.setVisibility(View.GONE);
            likeUser.setText("");
        }
    }

    /**
     * @return
     */
    private SpannableStringBuilder addClickablePart(final Context context, final List<ZanUserBean> zanUserBeans, int zanUserNumber) {

        StringBuilder sbBuilder = new StringBuilder();
        for (int i = 0; i < zanUserBeans.size(); i++) {
            sbBuilder.append(zanUserBeans.get(i).getAccount()).append("、");
        }

        String likeUsersStr = sbBuilder.substring(0, sbBuilder.lastIndexOf("、"));

        // 第一个赞图标
        // ImageSpan span = new ImageSpan(AppContext.getInstance(),
        // R.drawable.ic_unlike_small);
        SpannableString spanStr = new SpannableString("");
        // spanStr.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
        ssb.append(likeUsersStr);

        String[] likeUsers = likeUsersStr.split("、");

        if (likeUsers.length > 0) {
            // 最后一个
            for (int i = 0; i < likeUsers.length; i++) {
                final String name = likeUsers[i];
                final int start = likeUsersStr.indexOf(name) + spanStr.length();
                final int index = i;
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        CommonHandler.startPersonalActivity(context, zanUserBeans.get(index).getCreateUserId());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(context.getResources().getColor(R.color.blueAccountLink)); // 设置文本颜色
                        // 去掉下划线
                        ds.setUnderlineText(false);
                    }
                }, start, start + name.length(), 0);
            }
        }
        if (likeUsers.length < zanUserNumber) {
            ssb.append("等");
            int start = ssb.length();
            String more = zanUserNumber + "人";
            ssb.append(more);
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, ZanUserActivity.class);
                    intent.putExtra("table_id", tableId);
                    intent.putExtra("table_name", tableName);
                    context.startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.blueAccountLink)); // 设置文本颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, start, start + more.length(), 0);
            return ssb.append("觉得很赞");
        } else {
            return ssb.append("觉得很赞");
        }
    }
}
