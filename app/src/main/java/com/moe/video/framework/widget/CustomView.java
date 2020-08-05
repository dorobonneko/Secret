package com.moe.video.framework.widget;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import org.json.JSONObject;
import org.json.JSONException;

public class CustomView extends View
{
	private JSONObject data;
	public CustomView(Context context){
		this(context,null);
	}
	public CustomView(Context context,AttributeSet attrs){
		super(context,attrs);
		setWillNotDraw(false);
	}
	public void load(String json){
		try
		{
			JSONObject obj=new JSONObject(json);
			data=obj;
		}
		catch (JSONException e)
		{}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if(data!=null){
			int wm=MeasureSpec.getMode(widthMeasureSpec);
			int hm=MeasureSpec.getMode(heightMeasureSpec);
			
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		if(data!=null){
			
		}
		}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(data!=null){
			
		}
	}
	
}
