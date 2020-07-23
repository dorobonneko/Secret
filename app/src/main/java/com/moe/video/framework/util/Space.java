package com.moe.video.framework.util;
import android.support.v7.widget.RecyclerView;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;
import android.content.Context;
import android.util.TypedValue;

public class Space extends RecyclerView.ItemDecoration
{
	int size;
	public Space(Context context){
		size=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,context.getResources().getDisplayMetrics());
	}
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
	{
		switch(parent.getAdapter().getItemViewType(parent.getChildAdapterPosition(view))){
			case 1:
				break;
			case 2:
				outRect.set(size,size,size,size);
				break;
			
		}
		}
	
}
