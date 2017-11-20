package asbridged.me.uk.gphoto.classes;

import android.app.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import asbridged.me.uk.gphoto.R;

/**
 * Created by David on 30/11/2015.
 */
public class SlideshowSpeedDialog extends DialogFragment {

    private NumberPicker numberPicker;
    public interface SlideshowSpeedChangedListener {
        void slideshowSpeedChanged(int x);
    }

    private void btnOKClicked() {
        SlideshowSpeedChangedListener activity = (SlideshowSpeedChangedListener) getActivity();
        int value = numberPicker.getValue();
        activity.slideshowSpeedChanged(value);
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_slideshow_speed, container, false);

        int currentValue = getArguments().getInt("currentValue");

        getDialog().setTitle("Slide show speed");

        Button btnCancel = (Button) rootView.findViewById(R.id.btnSlideshowSpeedDialogCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnOK = (Button) rootView.findViewById(R.id.btnSlideshowSpeedDialogOK);
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnOKClicked();
            }
        });

        numberPicker = (NumberPicker) rootView.findViewById(R.id.numberpickerSlideshowSpeed);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(2);
        numberPicker.setValue(currentValue);
        return rootView;
    }

}
