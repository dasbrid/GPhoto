package asbridged.me.uk.gphoto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import asbridged.me.uk.gphoto.classes.Album;


import java.util.ArrayList;

/**
 * Created by David on 05/12/2015.
 */
public class AlbumListAdapter extends BaseAdapter {

    private ArrayList<Album> albums;
    private LayoutInflater inflater;

    // Constructor
    public AlbumListAdapter(Context c, ArrayList<Album> theAlbums){
        albums=theAlbums;
        inflater =LayoutInflater.from(c);
    }


    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int index) {
        // TODO Auto-generated method stub
        return albums.get(index);
    }

    public Album getAlbum(int position) {
        return albums.get(position);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get album using position
        Album currAlbum = albums.get(position);

        // using standard android layout, but could copy anc customise, but must be a checked text view...
        CheckedTextView layout = (CheckedTextView) inflater.inflate(android.R.layout.simple_list_item_multiple_choice/*R.layout.bucket_in_list_android*/, parent, false);
        TextView tvBucketName = (TextView)layout.findViewById(android.R.id.text1);
        tvBucketName.setText(currAlbum.getName());

        //set position as tag
        layout.setTag(position);
        return layout;
    }

}
