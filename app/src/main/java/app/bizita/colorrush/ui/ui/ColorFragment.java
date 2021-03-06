package app.bizita.colorrush.ui.ui;

import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.adefruandta.spinningwheel.SpinningWheelView;

import app.bizita.colorrush.R;

public class ColorFragment extends Fragment {
    private SpinningWheelView wheelView;

    View targetView;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_color, container, false);

        return rootView;
    }

    private void setUp() {

    }


}