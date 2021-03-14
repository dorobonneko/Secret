package com.moe.video.framework.util;
import android.support.v7.widget.RecyclerView;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;
import android.content.Context;
import android.util.TypedValue;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;
import android.graphics.Canvas;

public class Space extends RecyclerView.ItemDecoration
{
	int size;
	public Space(Context context){
		size=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,context.getResources().getDisplayMetrics());
	}

    
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
	{
        int position=parent.getChildAdapterPosition(view);
        if(position==-1)return;
		switch(parent.getAdapter().getItemViewType(position)){
			case 2:
                outRect.set(size,size,size,size);
                break;
            case 7:
                
                if(parent.getLayoutManager() instanceof StaggeredGridLayoutManager){
                outRect.bottom = size;
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                if (position == 0 || position == 1) {
                    //outRect.top = size;
                }

                if (params.getSpanIndex() % 2 != 0) {
                    //右边
                    outRect.left = size / 2;
                    outRect.right = size;
                } else {
                    //左边
                    outRect.left = size;
                    outRect.right = size / 2;
                }
                }else{
                    outRect.set(size,size,size,size);
                }
				break;
			
		}
	}

    
}
