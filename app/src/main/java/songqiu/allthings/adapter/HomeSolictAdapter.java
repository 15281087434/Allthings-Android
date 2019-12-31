package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.auth.adapter.BaseViewHolder;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.bean.HomeSolictBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.videodetail.VideoDetailActivity;

/**
 * create by: ADMIN
 * time:2019/12/3017:29
 * e_mail:734090232@qq.com
 * description:
 */
public class HomeSolictAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    Context context;
    List<HomeSolictBean> item;
    private TpCallBack mCallBack;

    public void setmCallBack(TpCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public void setBannerBeans(List<BannerBean> bannerBeans) {
        this.bannerBeans = bannerBeans;
        notifyItemChanged(0);
    }

    List<BannerBean> bannerBeans;
    public HomeSolictAdapter(Context context, List<HomeSolictBean> item) {
        this.context = context;
        this.item = item;

    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0){
            return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_solicit_banner,parent,false));
        }
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_solicit,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(position==0){
            SimpleDraweeView view  = (SimpleDraweeView) holder.getView(R.id.banner);

            view.setScaleType(ImageView.ScaleType.CENTER);
            if(bannerBeans==null||bannerBeans.size()<=0){
                view.setBackgroundResource(R.mipmap.item_home_tab_big_pic);
            }else{
                if(!StringUtil.isEmpty(bannerBeans.get(position).photo)) {
                    if(!bannerBeans.get(position).photo.contains("http")) {
                        bannerBeans.get(position).photo = HttpServicePath.BasePicUrl+bannerBeans.get(position).photo;
                    }
                }
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(bannerBeans.get(position).photo)
                        .setOldController(view.getController())
                        .setTapToRetryEnabled(true).build();
                view.setController(controller);
            }
            view.setScaleType(ImageView.ScaleType.CENTER);

        }else {
            HomeSolictBean bean=item.get(position-1);
            holder.getTextView(R.id.tv_title).setText(bean.getTitle());
            holder.getTextView(R.id.tv_contents).setText(bean.getDescriptions());
            holder.getTextView(R.id.tv_tickets).setText(bean.getSupport_num()+"");
            if(position<4){
                holder.getTextView(R.id.tv_rank).setText(position+"");
                holder.getTextView(R.id.tv_rank).setVisibility(View.VISIBLE);
            }else {
                holder.getTextView(R.id.tv_rank).setVisibility(View.GONE);
            }
            holder.getView(R.id.tv_tp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if(mCallBack!=null){
                            mCallBack.onTp(position-1);
                        }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String articleid = item.get(position).getArticleid();
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", articleid);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return item.size()+1;
    }

    public interface TpCallBack{
        void onTp(int position);
    }
}
