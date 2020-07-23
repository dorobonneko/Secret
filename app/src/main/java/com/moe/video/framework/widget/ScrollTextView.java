package com.moe.video.framework.widget;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;


public class ScrollTextView extends TextView{
	int lineCount;
    public ScrollTextView(Context context) {
        this(context,null);
        //setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        //setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
		setClickable(true);
		setMaxLines(3);
        //setMovementMethod(ScrollingMovementMethod.getInstance());
    }
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(),getMeasuredHeight()+getLineHeight());
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		TextPaint paint=getLayout().getPaint();
		String text=getMaxLines()>3?"收起":getMaxLines()==3?"展开":"";
		Rect bounds=new Rect();
		paint.getTextBounds(text,0,text.length(),bounds);
		paint.setColor(0xff000000);
		//float textHeight=(paint.descent()-paint.ascent())/2;
		canvas.drawText(text,0,text.length(),canvas.getWidth()-bounds.width()-getPaddingEnd(),canvas.getHeight()-bounds.height()/2,getLayout().getPaint());
	}

	@Override
	public boolean performClick()
	{
		if(getMaxLines()==3)
			setMaxLines(Integer.MAX_VALUE);
			else
			setMaxLines(3);
		return super.performClick();
	}

	
/*    float lastScrollY = 0;
	boolean start;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
		if(isNestedScrollingEnabled())
        switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				lastScrollY=ev.getRawY();
				start=startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
				return true;
			case MotionEvent.ACTION_MOVE:
				int diff=(int)(lastScrollY-ev.getRawY());
				lastScrollY=ev.getRawY();
				int[] con=new int[2],win=new int[2];
				if(dispatchNestedPreScroll(0,(int)diff,con,win)){
					if(con[1]<diff){
						diff=diff-con[1];
					}else{
						return true;
					}
				}
				int unUse=0,oldDiff=diff;
				
				if(diff>0){
					//上拉
					int preHeight=getScrollY()+getMeasuredHeight()+diff;
					int height=getLineCount()*getLineHeight();
					if(preHeight>height){
						diff=height-getScrollY()-getMeasuredHeight();
					}
				}else if(diff<0){
					if(getScrollY()+diff<0){
						diff=-getScrollY();
					}
				}
				
				scrollBy(0,(int)diff);
				unUse=oldDiff-diff;
				dispatchNestedScroll(0,diff,0,unUse,new int[]{0,-unUse});
				return true;
			case MotionEvent.ACTION_UP:
				
				break;
		}
        return super.onTouchEvent(ev);
    }

	@Override
	public boolean isNestedScrollingEnabled()
	{
		return true;
	}
*/
	
	
}
