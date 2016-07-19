package com.leedane.cn.pinyin;

import com.leedane.cn.bean.ContactBean;

import java.util.Comparator;


/**
 * Created by LeeDane on 16/4/21.
 */
public class PinyinComparator implements Comparator<ContactBean.MembersEntity> {

	public int compare(ContactBean.MembersEntity o1, ContactBean.MembersEntity o2) {
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
