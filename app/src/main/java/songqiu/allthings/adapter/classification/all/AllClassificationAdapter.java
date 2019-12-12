package songqiu.allthings.adapter.classification.all;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.adapter.comment.SectionedRecyclerViewAdapter;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.bean.AllClassificationBean;
import songqiu.allthings.bean.ClassificationSubitemBean;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.ToastUtil;


/**
 */

public class AllClassificationAdapter extends SectionedRecyclerViewAdapter<AllClassificationAdapter.ClassificationHolder, AllClassificationAdapter.ClassificationSubitemHolder, RecyclerView.ViewHolder> {

    public List<AllClassificationBean> item;
    private Context mContext;
    private LayoutInflater mInflater;
    private SparseBooleanArray mBooleanMap;//记录下哪个section是被打开的

    ClassificationItemListener classificationItemListener;
    //选择类的数量
    int selectNum;

    public AllClassificationAdapter(Context context, List<AllClassificationBean> item) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mBooleanMap = new SparseBooleanArray();
        this.item = item;
        for (int i = 0; i < item.size(); i++) {
            mBooleanMap.put(i, true);
        }
    }

    /**
     * 一共有多少个section需要展示， 返回值是我们最外称list的大小，在我们的示例图中，
     * 对应的为热门品牌—商业区—热门景点 等，对应的数据是我们的allTagList
     *
     * @Override
     */

    protected int getSectionCount() {
        return item.size();
    }

    /**
     * 来展示content内容区域，返回值是我们需要展示多少内容，在本例中，我们超过8条数据只展示8条，
     * 点击展开后就会展示全部数据，逻辑就在这里控制。 对应数据结构为tagInfoList
     *
     * @param section
     * @return
     */
    @Override
    protected int getItemCountForSection(int section) {
        if (null != item.get(section)) {
            if (null != item.get(section).row1) {
                return item.get(section).row1.size();
            }
            return 0;
        }
        return 0;
    }


    //是否有footer布局

    /**
     * 判断是否需要底部footer布局，在该例中，我们并不需要显示footer，所以默认返回false就可以，
     * 如果你对应的section需要展示footer布局，那么就在对应的section返回true就行了
     *
     * @param section
     * @return
     */
    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }


    @Override
    protected ClassificationHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        return new ClassificationHolder(mInflater.inflate(R.layout.item_classification_title, parent, false));
    }


    @Override
    protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    protected ClassificationSubitemHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ClassificationSubitemHolder(mInflater.inflate(R.layout.item_classification_subitem, parent, false));
    }


    @Override
    protected void onBindSectionHeaderViewHolder(ClassificationHolder holder, final int position) {
        AllClassificationBean allClassificationBean = item.get(position);
        if (0 == position) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }
        holder.titleTv.setText(allClassificationBean.name);
    }


    @Override
    protected void onBindSectionFooterViewHolder(RecyclerView.ViewHolder holder, int section) {

    }

    /**
     * 这里有一个section和position ，有些人可能会弄混
     * section是区域，也就是我们最外层的index，position是每个section对应的内容数据的position
     *
     * @param holder
     * @param section
     * @param position
     */
    @Override
    protected void onBindItemViewHolder(ClassificationSubitemHolder holder, final int section, final int position) {
        ClassificationSubitemBean classificationSubitemBean = item.get(section).row1.get(position);
        holder.classificationTv.setText(classificationSubitemBean.name);
        if(classificationSubitemBean.isClick) { //选择后设为选择背景
            holder.classificationTv.setBackgroundResource(R.drawable.classification_item_click);
            holder.classificationTv.setTextColor(mContext.getResources().getColor(R.color.normal_color));
        }else {
            if(item.get(0).name.equals("我最近访问的分类")) {
                switch (section) {
                    case 0:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item0_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(R.color.bottom_tab_tv));
                        break;
                    case 1:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item1_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(1)));
                        break;
                    case 2:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item2_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(2)));
                        break;
                    case 3:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item3_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(3)));
                        break;
                    case 4:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item4_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(4)));
                        break;
                    case 5:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item5_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(5)));
                        break;
                    case 6:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item6_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(6)));
                        break;
                    case 7:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item7_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(7)));
                        break;
                }

            }else {
                switch (section) {
                    case 0:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item1_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(1)));
                        break;
                    case 1:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item2_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(2)));
                        break;
                    case 2:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item3_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(3)));
                        break;
                    case 3:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item4_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(4)));
                        break;
                    case 4:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item5_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(5)));
                        break;
                    case 5:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item6_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(6)));
                        break;
                    case 6:
                        holder.classificationTv.setBackgroundResource(R.drawable.classification_item7_normal);
                        holder.classificationTv.setTextColor(mContext.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(7)));
                        break;
                }
            }
        }
        holder.classificationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classificationSubitemBean.isClick) { //取消选择
                    selectNum = selectNum-1;
                    classificationItemListener.isSelect(classificationSubitemBean,false,selectNum);
                }else { //选择
                    selectNum = selectNum+1;
                    if(selectNum>5) {
                        selectNum = 5;
                        ToastUtil.showToast(mContext,"最多选择5个分类");
                        return;
                    }
                    classificationItemListener.isSelect(classificationSubitemBean,true,selectNum);
                }
            }
        });

    }


    public class ClassificationHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.titleTv)
        TextView titleTv;

        public ClassificationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ClassificationSubitemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.classificationTv)
        TextView classificationTv;
        public ClassificationSubitemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void setClassificationItemListener(ClassificationItemListener classificationItemListener) {
        this.classificationItemListener = classificationItemListener;
    }
    public interface ClassificationItemListener {
        void isSelect(ClassificationSubitemBean classificationSubitemBean,boolean isSelect,int selectNum);
    }

}
