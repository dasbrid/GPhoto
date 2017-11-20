package asbridged.me.uk.gphoto.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.helper.AppConstant;
import asbridged.me.uk.gphoto.helper.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by David on 03/01/2016.
 * http://codetheory.in/android-image-slideshow-using-viewpager-pageradapter/
 */
public class SlideshowPagerAdapter extends PagerAdapter {

    private static final String TAG = "SlideshowPagerAdapter";
    private ArrayList<File> fileList = null;
    public void setFileList(ArrayList<File> fileList)
    {
        this.fileList = fileList;
    }

    Context mContext;
    LayoutInflater mLayoutInflater;

    public SlideshowPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (fileList==null)
            return 0;
        return fileList.size();
    }

    public File getFileAtPosition(int position)
    {
        return fileList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d (TAG,"instantiate " + position +" of " + fileList.size() + "=" + fileList.get(position).getAbsolutePath());

        Utils.setImageFilename(mContext, "instantiate " + position +" of " + fileList.size() + "=" + fileList.get(position).getAbsolutePath());
        View itemView = mLayoutInflater.inflate(R.layout.slideshow_page, container, false);

        ImageView iv = (ImageView) itemView.findViewById(R.id.slideshowImage);
        // get the imageview size and scale the image to fit
        Bitmap myBitmap = Utils.decodeFileToSize(new File(fileList.get(position).getAbsolutePath()), AppConstant.SLIDESHOW_WIDTH,AppConstant.SLIDESHOW_HEIGHT);

//        Bitmap myBitmap = Utils.decodeFileByScale(new File(fileList.get(position).getAbsolutePath()), 16);


        Utils.setImageFilename(mContext,Utils.getImageFilename(mContext) + " decoded bitmap");
        iv.setImageBitmap(myBitmap);
 /*
        ImageView imageView = (ImageView) itemView.findViewById(R.title.slideshowImage);
        imageView.setImageResource(mResources[position]);
*/
        container.addView(itemView);
        Utils.setImageFilename(mContext,Utils.getImageFilename(mContext) + " added view");
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d (TAG,"destroy position = "+position);
        View itemView = (View)object;
        ImageView iv = (ImageView) itemView.findViewById(R.id.slideshowImage);
        BitmapDrawable bmpDrawable = (BitmapDrawable) iv.getDrawable();
        if (bmpDrawable != null && bmpDrawable.getBitmap() != null) {
            // This is the important part
            Log.d (TAG,"recycling position = "+position);
            bmpDrawable.getBitmap().recycle();
        }
        ((ViewPager) container).removeView(itemView);

        //container.removeView((LinearLayout) object);
    }
}
