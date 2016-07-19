package com.leedane.cn.emoji;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * emoji每页的显示
 * Created by LeeDane on 2016/7/5.
 */
public class EmojiPageFragment extends Fragment {
    private List<EmojiBean> datas;
    private GridView sGrid;
    private EmojiGridAdapter adapter;
    private OnEmojiClickListener listener;

    public EmojiPageFragment(){}

    public EmojiPageFragment(int index, int type, OnEmojiClickListener l) {
        initData(index, type);
        this.listener = l;
    }

    private void initData(int index, int type) {
        datas = new ArrayList<>();
        int start = index * EmojiUtil.COLUMNS * 4;
        int end = 0;
        if(index < EmojiUtil.EMOJI_TAB_CONTENT-1){
            end = start + EmojiUtil.COLUMNS * 4;
        }else{
            end = EmojiUtil.emojiBeanList.size() ;
        }
        for(int i = start; i < end; i++){
            datas.add(EmojiUtil.emojiBeanList.get(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        sGrid = new GridView(getActivity());
        sGrid.setNumColumns(EmojiUtil.COLUMNS);
        adapter = new EmojiGridAdapter(getActivity(), datas);
        sGrid.setAdapter(adapter);
        sGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (listener != null) {
                    listener.onEmojiClick((EmojiBean) parent.getAdapter()
                            .getItem(position));
                }
            }
        });
        sGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.show(getContext(), ((EmojiBean) parent.getAdapter().getItem(position)).getEmojiStr(), Toast.LENGTH_SHORT, Gravity.CENTER, 0, 0);
                return true;
            }
        });
        sGrid.setSelector(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
        return sGrid;
    }

    public GridView getRootView() {
        return sGrid;
    }
}
