package org.spin.mhive;

import org.spin.mhive.WaveformDialog.OnWaveformDialogButtonListener;
import org.spin.mhive.replay.HapticNote;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mhive.R;

public class RenameDialog extends DialogFragment
{	
	HapticNote hn;
	ArrayAdapter<HapticNote> noteHistoryAdapter;
	TextView edtRenameText;
	
	public RenameDialog()
	{
		//do nothing
	}
	
	public void SetParameters(ArrayAdapter<HapticNote> noteHistoryAdapter, HapticNote hn)
	{
		this.noteHistoryAdapter = noteHistoryAdapter;
		this.hn = hn;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.rename_dialog_layout, container);
		edtRenameText = (TextView)view.findViewById(R.id.edtRenameRecording);
		edtRenameText.setText(hn.GetName());
		((Button)view.findViewById(R.id.btnRenameRecording)).setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									if(hn != null && noteHistoryAdapter != null)
									{
										hn.SetName(edtRenameText.getText().toString());
										noteHistoryAdapter.notifyDataSetChanged();
										dismiss();
									}
								}
							});
		((Button)view.findViewById(R.id.btnRenameRecordingCancel)).setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v) {
									dismiss();
								}
							});
		
		((Button)view.findViewById(R.id.btnDeleteRecording)).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(hn != null && noteHistoryAdapter != null)
				{
					noteHistoryAdapter.remove(hn);
					noteHistoryAdapter.notifyDataSetChanged();
					dismiss();
				}
			}
		});
		return view;
	}
	
}

