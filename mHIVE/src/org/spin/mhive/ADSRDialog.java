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
import android.widget.TextView;

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
		
		seekAttack.setOnSeekBarChangeListener(new ADSRDialogSeekBarChangeListener((TextView)view.findViewById(R.id.txtAttackValue), MAX_MS));
		seekDecay.setOnSeekBarChangeListener(new ADSRDialogSeekBarChangeListener((TextView)view.findViewById(R.id.txtDecayValue), MAX_MS));
		seekSustain.setOnSeekBarChangeListener(new ADSRDialogSeekBarChangeListener((TextView)view.findViewById(R.id.txtSustainValue), 1));
		seekRelease.setOnSeekBarChangeListener(new ADSRDialogSeekBarChangeListener((TextView)view.findViewById(R.id.txtReleaseValue), MAX_MS));
		
		
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
	
	class ADSRDialogSeekBarChangeListener implements OnSeekBarChangeListener
	{
		TextView displayView;
		int max;
		public ADSRDialogSeekBarChangeListener(TextView displayView, int max)
		{
			this.displayView = displayView;
			this.max = max;
		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2)
		{
			float value = ((float)arg1/(float)arg0.getMax() * (float) max); 
			displayView.setText(""+value);
		}
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
