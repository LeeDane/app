package com.leedane.cn.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.activity.MoodActivity;
import com.leedane.cn.activity.PersonalActivity;
import com.leedane.cn.activity.TopicActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.customview.MoodTextView;
import com.leedane.cn.customview.RightBorderTextView;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.fragment.PersonalMoodFragment;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.helper.PraiseUserHelper;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 个人中心Tab为心情的数据展示的adapter对象
 * Created by LeeDane on 2015/12/8.
 */
public class PersonalMoodListViewAdapter extends BaseRecyclerViewAdapter<MoodBean> {
    public static final String TAG = "PersonalMoodListViewAdapter";
    private PersonalMoodFragment fragment;
    public PersonalMoodListViewAdapter(Context context, List<MoodBean> data, PersonalMoodFragment fragment) {
        super(context, data);
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ContentHolder(mHeaderView);
        else if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ContentHolder(mFooterView);
        }else{
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_mood_listview, parent, false);
            return new ContentHolder(layout);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == TYPE_HEADER)
            return;
        if(viewType == TYPE_FOOTER){
            return;
        }

        if(viewHolder instanceof ContentHolder && !CommonUtil.isEmpty(mDatas)) {
            Log.i("ViewAdapter", "position=" + position);
            final int pos = getRealPosition(viewHolder);
            final MoodBean moodBean = mDatas.get(pos);
            ContentHolder holder = ((ContentHolder) viewHolder);

            String content = moodBean.getContent();
            holder.content.setMovementMethod(LinkMovementMethod.getInstance());
            holder.content.setFocusable(false);
            holder.content.setDispatchToParent(true);
            holder.content.setLongClickable(false);
            //Spannable spannable = AssimilateUtils.assimilateOnlyLink(mContext, content);
            //spannable = AssimilateUtils.assimilateOnlyAtUser(mContext, spannable);
            //spannable = AssimilateUtils.assimilateOnlyTag(mContext, spannable);
            //spannable = InputHelper.displayEmoji(mContext.getResources(), spannable);

            Spannable spannable= AppUtil.textviewShowImg(mContext, content);
            spannable= AppUtil.textviewShowTopic(mContext, spannable, new AppUtil.ClickTextAction() {
                @Override
                public void call(String str) {
                    CommonHandler.startTopActivity(mContext, str);
                }
            });

            spannable= AppUtil.textviewShowAtUser(mContext, spannable, new AppUtil.ClickTextAction() {
                @Override
                public void call(String str) {
                    CommonHandler.startPersonalActivity(mContext, str);
                }
            });
            holder.content.setText(spannable);

            //viewHolder.getmContent().setText(content, TextView.BufferType.SPANNABLE);

            holder.froms.setTypeface(typeface);
            holder.froms.setText("来自：" + moodBean.getFroms());
            String createTime = moodBean.getCreateTime();
            if(StringUtil.isNull(createTime)){
                holder.time.setText("");
            }else{
                holder.time.setTypeface(typeface);
                holder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
            }

            if(StringUtil.isNotNull(moodBean.getLocation())){
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText("位置：" + moodBean.getLocation());
            }else{
                holder.location.setVisibility(View.GONE);
            }

            String praiseList = moodBean.getPraiseUserList();

            try{
                PraiseUserHelper helper = new PraiseUserHelper("t_mood", moodBean.getId());
                helper.setLikeUsers(mContext, holder.praiseList, praiseList, moodBean.getZanNumber());
            }catch (Exception e){
                e.printStackTrace();
            }

            final int index = position;
            holder.transmit.setText(mContext.getResources().getString(R.string.personal_transmit) + "(" + StringUtil.changeNotNull(moodBean.getTransmitNumber()) + ")");
            holder.comment.setText(mContext.getResources().getString(R.string.personal_comment) + "(" + StringUtil.changeNotNull(moodBean.getCommentNumber()) + ")");

            if(fragment != null){
                holder.more.setVisibility(View.VISIBLE);
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.showMoodListItemMenuDialog(index, mDatas.get(index));
                    }
                });
                holder.transmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ToastUtil.success(mContext, "转发", Toast.LENGTH_SHORT);
                        Intent it_transmit = new Intent();
                        PersonalActivity activity = (PersonalActivity) mContext;
                        it_transmit.setClass(activity, MoodActivity.class);
                        it_transmit.putExtra("operateType", EnumUtil.MoodOperateType.转发.value);
                        it_transmit.putExtra("moodObj", mDatas.get(index));
                        it_transmit.putExtra("width", "30");//展示的图像的宽度
                        it_transmit.putExtra("height", "30"); //展示的图像的高度
                        activity.startActivity(it_transmit);
                    }
                });
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ToastUtil.success(mContext, "评论", Toast.LENGTH_SHORT);
                        Intent it_transmit = new Intent();
                        PersonalActivity activity = (PersonalActivity) mContext;
                        it_transmit.setClass(activity, MoodActivity.class);
                        it_transmit.putExtra("operateType", EnumUtil.MoodOperateType.评论.value);
                        it_transmit.putExtra("moodObj", mDatas.get(index));
                        it_transmit.putExtra("width", "30");//展示的图像的宽度
                        it_transmit.putExtra("height", "30"); //展示的图像的高度
                        activity.startActivityForResult(it_transmit, PersonalActivity.MOOD_UPDATE_REQUEST_CODE);
                    }
                });
            }else{
                holder.userInfo.setVisibility(View.VISIBLE);
                holder.userInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonHandler.startPersonalActivity(mContext, moodBean.getCreateUserId());
                    }
                });
                ImageCacheManager.loadImage(moodBean.getUserPicPath(), holder.userPic, 30, 30);
                holder.account.setText(StringUtil.changeNotNull(moodBean.getAccount()));
                holder.transmit.setTextColor(mContext.getResources().getColor(R.color.gray));
                holder.comment.setTextColor(mContext.getResources().getColor(R.color.gray));
            }

            ImageUtil.addImages(mContext, moodBean.getImgs(), holder.imgContainer);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(pos, null);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLongClickListener == null)
                        return true;

                    mOnItemLongClickListener.onItemLongClick(pos);
                    return true;
                }
            });
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder{
        TextView froms;
        TextView time;
        ImageView more;
        MoodTextView content;
        LinearLayout imgContainer;
        TextView praiseList;
        RightBorderTextView comment;
        RightBorderTextView transmit;
        TextView location;
        LinearLayout userInfo;
        CircularImageView userPic;
        TextView account;
        public ContentHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView || itemView == mFooterView)
                return;
            content = (MoodTextView) itemView.findViewById(R.id.personal_mood_content);
            froms = (TextView) itemView.findViewById(R.id.personal_mood_froms);
            time = (TextView) itemView.findViewById(R.id.personal_mood_time);

            location =(TextView) itemView.findViewById(R.id.personal_mood_location_show);
            imgContainer = (LinearLayout) itemView.findViewById(R.id.personal_mood_img_container);
            transmit = (RightBorderTextView) itemView.findViewById(R.id.personal_mood_operate_transmit);
            comment = (RightBorderTextView) itemView.findViewById(R.id.personal_mood_operate_comment);
            praiseList = (TextView)itemView.findViewById(R.id.personal_mood_praise_list);
            if(fragment == null){
                userInfo = (LinearLayout) itemView.findViewById(R.id.personal_mood_user_info);
                userPic = (CircularImageView) itemView.findViewById(R.id.personal_mood_user_pic);
                account = (TextView)itemView.findViewById(R.id.personal_mood_account);
            }else
                more = (ImageView) itemView.findViewById(R.id.personal_mood_more);
        }
    }
}
