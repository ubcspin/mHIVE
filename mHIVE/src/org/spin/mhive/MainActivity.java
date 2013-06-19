package org.spin.mhive;

import org.spin.mhive.util.SystemUiHider;

import com.example.mhive.R;

import android.annotation.TargetApi;
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

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    
    
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
        
        

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                    }
                });
        
        /*controlsView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				TextView t = (TextView)findViewById(R.id.testText1);
				t.setText(""+event.getX());
				return false;
			}
        	
        	
        });*/
        
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(event.getAction() == MotionEvent.ACTION_DOWN
    			|| event.getAction() == MotionEvent.ACTION_MOVE)
    	{
        	float xVal = event.getX()/screenWidth;
        	float yVal = (float) (1.0 - Math.log(event.getY()) / Math.log(screenHeight));
        	yVal = Math.min(Math.max(yVal, 0.0f), 1.0f);
        	int freq = (int)(xVal * (maxFreq-minFreq)) + minFreq;
        	float atten = yVal; //attenuation
    		HIVEAudioGenerator.Play(freq, atten);
    	}
    	else if (event.getAction() == MotionEvent.ACTION_UP)
    	{
    		HIVEAudioGenerator.Stop();
    	}

    	return false;
    }
    
    

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };
          
    
}
