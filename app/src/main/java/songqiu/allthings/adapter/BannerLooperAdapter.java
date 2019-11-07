package songqiu.allthings.adapter;
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

import songqiu.allthings.R;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.view.banner.LoopPagerAdapter;
import songqiu.allthings.view.banner.RollPagerView;

/**
 * ***********************************************
 * 包路径：
 * 类描述：
 * 创建人：杨延辉[PHONE：15281087434]
 * 创建时间：2019/5/14+14:18
 * 修改人：
 * 修改时间：2019/5/14+14:18
 * 修改备注：
 * ***********************************************
 */
public class BannerLooperAdapter extends LoopPagerAdapter {

    ArrayList<BannerBean> bannerBeans;
    public BannerLooperAdapter(RollPagerView viewPager, ArrayList<BannerBean> bannerBeans) {
        super(viewPager);
        this.bannerBeans = bannerBeans;
    }

    @Override
    public View getView(ViewGroup container, final int position) {
        View contentView  = LayoutInflater.from(container.getContext()).inflate(R.layout.banner_rv_item,null);
        SimpleDraweeView view  = contentView.findViewById(R.id.sdv_banner);
        LinearLayout titleLayout = contentView.findViewById(R.id.titleLayout);
        TextView titleTv = contentView.findViewById(R.id.titleTv);
        view.setScaleType(ImageView.ScaleType.CENTER);
        if(bannerBeans==null||bannerBeans.size()<=0){
            view.setBackgroundResource(R.mipmap.item_home_tab_big_pic);
        }else{
            if(!StringUtil.isEmpty(bannerBeans.get(position).photo)) {
                if(!bannerBeans.get(position).photo.contains("http")) {
                    bannerBeans.get(position).photo = HttpServicePath.BasePicUrl+bannerBeans.get(position).photo;
                }
            }
//            view.setImageURI(bannerBeans.get(position).photo);
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(bannerBeans.get(position).photo)
                    .setOldController(view.getController())
                    .setTapToRetryEnabled(true).build();
            view.setController(controller);
        }
        if(StringUtil.isEmpty(bannerBeans.get(position).title)) {
            titleLayout.setVisibility(View.GONE);
        }else {
            titleTv.setText(bannerBeans.get(position).title);
            titleLayout.setVisibility(View.VISIBLE);
        }
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        SimpleDraweeView view = new SimpleDraweeView(container.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER);

//设置圆角
//        RoundingParams roundingParams = RoundingParams.fromCornersRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,container.getContext().getResources().getDisplayMetrics()));
//        // alternatively use fromCornersRadii or asCircle
//        GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(container.getContext().getResources()).build();
//
//        hierarchy.setRoundingParams(roundingParams);
//        view.setHierarchy(hierarchy);

//        if(bannerBeans==null||bannerBeans.size()<=0){
//            view.setBackgroundResource(R.mipmap.item_home_tab_big_pic);
//        }else{
//            if(!StringUtil.isEmpty(bannerBeans.get(position).photo)) {
//                if(!bannerBeans.get(position).photo.contains("http")) {
//                    bannerBeans.get(position).photo = HttpServicePath.BasePicUrl+bannerBeans.get(position).photo;
//                }
//            }
//            DraweeController controller = Fresco.newDraweeControllerBuilder()
//                    .setUri(bannerBeans.get(position).photo)
//                    .setOldController(view.getController())
//                    .setTapToRetryEnabled(true).build();
//            view.setController(controller);
////            view.setBackgroundResource(bannerBeans.get(position).src);
//        }

//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return contentView;
    }

    @Override
    public int getRealCount() {
        return bannerBeans == null ? 0 : bannerBeans.size();
    }


}

