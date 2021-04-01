package com.moe.video.framework.util;
import java.util.Comparator;
import java.text.Collator;
import java.util.Locale;
import com.moe.video.framework.content.Package;

public class ChinaSort implements Comparator<Package>{
    private Collator mCollator;
    public ChinaSort(){
        mCollator=Collator.getInstance(Locale.CHINA);
    }
    @Override
    public int compare(Package p1,Package p2)
    {
        String name1=p1.title;
        String name2=p2.title;
        char n1=0;
        char n2=0;
        for(int i=0;i<Math.min(name1.length(),name2.length());i++){
            n1=name1.charAt(i);
            n2=name2.charAt(i);
            if(n1!=n2)
                break;
        }
        if(n1==n2)
        //判断长度
            return Integer.compare(name1.length(),name2.length());
        //非中文，直接比对
        if(n1<123&&n2<123){
            if(Character.toLowerCase(n1)==Character.toLowerCase(n2)){
                if(n1<='Z'&&n2>='a')
                    return 1;
                else
                if(n1>='a'&&n2<='Z')
                    return -1;
            }
            return Character.compare(Character.toLowerCase(n1),Character.toLowerCase(n2));

        }
        //第一个是中文，调换位置
        if(n1>128&&n2<128)
            return 1;
        //第二个是中文，位置不变
        if(n1<128&&n2>128)
            return -1;
        //都是中文
        return mCollator.compare(name1,name2);

    }
    
    
}
