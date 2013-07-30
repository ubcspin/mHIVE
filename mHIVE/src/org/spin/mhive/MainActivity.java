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
    public int getMinFreq() {return minFreq;}
    public int getMaxFreq() {return maxFreq;}
    public void setMinFreq(int newMinFreq)
    {
    	if (newMinFreq < maxFreq)
    	{
    		minFreq = newMinFreq;
    	}
    }
    public void setMaxFreq(int newMaxFreq)
    {
    	if(newMaxFreq > minFreq)
    	{
    		maxFreq = newMaxFreq;
    	}
    }
    
    /**
     *  Frequency Mode manipulation
     * @author oli
     *
     */
    public enum FrequencyMode {
    	LOG, LINEAR, EXP
    }
    private FrequencyMode frequencyMode = FrequencyMode.LOG;
    public FrequencyMode GetFrequencyMode() { return frequencyMode;}
    public void SetFrequency(FrequencyMode newMode)
	{ frequencyMode = newMode;
    	if (mainInputView != null)
    	{
    		mainInputView.invalidate();
    	}
	}
    
    private VisualTraceView mainInputView;
    
	ToggleButton tglADSR;
	
    private HIVEAudioGenerator hiveAudioGenerator;
    
    private HapticNoteList noteHistory;
    private ArrayAdapter<HapticNote> noteHistoryAdapter;
    Handler uiHandler;
    Runnable updateThread;
    
    ADSRView adsrView;
    
    WaveformDialog waveformDialog;
    ADSRDialog adsrDialog;
    RenameDialog renameDialog;
    SettingsDialog settingsDialog;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		uiHandler = new Handler();
		updateThread = new Thread() {
			@Override
			public void run()
			{
				SetWaveformUIElements(GetWaveform());
				SetADSREnabledUIElements(hiveAudioGenerator.GetADSREnabled());
				adsrView.EnableADSR(hiveAudioGenerator.GetADSREnabled());
				adsrView.SetADSR(hiveAudioGenerator.GetADSR());
			}
		};
        
        setContentView(R.layout.activity_main);
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
    	mainInputView = (VisualTraceView)findViewById(R.id.fullscreen_content);
    	mainInputView.SetFrequencyRange(minFreq, maxFreq);
    	
    	//set up Audio Generator
    	hiveAudioGenerator = new HIVEAudioGenerator();
    	hiveAudioGenerator.SetVisualTraceView(mainInputView);
    	
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
    	tglADSR.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					hiveAudioGenerator.EnableADSR(isChecked);
				}
			});
    	tglADSR.setChecked(true);
    	
		adsrView = (ADSRView)findViewById(R.id.adsrVisualization);
		adsrView.SetMainActivity(this);
		SetADSR(new ADSREnvelope(100, 100, 0.8f, 100));
    	hiveAudioGenerator.SetADSRView(adsrView);
		
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
		
		
		//set up settings dialog
		settingsDialog = new SettingsDialog();
		Button settingsButton = (Button)findViewById(R.id.btnSettings);
		settingsButton.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
								    FragmentTransaction ft = getFragmentManager().beginTransaction();
									settingsDialog.show(ft, "SettingsDialog");
								}
								
							});
		
		hiveAudioGenerator.addObserver(this);
		update(hiveAudioGenerator, null);
    }
    
    public int CalculateFrequencyFromFractionalPosition(float zeroToOne)
    {
    	int freq = 0;
    	float freqRange = maxFreq - minFreq;
    	if (frequencyMode == FrequencyMode.LOG)
    	{
    		freq = (int)(Math.log(zeroToOne*(freqRange-1)+1)/Math.log(freqRange)*freqRange) + minFreq;
    	} else if (frequencyMode == FrequencyMode.LINEAR)
    	{
    		freq = (int)(zeroToOne * freqRange) + minFreq;
    	} else if (frequencyMode == FrequencyMode.EXP)
    	{
    		freq = (int) (Math.pow(freqRange+1, zeroToOne)) + minFreq - 1;
    	}
    	
    	return freq;
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
	        	float yVal = 1.0f-(event.getY()-mainInputView.getY())/mainInputView.getHeight();
	        	yVal = Math.min(Math.max(yVal, 0.0f), 1.0f);
	        	int freq = CalculateFrequencyFromFractionalPosition(xVal);
	        	float atten = yVal; //attenuation
	        	hiveAudioGenerator.Play(freq, atten, event.getX(), event.getY());
	        		        	
    		} else {
    			hiveAudioGenerator.Stop();
    		}
    	}
    	else if (event.getAction() == MotionEvent.ACTION_UP)
    	{
    		hiveAudioGenerator.Stop();
    	}

    	mainInputView.addPoint(event.getX(), event.getY());

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
    
	public ADSREnvelope GetUIADSR()
	{
		return adsrView.GetADSR();
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
