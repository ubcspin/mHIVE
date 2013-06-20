package org.spin.mhive;

import com.example.mhive.R;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

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
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
    	mainInputView = findViewById(R.id.fullscreen_content);
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
	    		HIVEAudioGenerator.Play(freq, atten);
    		} else {
    			HIVEAudioGenerator.Stop();
    		}
    		
    	}
    	else if (event.getAction() == MotionEvent.ACTION_UP)
    	{
    		HIVEAudioGenerator.Stop();
    	}

    	return false;
    }          
}
