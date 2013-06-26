package org.spin.mhive;

import com.example.mhive.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ADSRDialog extends DialogFragment {
	
	private SeekBar seekAttack, seekDecay, seekSustain, seekRelease;
	private final int MAX_MS = 1000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.activity_adsr_selection, container);
		seekAttack = (SeekBar)view.findViewById(R.id.seekAttack);
		seekDecay = (SeekBar)view.findViewById(R.id.seekDecay);
		seekSustain = (SeekBar)view.findViewById(R.id.seekSustain);
		seekRelease = (SeekBar)view.findViewById(R.id.seekRelease);
		
		OnWaveformDialogButtonListener listener = new OnWaveformDialogButtonListener();
		seekAttack.setOnSeekBarChangeListener(listener);
		seekDecay.setOnSeekBarChangeListener(listener);
		seekSustain.setOnSeekBarChangeListener(listener);
		seekRelease.setOnSeekBarChangeListener(listener);
		
		return view;
	}
	
	public void SetADSR(ADSREnvelope envelope)
	{
		if(seekAttack != null)
		{
			seekAttack.setMax(MAX_MS);
			seekAttack.setProgress(envelope.getAttack());
		}
		
		if(seekDecay != null)
		{
			seekDecay.setMax(MAX_MS);
			seekDecay.setProgress(envelope.getDecay());
		}
		
		if(seekSustain != null)
		{
			seekSustain.setMax(100);
			seekSustain.setProgress((int)(envelope.getSustain()*100));
		}
		
		if(seekRelease != null)
		{
			seekRelease.setMax(MAX_MS);
			seekRelease.setProgress(envelope.getRelease());
		}
	}
	
	public ADSREnvelope GetADSR()
	{
		return new ADSREnvelope((int)((float)seekAttack.getProgress()/(float)seekAttack.getMax()*(float)MAX_MS), 
				(int)((float)seekDecay.getProgress()/(float)seekDecay.getMax()*(float)MAX_MS),
				(float)seekSustain.getProgress()/(float)seekSustain.getMax(),
				(int)((float)seekRelease.getProgress()/(float)seekRelease.getMax()*(float)MAX_MS));
	}
	
	class OnWaveformDialogButtonListener implements OnSeekBarChangeListener
	{

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2){}
		@Override
		public void onStartTrackingTouch(SeekBar arg0) {}

		@Override
		public void onStopTrackingTouch(SeekBar arg0)
		{
			MainActivity callingActivity = (MainActivity) getActivity();
			callingActivity.SetADSR(GetADSR());			
		}
		
	}


}
