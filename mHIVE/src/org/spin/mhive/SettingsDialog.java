package org.spin.mhive;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mhive.R;

public class SettingsDialog extends DialogFragment
{	
	
	public SettingsDialog()
	{
		//do nothing
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.settings_dialog_layout, container);
		
		return view;
	}
	
}

