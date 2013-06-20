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
		((Button)view.findViewById(R.id.btnWaveformSelectSine)).setOnClickListener(new OnWaveformDialogButtonListener());
		((Button)view.findViewById(R.id.btnWaveformSelectSquare)).setOnClickListener(new OnWaveformDialogButtonListener());
		((Button)view.findViewById(R.id.btnWaveformSelectSawUp)).setOnClickListener(new OnWaveformDialogButtonListener());
		((Button)view.findViewById(R.id.btnWaveformSelectTriangle)).setOnClickListener(new OnWaveformDialogButtonListener());
		
		return view;
	}
	
	
	class OnWaveformDialogButtonListener implements OnClickListener
	{
		@Override
		public void onClick(View v) 
		{
			dismiss();
		}
		
	}

}
