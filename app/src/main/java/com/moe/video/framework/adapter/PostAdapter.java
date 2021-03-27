package com.moe.video.framework.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.moe.video.framework.R;
import com.moe.video.framework.adapter.viewholder.BaseViewHolder;
import com.moe.video.framework.adapter.viewholder.DetailViewHolder;
import com.moe.video.framework.adapter.viewholder.IconViewHolder;
import com.moe.video.framework.adapter.viewholder.ImageViewHolder;
import com.moe.video.framework.adapter.viewholder.PictureViewHolder;
import com.moe.video.framework.adapter.viewholder.PlayViewHolder;
import com.moe.video.framework.adapter.viewholder.PostViewHolder;
import com.moe.video.framework.adapter.viewholder.TitleViewHolder;
import java.util.List;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import uk.co.senab.photoview.PhotoView;
import com.moe.video.framework.adapter.viewholder.ItemViewHolder;
import com.moe.neko.Neko;

public class PostAdapter<T extends BaseViewHolder> extends RecyclerView.Adapter<T> {
	private List data;
    
	public PostAdapter(List data) {
		this.data = data;
        
	}
	@Override
	public T onCreateViewHolder(ViewGroup p1, int p2) {
		switch (p2) {
			case 1://标题
				return (T)new TitleViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_title_item_view, p1, false));
			case 2://post
			case 6:
				return (T)new PostViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_item_view, p1, false), p2);
			case 3:
				return (T)new DetailViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_detail_item_view, p1, false));
			case 4:
				return (T)new PlayViewHolder(new Button(p1.getContext()));
			case 5:
				return (T)new IconViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_icon_item_view, p1, false));
            case 7:
                return (T)new ImageViewHolder(p1,new ImageView(p1.getContext()));
            case 8:
                return (T)new PictureViewHolder(new PhotoView(p1.getContext()));
            case 9:
                return (T)new ItemViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.list_item_view,p1,false));
		}
		return null;
	}

	@Override
	public void onBindViewHolder(T p1, int p2) {
		NativeObject obj=(NativeObject) data.get(p2);
        p1.onBind(obj);
	}
    
	@Override
	public int getItemViewType(int position) {
		try {
			NativeObject obj=(NativeObject) data.get(position);
			switch (ScriptRuntime.toString(obj.getOrDefault("type",""))) {
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
                    //和post一致
					return 6;
                case "image":
                    return 7;
                case "picture":
                    return 8;
                case "item":
                    return 9;
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

    @Override
    public void onViewRecycled(T holder) {
        super.onViewRecycled(holder);
        holder.recycle();
    }
    
	
    
}
