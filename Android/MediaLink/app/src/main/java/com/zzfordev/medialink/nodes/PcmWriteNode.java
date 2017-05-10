package com.zzfordev.medialink.nodes;


import com.zzfordev.medialink.Parameter;
import com.zzfordev.medialink.nodes.audio.AudioNode;

public class PcmWriteNode extends AudioNode
{
    short[] mBuff16;
    int mIndex;


    public PcmWriteNode(short[] buff)
    {
        mBuff16 = buff;
    }

    @Override
    protected Result onStart()
    {
        mIndex = 0;
        return null;
    }

    @Override
    protected Result onStop()
    {
        mIndex = 0;
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
    public Result push(short[] data, long length)
    {
        if (mBuff16 != null && mIndex < mBuff16.length)
        {
            for(int i=0;i<length;i++)
            {
                mBuff16[mIndex++] = data[i];

                if(mIndex >= mBuff16.length)
                {
                    break;
                }
            }
        }
        return new Result(true,0,"");
    }

    @Override
    protected SetParameterResult onSetParameters(Parameter params)
    {
        return null;
    }
}
