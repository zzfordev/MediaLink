package com.zzfordev.medialink.nodes.audio;

import android.media.AudioRecord;

import com.zzfordev.medialink.Parameter;
import com.zzfordev.medialink.nodes.Node;

public abstract class AudioNode extends Node
{
    public static class AudioParameter
    {
        public int sampleRate = -1;
        public int bitsPerSample = -1;
        public int channels = -1;

        public boolean equals(AudioParameter value)
        {
            boolean result = false;
            if(value != null)
            {
                result = value.sampleRate == sampleRate &&
                        value.bitsPerSample == bitsPerSample &&
                        value.channels == channels;
            }
            return result;
        }

        public boolean isValid()
        {
            return sampleRate >= 0 && bitsPerSample >=0 && channels >=0;
        }
    }


    ////
    protected AudioParameter mAudioParams;

    //
    protected SetParameterResult applyAudioParameter(AudioParameter audioParameter, boolean restartNode)
    {
        SetParameterResult result = null;

        String errMsg = null;
        int errCode = 0;

        if (audioParameter  == null)
        {
            errMsg = "input parameter is null";
            errCode = -1;
        }
        else if( ! audioParameter.isValid())
        {
            errMsg = "input parameter is invalid";
            errCode = -1;
        }
        else
        {
            mAudioParams = audioParameter;
        }

        //
        result = new SetParameterResult(errMsg == null, errCode, errMsg);
        result.needRestartNode = restartNode;

        return result;
    }

    //
    public static AudioParameter getAudioParameter(Parameter params)
    {
        Integer freq = params.getInt(PARAMETER_FREQUENCY);
        if (freq == null)
        {
            freq = -1;
        }

        Integer bitDepth = params.getInt(PARAMETER_BIT_DEPTH);
        if (bitDepth == null)
        {
            bitDepth = -1;
        }

        Integer channels = params.getInt(PARAMETER_CHANNELS);
        if (channels == null)
        {
            channels = -1;
        }

        AudioParameter audioParameter = new AudioParameter();
        audioParameter.sampleRate = freq;
        audioParameter.bitsPerSample = bitDepth;
        audioParameter.channels = channels;

        return audioParameter;
    }
}
