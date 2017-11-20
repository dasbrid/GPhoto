package asbridged.me.uk.gphoto.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.classes.CheckedFile;
import asbridged.me.uk.gphoto.classes.ImageDownloader;
import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.controls.CheckableLinearLayout;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by David on 10/11/2015.
 */
public class MultiCheckablePhotoGridAdapter extends BaseAdapter  {

    private final static String TAG = "PhotoGridAdapter";

    static class ViewHolder {
        ImageView image;
        CheckableLinearLayout layout;
    }

    private Activity _context;
    private ArrayList<File> _files;

    public MultiCheckablePhotoGridAdapter(Activity activity, ArrayList<File> imageFiles) {
        this._context = activity;
        this._files = imageFiles;
    }

    @Override
    public int getCount() {
        if (_files == null) return 0;
        return this._files.size();
    }

    @Override
    public Object getItem(int position) {
        return this._files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private final ImageDownloader imageDownloader = new ImageDownloader();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            Log.d(TAG, "getView " + position + " convertView == null");
            convertView = inflater.inflate(R.layout.checkable_photo_grid_item, null);
            holder = new ViewHolder();
            holder.layout = (CheckableLinearLayout) convertView.findViewById(R.id.layout);
            holder.image = (ImageView) convertView.findViewById(R.id.photo_grid_item_image);
            convertView.setTag(holder);
        } else {
            Log.d(TAG, "getView " + position + " convertView != null");
            holder = (ViewHolder) convertView.getTag();
        }
        // http://android-developers.blogspot.co.uk/2010/07/multithreading-for-performance.html
        imageDownloader.download(_files.get(position).getAbsolutePath(), (ImageView) holder.image);

        return convertView;
    }
}
