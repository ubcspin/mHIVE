/*===============================================================================================
 GenerateTone Example
 Copyright (c), Firelight Technologies Pty, Ltd 2011.

 This example shows how simply play generated tones using FMOD_System_PlayDSP instead of
 manually connecting and disconnecting DSP units.
===============================================================================================*/

#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <time.h>
#include <signal.h>
#include "fmod.h"
#include "fmod_errors.h"


#define NUM_SOUNDS 3

FMOD_SYSTEM  *gSystem  = 0;
FMOD_CHANNEL *gChannel = 0;
FMOD_DSP	 *gDSP	   = 0;
float gFrequency = 440.0f;

#define SIG SIGALRM

//ADSR envelope timer & data
FMOD_DSP	 *gADSRDSP   = 0;
const int AMPLITUDE_UPDATE_TIMER_INTERVAL_IN_NS = 10000000;
typedef struct
{
	long int attack, decay, release;//in ms
	float sustain;
	long int start_time;
} ADSRSettings;
//TODO: Make this local!
ADSRSettings *gADSRSettings;

//Waveform data
const long OSCILLATOR_SINE = 0;
const long OSCILLATOR_SQUARE = 1;
const long OSCILLATOR_SAWUP = 2;
//NO IDEA WHAT #3 IS...
const long OSCILLATOR_TRIANGLE = 4;
//NOT SUPPORTING NOISE FOR NOW
//const unsigned long OSCILLATOR_NOISE = 5;



#define CHECK_RESULT(x) \
{ \
	FMOD_RESULT _result = x; \
	if (_result != FMOD_OK) \
	{ \
		__android_log_print(ANDROID_LOG_ERROR, "fmod", "FMOD error! (%d) %s\n%s:%d", _result, FMOD_ErrorString(_result), __FILE__, __LINE__); \
		exit(-1); \
	} \
}


long int GetCurrentTimeMillis()
{
    struct timeval curTime;
    gettimeofday(&curTime, NULL);
	long int millis = curTime.tv_sec * 1000 + curTime.tv_usec / 1000;
	return millis;
}

FMOD_RESULT F_CALLBACK ADSRCallback(FMOD_DSP_STATE *dsp_state, float *inbuffer, float *outbuffer, unsigned int length, int inchannels, int outchannels)
{
    unsigned int count;
    int count2;
    ADSRSettings *adsrSettings;
    FMOD_DSP *thisdsp = dsp_state->instance;

    //FMOD_DSP_GetInfo(thisdsp, name, 0, 0, 0, 0);

    //FMOD_DSP_GetUserData(thisdsp, (void **)&adsrSettings);
    adsrSettings = gADSRSettings;
    long int millis = GetCurrentTimeMillis();

    long int elapsed = millis - adsrSettings->start_time;

    float fraction = 1.0f;

    if (elapsed <= adsrSettings->attack)
    {
    	fraction = ((float)elapsed) / ((float)adsrSettings->attack);
    } else if (elapsed <= adsrSettings->attack + adsrSettings->decay)
    {
    	fraction = adsrSettings->sustain
    			+ (1.0f-adsrSettings->sustain)* (1.0f-((float)(elapsed-adsrSettings->attack)) / ((float)adsrSettings->decay));
    } else
    {
    	fraction = adsrSettings->sustain;
    }

	//__android_log_print(ANDROID_LOG_ERROR, "fmod", "startmillis %li\tnowmillis %li\telapsed %li\tfraction %f", adsrSettings->start_time, millis, elapsed, fraction);


    /*
        This loop assumes inchannels = outchannels, which it will be if the DSP is created with '0'
        as the number of channels in FMOD_DSP_DESCRIPTION.
        Specifying an actual channel count will mean you have to take care of any number of channels coming in,
        but outputting the number of channels specified.  Generally it is best to keep the channel
        count at 0 for maximum compatibility.
    */
    for (count = 0; count < length; count++)
    {
        /*
            Feel free to unroll this.
        */
        for (count2 = 0; count2 < outchannels; count2++)
        {
            /*
                This DSP filter just fractions the volume!
                Input is modified, and sent to output.
            */
            outbuffer[(count * outchannels) + count2] = inbuffer[(count * inchannels) + count2] * fraction;
        }
    }

    return FMOD_OK;
}

void Java_org_spin_mhive_HIVEAudioGenerator_cBegin(JNIEnv *env, jobject thiz)
{
	FMOD_RESULT result = FMOD_OK;

	result = FMOD_System_Create(&gSystem);
	CHECK_RESULT(result);

	result = FMOD_System_Init(gSystem, 32, FMOD_INIT_NORMAL, 0);
	CHECK_RESULT(result);

    /*
        Create an oscillator DSP unit for the tone.
    */
    result = FMOD_System_CreateDSPByType(gSystem, FMOD_DSP_TYPE_OSCILLATOR, &gDSP);
    CHECK_RESULT(result);
    result = FMOD_DSP_SetParameter(gDSP, FMOD_DSP_OSCILLATOR_RATE, 440.0f);       /* musical note 'A' */
    CHECK_RESULT(result);

    //add in ADSR
    gADSRSettings = (ADSRSettings*)malloc(sizeof(ADSRSettings));
    gADSRSettings->attack = 300;
    gADSRSettings->decay = 300;
    gADSRSettings->sustain = 0.5f;
    gADSRSettings->start_time = GetCurrentTimeMillis();

	FMOD_DSP_DESCRIPTION  dspdesc;
	memset(&dspdesc, 0, sizeof(FMOD_DSP_DESCRIPTION));
	strcpy(dspdesc.name, "ADSR Envelope");
	dspdesc.channels     = 0;                   // 0 = whatever comes in, else specify.
	dspdesc.read         = ADSRCallback;
	dspdesc.userdata     = NULL;

	result = FMOD_System_CreateDSP(gSystem, &dspdesc, &gADSRDSP);
	CHECK_RESULT(result);
//		Inactive by default.
	//FMOD_DSP_SetBypass(gADSRDSP, 1);
//		Add to system
	result = FMOD_System_AddDSP(gSystem, gADSRDSP, 0);
	CHECK_RESULT(result);

    /* Play */
	result = FMOD_System_PlayDSP(gSystem, FMOD_CHANNEL_REUSE, gDSP, 1, &gChannel);
	CHECK_RESULT(result);
}

void Java_org_spin_mhive_HIVEAudioGenerator_cUpdate(JNIEnv *env, jobject thiz)
{
	FMOD_RESULT	result = FMOD_OK;

	result = FMOD_System_Update(gSystem);
	CHECK_RESULT(result);
}

void Java_org_spin_mhive_HIVEAudioGenerator_cEnd(JNIEnv *env, jobject thiz)
{
	FMOD_RESULT result = FMOD_OK;

	result = FMOD_DSP_Release(gADSRDSP);
	CHECK_RESULT(result);
	free(gADSRDSP);

    result = FMOD_DSP_Release(gDSP);
    CHECK_RESULT(result);

	result = FMOD_System_Release(gSystem);
	CHECK_RESULT(result);

}

void Java_org_spin_mhive_HIVEAudioGenerator_cSetWaveform(JNIEnv *env, jlong jWaveform)
{
	FMOD_RESULT result = FMOD_ERR_UNSUPPORTED;

	long waveform = (long)jWaveform;
	if(		waveform == OSCILLATOR_SINE
			|| waveform == OSCILLATOR_SQUARE
			|| waveform == OSCILLATOR_SAWUP
			|| waveform == OSCILLATOR_TRIANGLE
			)
	{
		result = FMOD_System_PlayDSP(gSystem, FMOD_CHANNEL_REUSE, gDSP, 1, &gChannel);
		CHECK_RESULT(result);
		result = FMOD_DSP_SetParameter(gDSP, FMOD_DSP_OSCILLATOR_TYPE, waveform);
		FMOD_Channel_SetPaused(gChannel, 0);
	}
    CHECK_RESULT(result);
}

jboolean Java_org_spin_mhive_HIVEAudioGenerator_cGetIsChannelPlaying(JNIEnv *env, jobject thiz)
{
	int isplaying = 0;

	FMOD_Channel_IsPlaying(gChannel, &isplaying);

	return isplaying;
}

jfloat Java_org_spin_mhive_HIVEAudioGenerator_cGetChannelFrequency(JNIEnv *env, jobject thiz)
{
	return gFrequency;
}

jfloat Java_org_spin_mhive_HIVEAudioGenerator_cGetChannelVolume(JNIEnv *env, jobject thiz)
{
	float volume = 0.0f;

	FMOD_Channel_GetVolume(gChannel, &volume);

	return volume;
}

jfloat Java_org_spin_mhive_HIVEAudioGenerator_cGetChannelPan(JNIEnv *env, jobject thiz)
{
	float pan = 0.0f;

	FMOD_Channel_GetPan(gChannel, &pan);

	return pan;
}

void Java_org_spin_mhive_HIVEAudioGenerator_cSetChannelVolume(JNIEnv *env, jobject thiz, jfloat volume)
{
	FMOD_Channel_SetVolume(gChannel, volume);
}

void Java_org_spin_mhive_HIVEAudioGenerator_cSetChannelFrequency(JNIEnv *env, jobject thiz, jfloat frequency)
{
	gFrequency = (float)frequency;
	FMOD_RESULT result = FMOD_OK;
	result = FMOD_DSP_SetParameter(gDSP, FMOD_DSP_OSCILLATOR_RATE, gFrequency);
	CHECK_RESULT(result);
}

void Java_org_spin_mhive_HIVEAudioGenerator_cSetChannelPan(JNIEnv *env, jobject thiz, jfloat pan)
{
	FMOD_Channel_SetPan(gChannel, pan);
}


//ADSR Functions
void Java_org_spin_mhive_HIVEAudioGenerator_cResetADSR(JNIEnv *env, jobject thiz)
{
	ADSRSettings *newADSRSettings, *oldADSRSettings;
	newADSRSettings = (ADSRSettings*)malloc(sizeof(ADSRSettings));
	newADSRSettings->attack = 1000;
	newADSRSettings->decay = 1000;
	newADSRSettings->sustain = 0.25f;
	newADSRSettings->start_time = GetCurrentTimeMillis();
	oldADSRSettings = gADSRSettings;
	gADSRSettings = newADSRSettings;
	free(oldADSRSettings);
}


void Java_org_spin_mhive_HIVEAudioGenerator_cEnableADSR(JNIEnv *env, jobject thiz, jboolean b)
{
	//TODO
}


