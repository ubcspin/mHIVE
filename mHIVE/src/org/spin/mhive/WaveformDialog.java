package org.spin.mhive;

import com.example.mhive.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WaveformDialog extends DialogFragment
{
	public WaveformDialog()
	{
//        setContentView(R.layout.activity_waveform_selection);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.activity_waveform_selection, container);
		return view;
	}

}
