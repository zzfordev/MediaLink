
package com.zzfordev.medialink;

public final class SoundTouch
{
    public native final static String getVersionString();

    private native final void setTempo(long handle, float value);
    private native final void setSpeed(long handle, float value);
    private native final void setTempoChange(long handle, float value);
    private native final void setSpeedChange(long handle, float value);

    private native final void setPitch(long handle, float value);
    private native final void setPitchSemiTones(long handle, float value);
    private native final void setPitchOctaves(long handle, float value);

    private native final void setSampleRate(long handle, int value);
    private native final void setChannels(long handle, int value);
    public native final static String getErrorString();
    private native final static long newInstance();
    private native final void deleteInstance(long handle);
    private native final void putSamples(long handle, short[] samples, int channels);
    private native final short[] getSamples2(long handle, int channels);
    private native final int getSamples(long handle, short[] samples, int channels);

    private long handle = 0;

    public SoundTouch()
    {
        handle = newInstance();
    }

    public void close()
    {
        deleteInstance(handle);
        handle = 0;
    }

    public void setTempo(float tempo)
    {
        setTempo(handle, tempo);
    }

    public void setSpeed(float speed)
    {
        setSpeed(handle, speed);
    }

    public void setTempoChange(float value)
    {
        setTempoChange(handle, value);
    }

    public void setSpeedChange(float value)
    {
        setSpeedChange(handle, value);
    }

    public void setPitchSemiTones(float pitch)
    {
        setPitchSemiTones(handle, pitch);
    }

    public void setPitch(float value)
    {
        setPitch(handle, value);
    }

    public void setPitchOctaves(float value)
    {
        setPitchOctaves(handle,value);
    }

    public void setSampleRate(int value)
    {
        setSampleRate(handle, value);
    }

    public void setChannels(int value)
    {
        setChannels(handle, value);
    }

    public void putSamples(short[] samples, int channels)
    {
        putSamples(handle, samples, channels);
    }
    public int getSamples(short[] samples, int channels)
    {
        return getSamples(handle, samples, channels);
    }

    public short[] getSamples2(int channels)
    {
        return getSamples2(handle, channels);
    }
}
