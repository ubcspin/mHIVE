package org.spin.mhive;

import java.util.Observable;
import java.util.Observer;

import org.spin.mhive.ADSRDialog.ADSRDialogSeekBarChangeListener;
import org.spin.mhive.WaveformDialog.OnWaveformDialogButtonListener;
import org.spin.mhive.replay.HapticNote;
import org.spin.mhive.replay.HapticNoteList;
import org.spin.mhive.replay.HapticNoteRecord;
import org.spin.mhive.replay.HapticNoteRecordPlay;
import org.spin.mhive.replay.HapticNoteRecordStop;

import com.example.mhive.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements Observer {

    private int minFreq = 20;
    private int maxFreq = 140;
    private View mainInputView;
    
	private SeekBar seekAttack, seekDecay, seekSustain, seekRelease;
	private final int MAX_MS = 1000;
    
	ToggleButton tglADSR;
	
    private HIVEAudioGenerator hiveAudioGenerator;
    
    private HapticNoteList noteHistory;
    private ArrayAdapter<HapticNote> noteHistoryAdapter;
    Handler uiHandler;
    Runnable updateThread;
    
    WaveformDialog waveformDialog;
    ADSRDialog adsrDialog;
    RenameDialog renameDialog;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		uiHandler = new Handler();
		updateThread = new Thread() {
			@Override
			public void run()
			{
				SetADSRUIElements(GetADSR());
				SetWaveformUIElements(GetWaveform());
				SetADSREnabledUIElements(hiveAudioGenerator.GetADSREnabled());
			}
		};
        
        setContentView(R.layout.activity_main);
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
    	mainInputView = findViewById(R.id.fullscreen_content);
    	
    	//set up Audio Generator
    	hiveAudioGenerator = new HIVEAudioGenerator();
    	SetWaveform(HIVEAudioGenerator.OSCILLATOR_SINE);
    	
    	//setup waveform button
    	RadioButton btnSineWave = (RadioButton)findViewById(R.id.btnWaveformSelectSine);
    	btnSineWave.setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_SINE));
		((RadioButton)findViewById(R.id.btnWaveformSelectSquare)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_SQUARE));
		((RadioButton)findViewById(R.id.btnWaveformSelectSawUp)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_SAWUP));
		((RadioButton)findViewById(R.id.btnWaveformSelectTriangle)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_TRIANGLE));
    	btnSineWave.setChecked(true);
		
    	
    	//setup ADSR toggle button
    	tglADSR = (ToggleButton)findViewById(R.id.tglADSR);
    	tglADSR.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hiveAudioGenerator.EnableADSR(tglADSR.isChecked());
					return true;
				}
			});
    	tglADSR.setChecked(true);
    	
    	//setup ADSR main button
    	seekAttack = (SeekBar)findViewById(R.id.seekAttack);
		seekDecay = (SeekBar)findViewById(R.id.seekDecay);
		seekSustain = (SeekBar)findViewById(R.id.seekSustain);
		seekRelease = (SeekBar)findViewById(R.id.seekRelease);
		
		seekAttack.setOnSeekBarChangeListener(new ADSRDialogSeekBarChangeListener((TextView)findViewById(R.id.txtAttackValue), MAX_MS));
		seekDecay.setOnSeekBarChangeListener(new ADSRDialogSeekBarChangeListener((TextView)findViewById(R.id.txtDecayValue), MAX_MS));
		seekSustain.setOnSeekBarChangeListener(new ADSRDialogSeekBarChangeListener((TextView)findViewById(R.id.txtSustainValue), 1));
		seekRelease.setOnSeekBarChangeListener(new ADSRDialogSeekBarChangeListener((TextView)findViewById(R.id.txtReleaseValue), MAX_MS));
		SetADSR(new ADSREnvelope(100, 100, 0.8f, 100));
		
		//set up STUB recording
		noteHistory = new HapticNoteList();
		ToggleButton tglRecordButton = (ToggleButton)findViewById(R.id.tglRecordButton);
		ListView lstHistory = (ListView)findViewById(R.id.lstHistory);
		noteHistoryAdapter = new ArrayAdapter<HapticNote>(this, android.R.layout.simple_list_item_1, noteHistory);
		lstHistory.setAdapter(noteHistoryAdapter);
		tglRecordButton.setOnCheckedChangeListener(new RecordingButtonCheckedListener());
		lstHistory.setOnItemClickListener(new HistoryItemSelectedListener());
		lstHistory.setOnItemLongClickListener(new HistoryItemLongSelectedListener());
		
		//set up rename dialog
		renameDialog = new RenameDialog();
		
		hiveAudioGenerator.addObserver(this);
		update(hiveAudioGenerator, null);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(event.getAction() == MotionEvent.ACTION_DOWN
    			|| event.getAction() == MotionEvent.ACTION_MOVE)
    	{
    		if (mainInputView.getX() <= event.getX() 
    				&& mainInputView.getX()+mainInputView.getWidth() >= event.getX()
    				&& mainInputView.getY() <= event.getY() 
    	    				&& mainInputView.getY()+mainInputView.getHeight() >= event.getY())
    		{
	        	float xVal = (event.getX()-mainInputView.getX())/mainInputView.getWidth();
	        	float yVal = (event.getY()-mainInputView.getY())/mainInputView.getHeight();
	        	yVal = (float) (1.0 - Math.log(event.getY()) / Math.log(mainInputView.getHeight()));
	        	yVal = Math.min(Math.max(yVal, 0.0f), 1.0f);
	        	int freq = (int)(xVal * (maxFreq-minFreq)) + minFreq;
	        	float atten = yVal; //attenuation
	        	hiveAudioGenerator.Play(freq, atten);
	        	
    		} else {
    			hiveAudioGenerator.Stop();
    		}
    		
    	}
    	else if (event.getAction() == MotionEvent.ACTION_UP)
    	{
    		hiveAudioGenerator.Stop();
    	}

    	return false;
    }
    
    public void SetWaveform(int waveform)
    {
    	hiveAudioGenerator.setWaveform(waveform);
    }
    
    public void SetWaveformUIElements(int waveform)
    {
    	switch(waveform)
    	{
    		case HIVEAudioGenerator.OSCILLATOR_SINE:
    			((RadioButton)findViewById(R.id.btnWaveformSelectSine)).setChecked(true);
    			break;
    		case HIVEAudioGenerator.OSCILLATOR_SQUARE:
    			((RadioButton)findViewById(R.id.btnWaveformSelectSquare)).setChecked(true);
    			break;
    		case HIVEAudioGenerator.OSCILLATOR_SAWUP:
    			((RadioButton)findViewById(R.id.btnWaveformSelectSawUp)).setChecked(true);
    			break;
    		case HIVEAudioGenerator.OSCILLATOR_TRIANGLE:
    			((RadioButton)findViewById(R.id.btnWaveformSelectTriangle)).setChecked(true);
    			break;
    	}
    }
    
    public int GetWaveform()
    {
    	return hiveAudioGenerator.getCurrentWaveform();
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
			SetWaveform(value);
		}
	}
		
    
    public void SetADSR(ADSREnvelope envelope)
    {
    	hiveAudioGenerator.SetADSR(envelope);
    }
    
    public void SetADSRUIElements(ADSREnvelope envelope)
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
			SetADSR(GetADSR());			
		}
		
	}
	
	class RecordingButtonCheckedListener implements OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked)
			{
				hiveAudioGenerator.StartRecording();
				//TODO: Disable ADSR and Waveform
			}
			else
			{
				noteHistory.add(hiveAudioGenerator.StopRecording());
				noteHistoryAdapter.notifyDataSetChanged();
			}
		}
	}
	
	
	class HistoryItemSelectedListener implements AdapterView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			hiveAudioGenerator.Replay(noteHistory.get(position));
		}
		
	}
	
	class HistoryItemLongSelectedListener implements AdapterView.OnItemLongClickListener
	{

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3)
		{
		    FragmentTransaction ft = getFragmentManager().beginTransaction();
			renameDialog.SetParameters(noteHistoryAdapter, noteHistory.get(position));
			renameDialog.show(ft, "RenameDialog");
			return true;
		}
		
	}

	public void SetADSREnabledUIElements(boolean b)
	{
		tglADSR.setChecked(b);
	}
	

	@Override
	public void update(Observable observable, Object data)
	{
		uiHandler.post(updateThread);
	}
    
    
    
    
}
