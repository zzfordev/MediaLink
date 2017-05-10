package com.zzfordev.medialink.nodes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.InterruptibleChannel;

import static android.media.AudioTrack.PLAYSTATE_STOPPED;

import com.zzfordev.medialink.SoundTouch;
import com.zzfordev.medialink.Parameter;
import com.zzfordev.medialink.nodes.audio.AudioNode;


public class SoundTouchNode extends Node
{
    // parameters keys
    public static final String PARAMETER_SOUNDTOUCH_PITCH = "parameter_soundtouch_pitch";
    public static final String PARAMETER_SOUNDTOUCH_SEMITONES = "parameter_soundtouch_semitones";
    public static final String PARAMETER_SOUNDTOUCH_OCTAVES = "parameter_soundtouch_octaves";
    public static final String PARAMETER_SOUNDTOUCH_TEMPO = "parameter_soundtouch_tempo";
    public static final String PARAMETER_SOUNDTOUCH_RATE = "parameter_soundtouch_rate";
    public static final String PARAMETER_SOUNDTOUCH_TEMPOCHANGE = "parameter_soundtouch_tempochange";
    public static final String PARAMETER_SOUNDTOUCH_RATECHANGE = "parameter_soundtouch_ratechange";

    //
    private SoundTouch mSoundTouch;

    private int mBitDepth;
    private int mSampleRate;
    private int mChannels;

    @Override
    protected Result onStart()
    {
        mSoundTouch = new SoundTouch();

        updateParameter();
        return null;
    }

    @Override
    protected Result onStop()
    {
        mSoundTouch = null;
        return null;
    }

    @Override
    public Result push(short[] data, long length)
    {
        if (mBitDepth == 16)
        {
            mSoundTouch.putSamples(data, mChannels);

            int sampleNum = 0;
            do
            {
                sampleNum = mSoundTouch.getSamples(data,mChannels);

                if (sampleNum > 0)
                {
                    pushToNext(data, sampleNum * mChannels);
                }
            }
            while (sampleNum != 0);
        }
        else
        {

        }
        return null;
    }

    @Override
    protected boolean onIsSourceNode()
    {
        return false;
    }

    @Override
    protected PushResult onPush()
    {
        return null;
    }

    @Override
    protected SetParameterResult onSetParameters(Parameter params)
    {
        SetParameterResult result = null;

        AudioNode.AudioParameter audioParameter = AudioNode.getAudioParameter(params);

        if (audioParameter.isValid())
        {
            mSampleRate = audioParameter.sampleRate;
            mBitDepth = audioParameter.bitsPerSample;
            mChannels = audioParameter.channels;

            if (mBitDepth != 16 || mChannels > 2)
            {
                result = new SetParameterResult(false, -1, "SoundTouch can only suppport 16bit, 2 channels");
            }
        }

        //
        updateParameter();

        return result;
    }

    private void updateParameter()
    {
        if (mSoundTouch != null)
        {
            mSoundTouch.setSampleRate(mSampleRate);
            mSoundTouch.setChannels(mChannels);

            //
            Float value = null;

            value = mParams.getFloat(PARAMETER_SOUNDTOUCH_TEMPO);
            if (value != null)
            {
                mSoundTouch.setTempo(value);
            }

            value = mParams.getFloat(PARAMETER_SOUNDTOUCH_RATE);
            if (value != null)
            {
                mSoundTouch.setSpeed(value);
            }

            value = mParams.getFloat(PARAMETER_SOUNDTOUCH_TEMPOCHANGE);
            if (value != null)
            {
                mSoundTouch.setTempoChange(value);
            }

            value = mParams.getFloat(PARAMETER_SOUNDTOUCH_RATECHANGE);
            if (value != null)
            {
                mSoundTouch.setSpeedChange(value);
            }

            value = mParams.getFloat(PARAMETER_SOUNDTOUCH_PITCH);
            if (value != null)
            {
                mSoundTouch.setPitch(value);
            }

            value = mParams.getFloat(PARAMETER_SOUNDTOUCH_SEMITONES);
            if (value != null)
            {
                mSoundTouch.setPitchSemiTones(value);
            }

            value = mParams.getFloat(PARAMETER_SOUNDTOUCH_OCTAVES);
            if (value != null)
            {
                mSoundTouch.setPitchOctaves(value);
            }
        }
    }
}
