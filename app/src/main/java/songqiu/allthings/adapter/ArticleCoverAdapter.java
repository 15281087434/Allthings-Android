package songqiu.allthings.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;

import songqiu.allthings.R;
import songqiu.allthings.util.AlbumUtil;
import songqiu.allthings.util.LogUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/30
 *
 *类描述：
 *
 ********/

public class ArticleCoverAdapter extends BaseAdapter {
    private LinkedList<String> gvList;
    private Context mContext;
    private int maxNum;
    public ArticleCoverAdapter(Context mContext, LinkedList<String> gvList, int maxNum) {
        this.gvList = gvList;
        this.mContext = mContext;
        this.maxNum = maxNum;
    }

    @Override
    public int getCount() {
        return gvList.size()>maxNum?maxNum:gvList.size();
    }

    @Override
    public Object getItem(int position) {
        return gvList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  interface ShowDialogListener{
        void addImage();
        void deleImage(int position);
    }
    ShowDialogListener showDialogListener;
    public void setOnShowDialogListener(ShowDialogListener showDialogListener){
        this.showDialogListener = showDialogListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        GvVH viewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_article_cover,null);
            viewHolder = new GvVH();
            viewHolder.img = convertView.findViewById(R.id.item_img_album);
            viewHolder.removeImg = convertView.findViewById(R.id.removeImg);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (GvVH) convertView.getTag();
        }
        if(position == gvList.size()-1){
            viewHolder.removeImg.setVisibility(View.GONE);
            if(gvList.size()>maxNum) {
                viewHolder.img.setVisibility(View.GONE);
            }else {
                viewHolder.img.setImageResource(R.mipmap.item_setting_add_img);
                viewHolder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if(gvList.size()>3){
//                            ToastUtil.shortshow(mContext,"您最多添加三张图片！");
//                            return;
//                        }
                        if(showDialogListener !=null){
                            showDialogListener.addImage();
                        }
                    }
                });
            }
        }else{
            viewHolder.removeImg.setVisibility(View.VISIBLE);
            setPic(viewHolder.img,gvList.get(position));
            viewHolder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent in = new Intent(mContext, AmplificationPicActivity.class);
//                    in.putExtra("path",gvList.get(position));
//                    mContext.startActivity(in);
                }
            });

            viewHolder.removeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(showDialogListener !=null){
                        showDialogListener.deleImage(position);
                    }
                }
            });
        }

        return convertView;
    }
    class GvVH{
        ImageView img;
        ImageView removeImg;
    }



    private void setPic(ImageView mImageView, String mPath) {

	/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = (int) mImageView.getContext().getResources().getDimension(R.dimen.x_ui_px_160_0);
        int targetH = (int) mImageView.getContext().getResources().getDimension(R.dimen.x_ui_px_160_0);

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */

        Bitmap bitmap = BitmapFactory.decodeFile(mPath, bmOptions);

        if(null != bitmap) {
            /* Associate the Bitmap to the ImageView */
            mImageView.setImageBitmap(AlbumUtil.fitBitmap(bitmap,targetW,targetH));
        }else {
            Glide.with(mContext).load(mPath).into(mImageView);
        }
    }
}
