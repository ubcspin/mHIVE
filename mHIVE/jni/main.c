/*===============================================================================================
 GenerateTone Example
 Copyright (c), Firelight Technologies Pty, Ltd 2011.

 This example shows how simply play generated tones using FMOD_System_PlayDSP instead of
 manually connecting and disconnecting DSP units.
===============================================================================================*/

#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include "fmod.h"
#include "fmod_errors.h"

#define NUM_SOUNDS 3

FMOD_SYSTEM  *gSystem  = 0;
FMOD_CHANNEL *gChannel = 0;
FMOD_DSP	 *gDSP	   = 0;
float gFrequency = 440.0f;

#define CHECK_RESULT(x) \
{ \
	FMOD_RESULT _result = x; \
	if (_result != FMOD_OK) \
	{ \
		__android_log_print(ANDROID_LOG_ERROR, "fmod", "FMOD error! (%d) %s\n%s:%d", _result, FMOD_ErrorString(_result), __FILE__, __LINE__); \
		exit(-1); \
	} \
}

void Java_org_fmod_generatetone_Example_cBegin(JNIEnv *env, jobject thiz)
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

}

void Java_org_fmod_generatetone_Example_cUpdate(JNIEnv *env, jobject thiz)
{
	FMOD_RESULT	result = FMOD_OK;

	result = FMOD_System_Update(gSystem);
	CHECK_RESULT(result);
}

void Java_org_fmod_generatetone_Example_cEnd(JNIEnv *env, jobject thiz)
{
	FMOD_RESULT result = FMOD_OK;

    result = FMOD_DSP_Release(gDSP);
    CHECK_RESULT(result);

	result = FMOD_System_Release(gSystem);
	CHECK_RESULT(result);
}

void Java_org_fmod_generatetone_Example_cPlayDSPSine(JNIEnv *env, jobject thiz, int id)
{
	FMOD_RESULT result = FMOD_OK;

    result = FMOD_System_PlayDSP(gSystem, FMOD_CHANNEL_REUSE, gDSP, 1, &gChannel);
	CHECK_RESULT(result);
    FMOD_Channel_SetVolume(gChannel, 0.5f);
    result = FMOD_DSP_SetParameter(gDSP, FMOD_DSP_OSCILLATOR_TYPE, 0);
    CHECK_RESULT(result);
    FMOD_Channel_SetPaused(gChannel, 0);
}

void Java_org_fmod_generatetone_Example_cPlayDSPSquare(JNIEnv *env, jobject thiz, int id)
{
	FMOD_RESULT result = FMOD_OK;

    result = FMOD_System_PlayDSP(gSystem, FMOD_CHANNEL_REUSE, gDSP, 1, &gChannel);
	CHECK_RESULT(result);
    FMOD_Channel_SetVolume(gChannel, 0.125f);
    result = FMOD_DSP_SetParameter(gDSP, FMOD_DSP_OSCILLATOR_TYPE, 1);
    CHECK_RESULT(result);
    FMOD_Channel_SetPaused(gChannel, 0);
}

void Java_org_fmod_generatetone_Example_cPlayDSPSawUp(JNIEnv *env, jobject thiz, int id)
{
	FMOD_RESULT result = FMOD_OK;

    result = FMOD_System_PlayDSP(gSystem, FMOD_CHANNEL_REUSE, gDSP, 1, &gChannel);
	CHECK_RESULT(result);
    FMOD_Channel_SetVolume(gChannel, 0.125f);
    result = FMOD_DSP_SetParameter(gDSP, FMOD_DSP_OSCILLATOR_TYPE, 2);
    CHECK_RESULT(result);
    FMOD_Channel_SetPaused(gChannel, 0);
}

void Java_org_fmod_generatetone_Example_cPlayDSPTriangle(JNIEnv *env, jobject thiz, int id)
{
	FMOD_RESULT result = FMOD_OK;

    result = FMOD_System_PlayDSP(gSystem, FMOD_CHANNEL_REUSE, gDSP, 1, &gChannel);
	CHECK_RESULT(result);
    FMOD_Channel_SetVolume(gChannel, 0.5f);
    result = FMOD_DSP_SetParameter(gDSP, FMOD_DSP_OSCILLATOR_TYPE, 4);
    CHECK_RESULT(result);
    FMOD_Channel_SetPaused(gChannel, 0);
}

void Java_org_fmod_generatetone_Example_cPlayDSPNoise(JNIEnv *env, jobject thiz, int id)
{
	FMOD_RESULT result = FMOD_OK;

    result = FMOD_System_PlayDSP(gSystem, FMOD_CHANNEL_REUSE, gDSP, 1, &gChannel);
	CHECK_RESULT(result);
    FMOD_Channel_SetVolume(gChannel, 0.25f);
    result = FMOD_DSP_SetParameter(gDSP, FMOD_DSP_OSCILLATOR_TYPE, 5);
    CHECK_RESULT(result);
    FMOD_Channel_SetPaused(gChannel, 0);
}

jboolean Java_org_fmod_generatetone_Example_cGetIsChannelPlaying(JNIEnv *env, jobject thiz)
{
	int isplaying = 0;

	FMOD_Channel_IsPlaying(gChannel, &isplaying);

	return isplaying;
}

jfloat Java_org_fmod_generatetone_Example_cGetChannelFrequency(JNIEnv *env, jobject thiz)
{
	//float frequency = 0.0f;

	//FMOD_Channel_GetFrequency(gChannel, &frequency);

	return gFrequency;
}

jfloat Java_org_fmod_generatetone_Example_cGetChannelVolume(JNIEnv *env, jobject thiz)
{
	float volume = 0.0f;

	FMOD_Channel_GetVolume(gChannel, &volume);

	return volume;
}

jfloat Java_org_fmod_generatetone_Example_cGetChannelPan(JNIEnv *env, jobject thiz)
{
	float pan = 0.0f;

	FMOD_Channel_GetPan(gChannel, &pan);

	return pan;
}

void Java_org_fmod_generatetone_Example_cSetChannelVolume(JNIEnv *env, jobject thiz, jfloat volume)
{
	FMOD_Channel_SetVolume(gChannel, volume);
}

void Java_org_fmod_generatetone_Example_cSetChannelFrequency(JNIEnv *env, jobject thiz, jfloat frequency)
{
	gFrequency = (float)frequency;
	//FMOD_Channel_SetFrequency(gChannel, frequency);
	FMOD_RESULT result = FMOD_OK;
	result = FMOD_DSP_SetParameter(gDSP, FMOD_DSP_OSCILLATOR_RATE, gFrequency);
	CHECK_RESULT(result);
}

void Java_org_fmod_generatetone_Example_cSetChannelPan(JNIEnv *env, jobject thiz, jfloat pan)
{
	FMOD_Channel_SetPan(gChannel, pan);
}
