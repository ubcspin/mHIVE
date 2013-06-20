package org.spin.mhive;

import com.example.mhive.R;

import android.content.Context;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class WaveformDialog extends DialogFragment
{	
	Context context;
	public WaveformDialog()
	{
//        setContentView(R.layout.activity_waveform_selection);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.activity_waveform_selection, container);
		context = view.getContext();
		((Button)view.findViewById(R.id.btnWaveformSelectSine)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_SINE));
		((Button)view.findViewById(R.id.btnWaveformSelectSquare)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_SQUARE));
		((Button)view.findViewById(R.id.btnWaveformSelectSawUp)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_SAWUP));
		((Button)view.findViewById(R.id.btnWaveformSelectTriangle)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_TRIANGLE));
		
		return view;
	}
	
	
	class OnWaveformDialogButtonListener implements OnClickListener
	{
		private int value;
		public OnWaveformDialogButtonListener(int value)
		{
			this.value = value;
		}
		
		@Override
		public void onClick(View v) 
		{
			MainActivity callingActivity = (MainActivity) getActivity();
			callingActivity.SetWaveform(value);
			dismiss();
		}
		
	}

}
