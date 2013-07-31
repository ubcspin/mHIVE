package org.spin.mhive;

import java.util.LinkedList;
import java.util.List;

import org.spin.mhive.MainActivity.ScalingMode;

import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.mhive.R;

public class SettingsDialog extends DialogFragment
{	
	Spinner minFrequencyPicker;
	Spinner maxFrequencyPicker;
	
	List<Integer> freqValues;
	
	ArrayAdapter<Integer> minAdapter;
	ArrayAdapter<Integer> maxAdapter;

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
		minFrequencyPicker = (Spinner)view.findViewById(R.id.nmbrMinimumFrequency);
		maxFrequencyPicker = (Spinner)view.findViewById(R.id.nmbrMaximumFrequency);
		Resources res = getResources();
		int minFreq = res.getInteger(R.integer.MinAllowedFrequencyInHz);
		int maxFreq = res.getInteger(R.integer.MaxAllowedFrequencyInHz);
		freqValues = new LinkedList<Integer>();
		for (int i = minFreq; i <= maxFreq; i+=5)
		{
			freqValues.add(i);
		}
		minAdapter = new ArrayAdapter<Integer>(view.getContext(), android.R.layout.simple_spinner_item);
		minAdapter.addAll(freqValues);
		maxAdapter = new ArrayAdapter<Integer>(view.getContext(), android.R.layout.simple_spinner_item);
		maxAdapter.addAll(freqValues);
		minFrequencyPicker.setAdapter(minAdapter);
		maxFrequencyPicker.setAdapter(maxAdapter);
		minAdapter.notifyDataSetChanged();
		maxAdapter.notifyDataSetChanged();


		parent = (MainActivity)getActivity();
		
		minFrequencyPicker.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						parent.setMinFreq((Integer)minFrequencyPicker.getSelectedItem());
						UpdateFrequencyValues();							
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						UpdateFrequencyValues();							
					}});
		
		maxFrequencyPicker.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				parent.setMaxFreq((Integer)maxFrequencyPicker.getSelectedItem());
				UpdateFrequencyValues();							
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				UpdateFrequencyValues();							
			}});

		
		UpdateFrequencyValues();

		
		//FREQUENCY MODE
		RadioButton radFreqLog = (RadioButton)view.findViewById(R.id.radFrequencyLog);
		radFreqLog.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked)
				{
					parent.SetFrequencyMode(ScalingMode.LOG);
				}
			}});
		RadioButton radFreqLinear = (RadioButton)view.findViewById(R.id.radFrequencyLinear);
		radFreqLinear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked)
				{
					parent.SetFrequencyMode(ScalingMode.LINEAR);
				}
			}});
		RadioButton radFreqExp = (RadioButton)view.findViewById(R.id.radFrequencyExp);
		radFreqExp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked)
				{
					parent.SetFrequencyMode(ScalingMode.EXP);
				}
			}});
		
		if(parent.GetFrequencyMode() == ScalingMode.LOG)
		{
			radFreqLog.setChecked(true);
		} else if(parent.GetFrequencyMode() == ScalingMode.LINEAR)
		{
			radFreqLinear.setChecked(true);
		} else if(parent.GetFrequencyMode() == ScalingMode.EXP)
		{
			radFreqExp.setChecked(true);
		}
		
		
		
		//AMPLITUDE MODE
		RadioButton radAmpLog = (RadioButton)view.findViewById(R.id.radAmplitudeLog);
		radAmpLog.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked)
				{
					parent.SetAmplitudeMode(ScalingMode.LOG);
				}
			}});
		RadioButton radAmpLinear = (RadioButton)view.findViewById(R.id.radAmplitudeLinear);
		radAmpLinear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked)
				{
					parent.SetAmplitudeMode(ScalingMode.LINEAR);
				}
			}});
		RadioButton radAmpExp = (RadioButton)view.findViewById(R.id.radAmplitudeExp);
		radAmpExp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked)
				{
					parent.SetAmplitudeMode(ScalingMode.EXP);
				}
			}});

		if(parent.GetAmplitudeMode() == ScalingMode.LOG)
		{
			radAmpLog.setChecked(true);
		} else if(parent.GetAmplitudeMode() == ScalingMode.LINEAR)
		{
			radAmpLinear.setChecked(true);
		} else if(parent.GetAmplitudeMode() == ScalingMode.EXP)
		{
			radAmpExp.setChecked(true);
		}
		
		
		((Button)view.findViewById(R.id.btnSynchronize)).setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								parent.Synchronize();
							}
						});
		
		return view;
	}
	
	private void UpdateFrequencyValues()
	{
		minFrequencyPicker.setSelection(minAdapter.getPosition(parent.getMinFreq()));
		maxFrequencyPicker.setSelection(maxAdapter.getPosition(parent.getMaxFreq()));

	}
	
}

