package com.ncq.ubi;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IfomacionVehiculos extends Fragment {

	public IfomacionVehiculos()
	{}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.informacion_vehiculos, container, false);

		return rootView;
	}
}
