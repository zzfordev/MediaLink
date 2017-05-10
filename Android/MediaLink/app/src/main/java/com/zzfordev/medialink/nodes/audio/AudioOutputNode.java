package com.zzfordev.medialink.nodes.audio;


import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import static android.media.AudioTrack.PLAYSTATE_STOPPED;
import static android.media.AudioTrack.WRITE_BLOCKING;

import com.zzfordev.medialink.MediaUtil;
import com.zzfordev.medialink.Parameter;
import com.zzfordev.medialink.nodes.audio.AudioNode;

public class AudioOutputNode extends AudioNode
{
    private static final int BUFF_LENGTH = 24*1024;

    private AudioTrack mAudioTrack;

    //
    @Override
    protected Result onStart()
    {
        if (mAudioParams == null)
        {
            return new Result(false, -1, "no audio parameters");
        }

        if (mAudioTrack == null)
        {
            int channel = mAudioParams.channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
            int pcmEncodingBits = MediaUtil.getAudioFmtPCMEncoding(mAudioParams.bitsPerSample);

            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mAudioParams.sampleRate, channel, pcmEncodingBits, BUFF_LENGTH, AudioTrack.MODE_STREAM);

            mAudioTrack.play();
        }
        return null;
    }

    @Override
    protected Result onStop()
    {
        if (mAudioTrack != null)
        {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
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
    @TargetApi(23)
    public Result push(short[] data, long length)
    {
        Result result = null;

        //
        if (mAudioTrack != null)
        {
            if (mAudioTrack.getPlayState() != PLAYSTATE_STOPPED)
            {
                int buffOffset = 0;
                int buffRemaining = (int)length;

                do
                {
                    final int BUFFER_LENGTH_IN_SHORT = BUFF_LENGTH / (mAudioParams.bitsPerSample / 8);
                    int lengthToWrite = buffRemaining > BUFFER_LENGTH_IN_SHORT ? BUFFER_LENGTH_IN_SHORT : buffRemaining;
                    lengthToWrite = mAudioTrack.write(data, buffOffset, lengthToWrite, WRITE_BLOCKING);

                    buffOffset += lengthToWrite;
                    buffRemaining = (int)length - buffOffset;
                }
                while (buffRemaining > 0);

                result = new Result(true, 0, "");
            }
        }

        //
        return result;
    }

    @Override
    protected SetParameterResult onSetParameters(Parameter params)
    {
        return applyAudioParameter(AudioNode.getAudioParameter(params), true);
    }
}
