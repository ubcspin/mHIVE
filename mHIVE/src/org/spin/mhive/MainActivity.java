package org.spin.mhive;

import org.spin.mhive.WaveformDialog.OnWaveformDialogButtonListener;

import com.example.mhive.R;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

    private int minFreq = 20;
    private int maxFreq = 140;
    private View mainInputView;
    
    private HIVEAudioGenerator hiveAudioGenerator;
    
    WaveformDialog waveformDialog;
    ADSRDialog adsrDialog;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
    	mainInputView = findViewById(R.id.fullscreen_content);
    	
    	//set up Audio Generator
    	hiveAudioGenerator = new HIVEAudioGenerator();
    	SetWaveform(HIVEAudioGenerator.OSCILLATOR_SINE);
    	
    	//setup waveform button
    	((Button)findViewById(R.id.btnWaveformSelectSine)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_SINE));
		((Button)findViewById(R.id.btnWaveformSelectSquare)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_SQUARE));
		((Button)findViewById(R.id.btnWaveformSelectSawUp)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_SAWUP));
		((Button)findViewById(R.id.btnWaveformSelectTriangle)).setOnClickListener(new OnWaveformDialogButtonListener(HIVEAudioGenerator.OSCILLATOR_TRIANGLE));
		
    	
    	//setup ADSR toggle button
    	ToggleButton tglADSR = (ToggleButton)findViewById(R.id.tglADSR);
    	tglADSR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					hiveAudioGenerator.EnableADSR(isChecked);
				}
			});
    	tglADSR.setChecked(true);
    	
    	//setup ADSR main button
    	adsrDialog = new ADSRDialog();
    	Button btnADSR = (Button)findViewById(R.id.btnADSR);
    	btnADSR.setOnClickListener(new ADSRClickListener());
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
    
    class ADSRClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			adsrDialog.show(getFragmentManager(), "ADSRDialog");
			adsrDialog.SetADSR(hiveAudioGenerator.GetADSR());
		}
    }
    
}
