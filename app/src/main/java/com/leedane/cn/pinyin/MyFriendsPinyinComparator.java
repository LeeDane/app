package com.leedane.cn.pinyin;

import com.leedane.cn.bean.ContactBean;
import com.leedane.cn.bean.MyFriendsBean;

import java.util.Comparator;


/**
 * Created by LeeDane on 16/4/30.
 */
public class MyFriendsPinyinComparator implements Comparator<MyFriendsBean> {

	public int compare(MyFriendsBean o1, MyFriendsBean o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
