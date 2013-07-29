package org.spin.mhive;

import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.mhive.R;

public class SettingsDialog extends DialogFragment
{	
	NumberPicker minFrequencyPicker;
	NumberPicker maxFrequencyPicker;
	
	MainActivity parent;
	
	public SettingsDialog()
	{
		//do nothing
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.settings_dialog_layout, container);
		
		minFrequencyPicker = (NumberPicker)view.findViewById(R.id.nmbrMinimumFrequency);
		maxFrequencyPicker = (NumberPicker)view.findViewById(R.id.nmbrMaximumFrequency);
		
		parent = (MainActivity)getActivity();
		
		Resources res = getResources();
		minFrequencyPicker.setMinValue(res.getInteger(R.integer.MinAllowedFrequencyInHz));
		minFrequencyPicker.setMaxValue(res.getInteger(R.integer.MaxAllowedFrequencyInHz));

		maxFrequencyPicker.setMinValue(res.getInteger(R.integer.MinAllowedFrequencyInHz));
		maxFrequencyPicker.setMaxValue(res.getInteger(R.integer.MaxAllowedFrequencyInHz));

		minFrequencyPicker.setValue(parent.getMinFreq());
		maxFrequencyPicker.setValue(parent.getMaxFreq());
		
		return view;
	}
	
}

