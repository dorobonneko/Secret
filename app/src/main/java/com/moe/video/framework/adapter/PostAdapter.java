package com.moe.video.framework.adapter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.moe.neko.Neko;
import com.moe.neko.transform.BlurTransform;
import com.moe.neko.transform.RoundTransform;
import com.moe.pussy.widget.PercentImageView;
import com.moe.video.framework.R;
import java.util.List;
import java.util.Random;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import android.graphics.Rect;

public class PostAdapter<T extends PostAdapter.BaseViewholder> extends RecyclerView.Adapter<T> {
	private List data;
    private RecyclerView mRecyclerView;
	public PostAdapter(List data,RecyclerView mRecyclerView) {
		this.data = data;
        this.mRecyclerView=mRecyclerView;
	}
	@Override
	public T onCreateViewHolder(ViewGroup p1, int p2) {
		switch (p2) {
			case 1://标题
				return (T)new TitleViewholder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_title_item_view, p1, false));
			case 2://post
			case 6:
				return (T)new PostViewholder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_item_view, p1, false), p2);
			case 3:
				return (T)new DetailViewholder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_detail_item_view, p1, false));
			case 4:
				return (T)new PlayViewholder(new Button(p1.getContext()));
			case 5:
				return (T)new IconViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_icon_item_view, p1, false));
            case 7:
                return (T)new ImageViewHolder(new ImageView(p1.getContext()));
		}
		return null;
	}

	@Override
	public void onBindViewHolder(T p1, int p2) {
		NativeObject obj=(NativeObject) data.get(p2);
        if(p1.title!=null)
		p1.title.setText(obj.get("title").toString());
		if (p1 instanceof DetailViewholder) {
			DetailViewholder dvh=(PostAdapter<T>.DetailViewholder) p1;
			Neko.with(dvh.thumb).load(ScriptRuntime.toString(obj.get(("thumb")))).asBitmap().scaleType(ScaleType.CENTER_CROP).fade(500).transform(new RoundTransform(5)).into(dvh.thumb);
            //Pussy.$(dvh.itemView.getContext()).load(ScriptRuntime.toString(obj.get("thumb"))).execute().anime(Anim.fade(350)).transformer(new CropTransformer(Gravity.CENTER),new RoundTransformer(dvh.itemView.getResources().getDisplayMetrics(),5)).into(dvh.thumb);
			String info=ScriptRuntime.toString(obj.get("info"));
			dvh.des.setText(info == null ?null: Html.fromHtml(info));
			String desc=ScriptRuntime.toString(obj.get("desc"));
			dvh.description.setText(desc == null ?null: Html.fromHtml(desc));
            Neko.with((View)dvh.thumb.getParent()).load(ScriptRuntime.toString(obj.get("thumb"))).asBitmap().scaleType(ScaleType.CENTER_CROP).fade(300, 200).transform(new RoundTransform(5), new BlurTransform(15)).into((View)dvh.thumb.getParent());
			//Pussy.$(dvh.itemView.getContext()).load(ScriptRuntime.toString(obj.get("thumb"))).execute().anime(Anim.fade(500)).transformer(new CropTransformer(Gravity.CENTER),new com.moe.pussy.transformer.BlurTransformer(55),new RoundTransformer(dvh.itemView.getResources().getDisplayMetrics(),5)).into((View)dvh.thumb.getParent());

		} else if (p1 instanceof PostViewholder) {
			PostViewholder pvh=(PostAdapter.PostViewholder) p1;
			Object des=obj.get("des");
			if (des == null)
				des = "";
			pvh.des.setText(des.toString());
            Neko.with(pvh.thumb).load(ScriptRuntime.toString(obj.get("thumb"))).asBitmap().fade(300).error(new ColorDrawable(0xff000000)).scaleType(ScaleType.CENTER_CROP).transform(new RoundTransform(5)).into(pvh.thumb);
			//Pussy.$(pvh.itemView.getContext()).load(ScriptRuntime.toString(obj.get("thumb"))).execute().delay(150).anime(Anim.fade(150)).transformer(new CropTransformer(Gravity.CENTER),new RoundTransformer(pvh.itemView.getResources().getDisplayMetrics(),5)).into(pvh.thumb);
		} else if (p1 instanceof TitleViewholder) {
			TitleViewholder tvh=(PostAdapter.TitleViewholder) p1;
			tvh.summary.setVisibility(obj.get("click") == null ?View.INVISIBLE: View.VISIBLE);
		} else if (p1 instanceof IconViewHolder) {
			IconViewHolder ivh=(PostAdapter<T>.IconViewHolder) p1;
            Neko.with(ivh.icon).load(ScriptRuntime.toString(obj.get("thumb"))).fade(150).circleCrop().into(ivh.icon);
			//Pussy.$(ivh.itemView.getContext()).load(ScriptRuntime.toString(obj.get("thumb"))).execute().transformer(new CircleTransFormation(0)).into(ivh.icon);
		}else if(p1 instanceof ImageViewHolder){
            ImageViewHolder ivh=(PostAdapter<T>.ImageViewHolder) p1;
           // Neko.with(ivh.view).load(ScriptRuntime.toString(obj.get("thumb"))).override(ScriptRuntime.toInt32(obj.getOrDefault("width",-1)),ScriptRuntime.toInt32(obj.getOrDefault("height",-1))).fade(150).into(ivh.view);
           // Neko.with(ivh.view).load("https://ddd.ee").fade(150).error(new ColorDrawable(randomColor())).into(ivh.view).reSize(ScriptRuntime.toInt32(obj.getOrDefault("width",-1)),ScriptRuntime.toInt32(obj.getOrDefault("height",-1)));
           int width=ScriptRuntime.toInt32(obj.getOrDefault("width",-1));
           int height=ScriptRuntime.toInt32(obj.getOrDefault("height",-1));
           
           int viewWidth=mRecyclerView.getWidth()/2;
           int viewHeight=(int)Math.floor(viewWidth/(float)width*height);
           ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(viewWidth,viewHeight);
           ivh.view.setLayoutParams(params);
           
           Neko.with(ivh.view).load(ScriptRuntime.toString(obj.get("thumb"))).fade(150).placeHolder(new ColorDrawable(randomColor())).into(ivh.view);
        }
	}
    public static int randomColor() {
        Random random = new Random();
        int r = random.nextInt(100)+100;
        int g = random.nextInt(100)+100;
        int b = random.nextInt(100)+100;
        return Color.argb(0x8a,r, g, b);
    }
	@Override
	public int getItemViewType(int position) {
		try {
			NativeObject obj=(NativeObject) data.get(position);
			switch (obj.get("type").toString()) {
				case "title":
					return 1;
				case "post":
					return 2;
				case "detail":
					return 3;
				case "play":
					return 4;
				case "icon":
					return 5;
				case "thumb":
					return 6;
                case "image":
                    return 7;
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return super.getItemViewType(position);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public abstract class BaseViewholder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        TextView title;
		BaseViewholder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
		}

		@Override
		public void onClick(View p1) {
			NativeObject obj=(NativeObject) data.get(getAdapterPosition());
			Function fun=(Function) obj.get("click");
			if (fun != null) {
				Context context=Context.enter();
				fun.call(context, fun, fun, new Object[]{obj});
				context.exit();
			}
		}

		@Override
		public boolean onLongClick(View p1) {
			NativeObject obj=(NativeObject) data.get(getAdapterPosition());
			Function fun=(Function) obj.get("longClick");
			if (fun != null) {
				Context context=Context.enter();
				fun.call(context, fun, fun, new Object[]{obj});
				context.exit();
			}
			return true;
		}



	}
	public class TitleViewholder extends BaseViewholder {
		TextView summary;
		TitleViewholder(View v) {
			super(v);
			summary = v.findViewById(R.id.summary);
			summary.setOnClickListener(this);
		}
	}
	public class IconViewHolder extends BaseViewholder {
		ImageView icon;
		IconViewHolder(View v) {
			super(v);
			icon = v.findViewById(R.id.icon);
			v.setOnClickListener(this);
		}
	}
	public class PostViewholder extends BaseViewholder {
		ImageView thumb;
		TextView des;
		PostViewholder(View v, int type) {
			super(v);
			thumb = v.findViewById(R.id.icon);
			des = v.findViewById(R.id.summary);
			v.setOnClickListener(this);
			v.setOnLongClickListener(this);
			if (type == 6)
				((PercentImageView)thumb).setPercent(0.618f);
		}
	}
	public class DetailViewholder extends PostViewholder {
		TextView description;
		DetailViewholder(View v) {
			super(v, 3);
			description = v.findViewById(R.id.desc);
		}
	}
	public class PlayViewholder extends BaseViewholder {
		PlayViewholder(View v) {
			super(v);
			title = (TextView) v;
			TypedArray ta=v.getContext().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
			title.setBackgroundDrawable(ta.getDrawable(0));
			ta.recycle();
			title.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
			title.setAllCaps(false);
			title.setOnClickListener(this);
		}
	}
    public class ImageViewHolder extends BaseViewholder {
        ImageView view;
        ImageViewHolder(View view) {
            super(view);
            TypedArray ta=view.getContext().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
            view.setForeground(ta.getDrawable(0));
            ta.recycle();
            this.view = (ImageView) view;
            this.view.setScaleType(ScaleType.CENTER_CROP);
            //view.setPadding(10,10,10,10);
            view.setOnClickListener(this);
        }
    }
}
