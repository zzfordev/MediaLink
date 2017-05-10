package com.zzfordev.medialink.nodes.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.zzfordev.medialink.Global;
import com.zzfordev.medialink.MediaUtil;
import com.zzfordev.medialink.Parameter;
import com.zzfordev.medialink.nodes.audio.AudioNode;

import static android.media.AudioTrack.PLAYSTATE_STOPPED;


public class AudioInputNode extends AudioNode
{
    private boolean mIsStopped;
    private AudioRecord mAudioRecord;
    private short[] mBuffer;


    @Override
    @TargetApi(23)
    protected Result onStart()
    {
        if (mAudioParams == null)
        {
            return new Result(false, -1, "no audio parameters");
        }

        //
        if (mAudioRecord == null)
        {
            try
            {
                final int channel = mAudioParams.channels == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
                final int pcmEncodingBits = MediaUtil.getAudioFmtPCMEncoding(mAudioParams.bitsPerSample);
                final int bufferSize = AudioRecord.getMinBufferSize(mAudioParams.sampleRate, channel, pcmEncodingBits);

                //
                mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mAudioParams.sampleRate, channel, pcmEncodingBits, bufferSize);

                //
                AudioManager audioManager = (AudioManager) Global.application.getSystemService(Context.AUDIO_SERVICE);

                AudioDeviceInfo[] audioDevInfos = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);

                if (audioDevInfos != null)
                {
                    for(AudioDeviceInfo audioDevInfo : audioDevInfos)
                    {
                        if (audioDevInfo.getType() == AudioDeviceInfo.TYPE_BUILTIN_MIC)
                        {
                            mAudioRecord.setPreferredDevice(audioDevInfo);
                        }
                    }
                }

                //
                int state = mAudioRecord.getState();

                if (state != AudioRecord.STATE_INITIALIZED)
                {
                    mAudioRecord.release();
                    return new Result(false, -1, "AudioRecord not initialized");
                }

                //
                mAudioRecord.startRecording();

                //
                mBuffer = new short[bufferSize];

                //
                mIsStopped = false;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                if (mAudioRecord != null)
                {
                    mAudioRecord.release();
                }
            }
        }

        return null;
    }

    @Override
    protected boolean onIsSourceNode()
    {
        return true;
    }

    @Override
    protected PushResult onPush()
    {
        int len = mAudioRecord.read(mBuffer, 0, mBuffer.length);

        if ((len == AudioRecord.ERROR_INVALID_OPERATION || len == AudioRecord.ERROR_BAD_VALUE))
        {
            return null;
        }

        //
        pushToNext(mBuffer,len);

        return null;
    }

    @Override
    protected Result onStop()
    {
        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioRecord = null;
        return null;
    }

    @Override
    protected SetParameterResult onSetParameters(Parameter params)
    {
        return applyAudioParameter(AudioNode.getAudioParameter(params), true);
    }

}
