package ke.co.mobiticket.mobiticket.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.mobiticket.mobiticket.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriveVehiclesFragment extends Fragment {

    public DriveVehiclesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drive_vehicles, container, false);
    }
}
