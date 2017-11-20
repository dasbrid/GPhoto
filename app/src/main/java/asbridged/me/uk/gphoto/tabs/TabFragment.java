package asbridged.me.uk.gphoto.tabs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by AsbridgeD on 03/12/2015.
 */
public abstract class TabFragment extends Fragment {

    public abstract void doSlideshow();
    public abstract void viewAlbum();

}