package org.spin.mhive;

import com.example.mhive.R;

import android.app.Activity;
import android.graphics.Point;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

	private int screenHeight;
    private int screenWidth;
    private int minFreq = 20;
    private int maxFreq = 140;
    private AudioTrack track;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(event.getAction() == MotionEvent.ACTION_DOWN
    			|| event.getAction() == MotionEvent.ACTION_MOVE)
    	{
        	float xVal = event.getX()/screenWidth;
        	float yVal = (float) (1.0 - Math.log(event.getY()) / Math.log(screenHeight));
        	yVal = Math.min(Math.max(yVal, 0.0f), 1.0f);
        	int freq = (int)(xVal * maxFreq) + minFreq;
        	float atten = yVal; //attenuation
    		HIVEAudioGenerator.Play(freq, atten);
    	}
    	else if (event.getAction() == MotionEvent.ACTION_UP)
    	{
    		HIVEAudioGenerator.Stop();
    	}

    	return false;
    }          
}
