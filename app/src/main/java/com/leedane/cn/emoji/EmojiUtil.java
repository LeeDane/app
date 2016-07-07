package com.leedane.cn.emoji;

import com.leedane.cn.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化生产emoji对象列表
 * Created by LeeDane on 2016/7/5.
 */
public class EmojiUtil {

    static List<EmojiBean> emojiBeanList = new ArrayList<>();
    public static int COLUMNS = 6; //每一列展示的数量
    public static int EMOJI_TAB_CONTENT = 0;
    static {
        EmojiBean emojiBean1 = new EmojiBean(R.drawable.angry, 1, "生气");
        EmojiBean emojiBean2 = new EmojiBean(R.drawable.anguished, 2, "痛苦");
        EmojiBean emojiBean3 = new EmojiBean(R.drawable.astonished, 3, "惊讶");
        EmojiBean emojiBean4 = new EmojiBean(R.drawable.blush, 4, "脸红");
        EmojiBean emojiBean5 = new EmojiBean(R.drawable.bowtie, 5, "领结");
        EmojiBean emojiBean6 = new EmojiBean(R.drawable.cold_sweat, 6, "冷汗");
        EmojiBean emojiBean7 = new EmojiBean(R.drawable.confounded, 7, "困惑");
        EmojiBean emojiBean8 = new EmojiBean(R.drawable.confused, 8, "糊涂");
        EmojiBean emojiBean9 = new EmojiBean(R.drawable.cry, 9, "哭");
        EmojiBean emojiBean10 = new EmojiBean(R.drawable.disappointed, 10, "沮丧");
        EmojiBean emojiBean11 = new EmojiBean(R.drawable.disappointed_relieved, 11, "轻沮丧");
        EmojiBean emojiBean12 = new EmojiBean(R.drawable.dizzy_face, 12, "眩晕");
        EmojiBean emojiBean13 = new EmojiBean(R.drawable.expressionless, 13, "无表情");
        EmojiBean emojiBean14 = new EmojiBean(R.drawable.fire, 14, "发火");
        EmojiBean emojiBean15 = new EmojiBean(R.drawable.flushed, 15, "兴奋");
        EmojiBean emojiBean16 = new EmojiBean(R.drawable.frowning, 16, "皱眉");
        EmojiBean emojiBean17 = new EmojiBean(R.drawable.grimacing, 17, "鬼脸");
        EmojiBean emojiBean18 = new EmojiBean(R.drawable.grin, 18, "咧嘴");
        EmojiBean emojiBean19 = new EmojiBean(R.drawable.grinning, 19, "笑嘻嘻");
        EmojiBean emojiBean20 = new EmojiBean(R.drawable.heart_eyes, 20, "色色");
        EmojiBean emojiBean21 = new EmojiBean(R.drawable.hushed, 21, "安静");
        EmojiBean emojiBean22 = new EmojiBean(R.drawable.innocent, 22, "无辜");
        EmojiBean emojiBean23 = new EmojiBean(R.drawable.joy, 23, "喜悦");
        EmojiBean emojiBean24 = new EmojiBean(R.drawable.kissing, 24, "亲亲");
        EmojiBean emojiBean25 = new EmojiBean(R.drawable.kissing_closed_eyes,25 , "调皮");
        EmojiBean emojiBean26 = new EmojiBean(R.drawable.kissing_heart, 26, "飞吻");
        EmojiBean emojiBean27 = new EmojiBean(R.drawable.kissing_smiling_eyes, 27, "轻吻");
        EmojiBean emojiBean28 = new EmojiBean(R.drawable.laughing, 28, "大笑");
        EmojiBean emojiBean29 = new EmojiBean(R.drawable.mask, 29, "闭嘴");
        EmojiBean emojiBean30 = new EmojiBean(R.drawable.neutral_face, 30, "平静");
        EmojiBean emojiBean31 = new EmojiBean(R.drawable.no_mouth, 31, "没脸");
        EmojiBean emojiBean32 = new EmojiBean(R.drawable.open_mouth, 32, "开口");
        EmojiBean emojiBean33 = new EmojiBean(R.drawable.pensive, 33, "沉思");
        EmojiBean emojiBean34 = new EmojiBean(R.drawable.persevere, 34 , "坚持");
        EmojiBean emojiBean35 = new EmojiBean(R.drawable.relaxed, 35, "放松");
        EmojiBean emojiBean36 = new EmojiBean(R.drawable.relieved, 36, "宽慰");
        EmojiBean emojiBean37 = new EmojiBean(R.drawable.satisfied, 37, "满意");
        EmojiBean emojiBean38 = new EmojiBean(R.drawable.sleeping, 38, "睡觉");
        EmojiBean emojiBean39 = new EmojiBean(R.drawable.sleepy, 39, "困乏");
        EmojiBean emojiBean40 = new EmojiBean(R.drawable.smile, 40, "微笑");
        EmojiBean emojiBean41 = new EmojiBean(R.drawable.smiley, 41, "笑容");
        EmojiBean emojiBean42 = new EmojiBean(R.drawable.smirk, 42, "傻笑");
        EmojiBean emojiBean43 = new EmojiBean(R.drawable.stuck_out_tongue, 43, "伸舌头");
        EmojiBean emojiBean44 = new EmojiBean(R.drawable.stuck_out_tongue_closed_eyes, 44, "闭眼伸舌头");
        EmojiBean emojiBean45 = new EmojiBean(R.drawable.stuck_out_tongue_winking_eye, 45, "顽皮");
        EmojiBean emojiBean46 = new EmojiBean(R.drawable.sunglasses, 46, "装酷");
        EmojiBean emojiBean47 = new EmojiBean(R.drawable.sweat, 47, "流汗");
        EmojiBean emojiBean48 = new EmojiBean(R.drawable.sweat_smile, 48, "冒汗");
        EmojiBean emojiBean49 = new EmojiBean(R.drawable.tired_face, 49, "厌烦");
        EmojiBean emojiBean50 = new EmojiBean(R.drawable.triumph, 50, "胜利");
        EmojiBean emojiBean51 = new EmojiBean(R.drawable.unamused, 51, "非娱乐");
        EmojiBean emojiBean52 = new EmojiBean(R.drawable.weary, 52, "疲倦");
        EmojiBean emojiBean53 = new EmojiBean(R.drawable.wink, 53, "使眼色");
        EmojiBean emojiBean54 = new EmojiBean(R.drawable.worried, 54, "担心");
        EmojiBean emojiBean55 = new EmojiBean(R.drawable.yum, 55, "美味");
        emojiBeanList.add(emojiBean1);
        emojiBeanList.add(emojiBean2);
        emojiBeanList.add(emojiBean3);
        emojiBeanList.add(emojiBean4);
        emojiBeanList.add(emojiBean5);
        emojiBeanList.add(emojiBean6);
        emojiBeanList.add(emojiBean7);
        emojiBeanList.add(emojiBean8);
        emojiBeanList.add(emojiBean9);
        emojiBeanList.add(emojiBean10);
        emojiBeanList.add(emojiBean11);
        emojiBeanList.add(emojiBean12);
        emojiBeanList.add(emojiBean13);
        emojiBeanList.add(emojiBean14);
        emojiBeanList.add(emojiBean15);
        emojiBeanList.add(emojiBean16);
        emojiBeanList.add(emojiBean17);
        emojiBeanList.add(emojiBean18);
        emojiBeanList.add(emojiBean19);
        emojiBeanList.add(emojiBean20);
        emojiBeanList.add(emojiBean21);
        emojiBeanList.add(emojiBean22);
        emojiBeanList.add(emojiBean23);
        emojiBeanList.add(emojiBean24);
        emojiBeanList.add(emojiBean25);
        emojiBeanList.add(emojiBean26);
        emojiBeanList.add(emojiBean27);
        emojiBeanList.add(emojiBean28);
        emojiBeanList.add(emojiBean29);
        emojiBeanList.add(emojiBean30);
        emojiBeanList.add(emojiBean31);
        emojiBeanList.add(emojiBean32);
        emojiBeanList.add(emojiBean33);
        emojiBeanList.add(emojiBean34);
        emojiBeanList.add(emojiBean35);
        emojiBeanList.add(emojiBean36);
        emojiBeanList.add(emojiBean37);
        emojiBeanList.add(emojiBean38);
        emojiBeanList.add(emojiBean39);
        emojiBeanList.add(emojiBean40);
        emojiBeanList.add(emojiBean41);
        emojiBeanList.add(emojiBean42);
        emojiBeanList.add(emojiBean43);
        emojiBeanList.add(emojiBean44);
        emojiBeanList.add(emojiBean45);
        emojiBeanList.add(emojiBean46);
        emojiBeanList.add(emojiBean47);
        emojiBeanList.add(emojiBean48);
        emojiBeanList.add(emojiBean49);
        emojiBeanList.add(emojiBean50);
        emojiBeanList.add(emojiBean51);
        emojiBeanList.add(emojiBean52);
        emojiBeanList.add(emojiBean53);
        emojiBeanList.add(emojiBean54);
        emojiBeanList.add(emojiBean55);

        EMOJI_TAB_CONTENT = emojiBeanList.size() / COLUMNS /4+ 1;
    }

    /**
     * 根据中文获取图片资源ID
     * @param name
     * @return
     */
    public static int getImgId(String name){
        int resId = 0;
        for(int i = 0; i< emojiBeanList.size(); i++){
            if(name.equals(emojiBeanList.get(i).getEmojiStr())){
                resId = emojiBeanList.get(i).getResId();
                break;
            }
        }
        return resId;
    }
}
