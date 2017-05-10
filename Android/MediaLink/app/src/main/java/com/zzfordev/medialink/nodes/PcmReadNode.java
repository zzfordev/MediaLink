package com.zzfordev.medialink.nodes;


import com.zzfordev.medialink.Parameter;
import com.zzfordev.medialink.nodes.audio.AudioNode;

public class PcmReadNode extends Node
{
    short[] mBuff16;

    public PcmReadNode(short[] buff)
    {
        mBuff16 = buff;
    }

    @Override
    protected Result onStart()
    {
        return null;
    }

    @Override
    protected Result onStop()
    {
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
        PushResult result = new PushResult(true,0,"");

        pushToNext(mBuff16, mBuff16.length);

        result.dataEnd = true;
        return result;
    }

    @Override
    protected SetParameterResult onSetParameters(Parameter params)
    {
        return null;
    }
}
