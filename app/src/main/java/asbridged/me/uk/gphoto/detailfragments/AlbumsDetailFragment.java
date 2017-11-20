package asbridged.me.uk.gphoto.detailfragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import asbridged.me.uk.gphoto.R;
import asbridged.me.uk.gphoto.activities.GetPermissionsActivity;
import asbridged.me.uk.gphoto.activities.MultiCheckablePhotoGridActivity;
import asbridged.me.uk.gphoto.activities.SlideshowActivity;
import asbridged.me.uk.gphoto.adapter.AlbumListAdapter;
import asbridged.me.uk.gphoto.classes.Album;
import asbridged.me.uk.gphoto.helper.LogHelper;
import asbridged.me.uk.gphoto.helper.Utils;

/**
 * Created by AsbridgeD on 15-Nov-17.
 */

public class AlbumsDetailFragment extends OptionDynamicDetailFragment {

    private static final String TAG = LogHelper.makeLogTag(AlbumsDetailFragment.class);

    private ArrayList<Album> albumList;
    ListView lvAlbumList;
    AlbumListAdapter albumAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_albums, container, false);

        lvAlbumList = (ListView)v.findViewById(R.id.lvBucketList);

        lvAlbumList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // We do not have necessary permission. Must ask the user
                LogHelper.i(TAG, "Permission not granted");
                // No explanation needed, we can request the permission.
                startActivity(new Intent(getActivity(), GetPermissionsActivity.class));
            } else {
                albumList = Utils.getAlbumsFromMedia(getContext());
                albumAdapter = new AlbumListAdapter(getContext(), albumList);
                lvAlbumList.setAdapter(albumAdapter);
            }
        }

        LogHelper.i(TAG, "setting up and returning view");
        Button button = (Button) v.findViewById(R.id.btnShowSlideshow);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.btnShowPictures);
        button.setOnClickListener(this);

        return v;
    }

    public void doSlideshow() {
        // start the slideshow activity for the selected album
        SparseBooleanArray checked = lvAlbumList.getCheckedItemPositions();
        String albumNames = "";
        ArrayList<Album> selectedItems = new ArrayList<Album>();
        ArrayList<String> selectedBucketIDs = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                Album selectedItem = albumAdapter.getAlbum(position);
                if (!albumNames.isEmpty())
                    albumNames = albumNames + ", ";
                albumNames = albumNames + selectedItem.getName();
                selectedItems.add(selectedItem);
                selectedBucketIDs.add(Long.toString(selectedItem.getBucketID()));
            }
        }

        if (selectedItems.size() > 0 ) {
            Intent intent = new Intent(getActivity(), SlideshowActivity.class);
            intent.putExtra("albumType", "multipleBuckets");
            intent.putExtra("albumName", albumNames);
            intent.putExtra("position", -1);
            intent.putStringArrayListExtra("bucketIDs", selectedBucketIDs);
            this.startActivity(intent);
        }
    }


    public void viewAlbum() {
        SparseBooleanArray checked = lvAlbumList.getCheckedItemPositions();
        String albumNames = "";
        ArrayList<Album> selectedItems = new ArrayList<Album>();
        ArrayList<String> selectedBucketIDs = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                Album selectedItem = albumAdapter.getAlbum(position);
                if (!albumNames.isEmpty())
                    albumNames = albumNames + ", ";
                albumNames = albumNames + selectedItem.getName();
                selectedItems.add(selectedItem);
                selectedBucketIDs.add(Long.toString(selectedItem.getBucketID()));
            }
        }

        if (selectedItems.size() > 0 ) {
            Intent intent;

            intent = new Intent(getActivity(), MultiCheckablePhotoGridActivity.class);

            intent.putExtra("albumType", "multipleBuckets");
            intent.putExtra("albumName", albumNames);
            intent.putStringArrayListExtra("bucketIDs", selectedBucketIDs);
            this.startActivity(intent);
        }
    }

}
