
#include "common.hpp"
#include <stdexcept>
#include "SoundTouch-jni.h"

//
using namespace std;
using namespace soundtouch;


//
static string _errMsg = "";

//
#define BUFF_SIZE 4096


// Set error message to return
static void _setErrmsg(const char *msg)
{
	_errMsg = msg;
}


//
extern "C"
{
    JNIEXPORT jstring Java_com_zzfordev_medialink_SoundTouch_getVersionString(JNIEnv *env, jobject thiz)
    {
        const char *verStr = SoundTouch::getVersionString();

        return env->NewStringUTF(verStr);
    }



    JNIEXPORT jlong Java_com_zzfordev_medialink_SoundTouch_newInstance(JNIEnv *env, jobject thiz)
    {
        return (jlong)(new SoundTouch());
    }


    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_deleteInstance(JNIEnv *env, jobject thiz, jlong handle)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        delete ptr;
    }


    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_setTempo(JNIEnv *env, jobject thiz, jlong handle, jfloat tempo)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        ptr->setTempo(tempo);
    }


    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_setSpeed(JNIEnv *env, jobject thiz, jlong handle, jfloat speed)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        ptr->setRate(speed);
    }


    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_setSpeedChange(JNIEnv *env, jobject thiz, jlong handle, jfloat value)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        ptr->setRateChange(value);
    }


    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_setTempoChange(JNIEnv *env, jobject thiz, jlong handle, jfloat value)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        ptr->setTempoChange(value);
    }


    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_setPitchSemiTones(JNIEnv *env, jobject thiz, jlong handle, jfloat pitch)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        ptr->setPitchSemiTones(pitch);
    }

    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_setPitch(JNIEnv *env, jobject thiz, jlong handle, jfloat value)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        ptr->setPitch(value);
    }

    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_setPitchOctaves(JNIEnv *env, jobject thiz, jlong handle, jfloat value)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        ptr->setPitchOctaves(value);
    }

    JNIEXPORT jstring Java_com_zzfordev_medialink_SoundTouch_getErrorString(JNIEnv *env, jobject thiz)
    {
        jstring result = env->NewStringUTF(_errMsg.c_str());
        _errMsg.clear();

        return result;
    }

    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_setSampleRate(JNIEnv *env, jobject thiz, jlong handle, jint jvalue)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        ptr->setSampleRate(jvalue);
    }

    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_setChannels(JNIEnv *env, jobject thiz, jlong handle, jint jvalue)
    {
        SoundTouch *ptr = (SoundTouch*)handle;
        ptr->setChannels(jvalue);
    }

    JNIEXPORT void Java_com_zzfordev_medialink_SoundTouch_putSamples(JNIEnv *env, jobject thiz, jlong handle, jshortArray jsamples, jint jchannels)
    {
        SoundTouch *pSoundTouch = (SoundTouch*)handle;

        //
        jsize jlen = env->GetArrayLength(jsamples);

        int sampleNum = jlen / jchannels;


        jboolean isCopy;
        jshort *jdatas = (jshort*)env->GetPrimitiveArrayCritical(jsamples, &isCopy);

        pSoundTouch->putSamples(jdatas, sampleNum);

        env->ReleasePrimitiveArrayCritical(jsamples, jdatas, 0);
    }

    JNIEXPORT jint Java_com_zzfordev_medialink_SoundTouch_getSamples(JNIEnv *env, jobject thiz, jlong handle, jshortArray jsamples, jint jchannels)
    {
        SoundTouch *pSoundTouch = (SoundTouch*)handle;

        //
        jsize jlen = env->GetArrayLength(jsamples);

        int maxSampleNum = jlen / jchannels;

        jboolean isCopy;
        jshort *jdatas = (jshort*)env->GetPrimitiveArrayCritical(jsamples, &isCopy);

        TRACE("aaaaaa %d", isCopy);

        //
        int receivedSamples = pSoundTouch->receiveSamples(jdatas, maxSampleNum);

        //
        env->ReleasePrimitiveArrayCritical(jsamples, jdatas, 0);

        //
        return receivedSamples;
    }

    JNIEXPORT jshortArray Java_com_zzfordev_medialink_SoundTouch_getSamples2(JNIEnv *env, jobject thiz, jlong handle, jint jchannels)
    {
        SoundTouch *pSoundTouch = (SoundTouch*)handle;

        short buff[4096];

        int receivedSamples = pSoundTouch->receiveSamples(buff, 4096/jchannels);

        jshortArray result = NULL;

        result = env->NewShortArray(receivedSamples*jchannels);

        env->SetShortArrayRegion(result, 0, receivedSamples*jchannels, (short*)buff);

        return result;

    }

}