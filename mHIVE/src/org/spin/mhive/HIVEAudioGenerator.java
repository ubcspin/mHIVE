package org.spin.mhive;

import org.fmod.FMODAudioDevice;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

//Singleton class for handling audio generation for mHIVE
public class HIVEAudioGenerator
{
	
    private static FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

	
	
	private static boolean initiated = false;
	private static boolean playing = false;
	private final static int minBufferSize = 4096;
	public final static double tau = 2*Math.PI;
	public final static String TAG = "HIVEAudioGenerator";
	
	public static void Init()
	{	
    	System.loadLibrary("fmodex");
    	System.loadLibrary("main");
		initiated = true;
		mFMODAudioDevice.start();
		cBegin();
	}
	
	
	public static void Play(int freq, float atten)
	{
		if(!initiated)
		{
			Init();
		}
		if(!playing)
		{
			cPlayDSPSine();
			playing = true;
		}
    	
		cSetChannelFrequency(freq);
		cSetChannelVolume(atten);

	}
	
	public static void Stop()
	{
		if(playing)
		{
	    	//cEnd();
	    	//mFMODAudioDevice.stop();
			cSetChannelVolume(0);
	    	playing = false;
		}

	}
	
//	static 
//    {
//    	System.loadLibrary("fmodex");
//        System.loadLibrary("main");
//    }
    
	public native static void cBegin();
	public native static void cUpdate();
	public native static void cEnd();
	
	public native static void cPlayDSPSine();
	//public native void cPlayDSPSquare();
	//public native void cPlayDSPSawUp();
	//public native void cPlayDSPTriangle();
	//public native void cPlayDSPNoise();	
	
	public native static boolean cGetIsChannelPlaying();
	public native static float cGetChannelFrequency();
	public native static float cGetChannelVolume();
	public native static float cGetChannelPan();
	
	public native static void cSetChannelFrequency(float frequency);
	public native static void cSetChannelVolume(float volume);
	public native static void cSetChannelPan(float pan);

}
