package com.moe.video.framework.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.moe.video.framework.R;
import org.json.JSONArray;
import org.json.JSONObject;
import android.widget.TextView;
import org.json.JSONException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import java.util.List;
import com.moe.pussy.Pussy;
import android.widget.ImageView;
import com.moe.pussy.transformer.CropTransformer;
import android.view.Gravity;
import com.moe.pussy.transformer.RoundTransformer;
import android.support.v7.widget.GridLayoutManager;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import android.text.Html;
import android.widget.Button;
import android.content.res.TypedArray;

public class PostAdapter<T extends PostAdapter.BaseViewholder> extends RecyclerView.Adapter<T>
{
	private List data;
	public PostAdapter(List data){
		this.data=data;
	}
	@Override
	public T onCreateViewHolder(ViewGroup p1, int p2)
	{
		switch(p2){
			case 1://标题
				return (T)new TitleViewholder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_title_item_view,p1,false));
			case 2://post
				return (T)new PostViewholder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_item_view,p1,false));
			case 3:
				return (T)new DetailViewholder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_detail_item_view,p1,false));
			case 4:
				return (T)new PlayViewholder(new Button(p1.getContext()));
		}
		return null;
	}

	@Override
	public void onBindViewHolder(T p1, int p2)
	{
		NativeObject obj=(NativeObject) data.get(p2);
		p1.title.setText(obj.get("title").toString());
		if(p1 instanceof DetailViewholder){
			DetailViewholder dvh=(PostAdapter<T>.DetailViewholder) p1;
			Pussy.$(dvh.itemView.getContext()).load(ScriptRuntime.toString(obj.get("thumb"))).execute().transformer(new CropTransformer(Gravity.CENTER),new RoundTransformer(dvh.itemView.getResources().getDisplayMetrics(),5)).into(dvh.thumb);
			String info=ScriptRuntime.toString(obj.get("info"));
			dvh.des.setText(info==null?null:Html.fromHtml(info));
			String desc=ScriptRuntime.toString(obj.get("desc"));
			dvh.description.setText(desc==null?null:Html.fromHtml(desc));
			Pussy.$(dvh.itemView.getContext()).load(ScriptRuntime.toString(obj.get("thumb"))).execute().transformer(new CropTransformer(Gravity.CENTER),new com.moe.pussy.transformer.BlurTransformer(55),new RoundTransformer(dvh.itemView.getResources().getDisplayMetrics(),5)).into((View)dvh.thumb.getParent());
			
		}else
		if(p1 instanceof PostViewholder){
			PostViewholder pvh=(PostAdapter.PostViewholder) p1;
			pvh.des.setText(ScriptRuntime.toString(obj.get("des")));
			Pussy.$(pvh.itemView.getContext()).load(ScriptRuntime.toString(obj.get("thumb"))).execute().transformer(new CropTransformer(Gravity.CENTER),new RoundTransformer(pvh.itemView.getResources().getDisplayMetrics(),5)).into(pvh.thumb);
		}else if(p1 instanceof TitleViewholder){
			TitleViewholder tvh=(PostAdapter.TitleViewholder) p1;
			tvh.summary.setVisibility(obj.get("click")==null?View.INVISIBLE:View.VISIBLE);
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		try
		{
			NativeObject obj=(NativeObject) data.get(position);
			switch(obj.get("type").toString()){
				case "title":
					return 1;
				case "post":
					return 2;
				case "detail":
					return 3;
				case "play":
					return 4;
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
		return super.getItemViewType(position);
	}
	
	@Override
	public int getItemCount()
	{
		return data.size();
	}
	
	public abstract class BaseViewholder extends RecyclerView.ViewHolder implements View.OnClickListener{
		 TextView title;
		BaseViewholder(View v){
			 super(v);
			 title=v.findViewById(R.id.title);
		}

		@Override
		public void onClick(View p1)
		{
			NativeObject obj=(NativeObject) data.get(getAdapterPosition());
			Function fun=(Function) obj.get("click");
			if(fun!=null)
			{
				Context context=Context.enter();
				fun.call(context,fun,fun,new Object[0]);
				context.exit();
			}
		}

			  
	}
	public class TitleViewholder extends BaseViewholder{
		TextView summary;
		TitleViewholder(View v){
			super(v);
			summary=v.findViewById(R.id.summary);
			summary.setOnClickListener(this);
		}
	}
	public class PostViewholder extends BaseViewholder{
		ImageView thumb;
		TextView des;
		PostViewholder(View v){
			super(v);
			thumb=v.findViewById(R.id.icon);
			des=v.findViewById(R.id.summary);
			v.setOnClickListener(this);
		}
	}
	public class DetailViewholder extends PostViewholder{
		TextView description;
		DetailViewholder(View v){
			super(v);
			description=v.findViewById(R.id.desc);
		}
	}
	public class PlayViewholder extends BaseViewholder{
		PlayViewholder(View v){
			super(v);
			title=(TextView) v;
			TypedArray ta=v.getContext().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
			title.setBackgroundDrawable(ta.getDrawable(0));
			ta.recycle();
			title.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
			title.setAllCaps(false);
			title.setOnClickListener(this);
		}
	}
}
