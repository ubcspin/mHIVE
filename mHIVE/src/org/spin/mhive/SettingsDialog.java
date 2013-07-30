package org.spin.mhive;

import org.spin.mhive.MainActivity.ScalingMode;

import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RadioButton;

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
		
		
		//FREQUENCY MAX/MIN
		minFrequencyPicker = (NumberPicker)view.findViewById(R.id.nmbrMinimumFrequency);
		maxFrequencyPicker = (NumberPicker)view.findViewById(R.id.nmbrMaximumFrequency);
		
		parent = (MainActivity)getActivity();
		
		minFrequencyPicker.setOnValueChangedListener(new OnValueChangeListener() {
							@Override
							public void onValueChange(NumberPicker picker, int oldVal,
									int newVal) {
								parent.setMinFreq(newVal);
								UpdateFrequencyValues();
							}});
		
		maxFrequencyPicker.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				parent.setMaxFreq(newVal);
				UpdateFrequencyValues();
			}});
		
		UpdateFrequencyValues();

		//FREQUENCY MODE
		((RadioButton)view.findViewById(R.id.radFrequencyLog)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				parent.SetFrequency(ScalingMode.LOG);
			}});
		((RadioButton)view.findViewById(R.id.radFrequencyLinear)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				parent.SetFrequency(ScalingMode.LINEAR);
			}});
		((RadioButton)view.findViewById(R.id.radFrequencyExp)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				parent.SetFrequency(ScalingMode.EXP);
			}});
		
		return view;
	}
	
	private void UpdateFrequencyValues()
	{
		Resources res = getResources();
		minFrequencyPicker.setMinValue(res.getInteger(R.integer.MinAllowedFrequencyInHz));
		minFrequencyPicker.setMaxValue(res.getInteger(R.integer.MaxAllowedFrequencyInHz));

		maxFrequencyPicker.setMinValue(res.getInteger(R.integer.MinAllowedFrequencyInHz));
		maxFrequencyPicker.setMaxValue(res.getInteger(R.integer.MaxAllowedFrequencyInHz));

		minFrequencyPicker.setValue(parent.getMinFreq());
		maxFrequencyPicker.setValue(parent.getMaxFreq());
	}
	
}

