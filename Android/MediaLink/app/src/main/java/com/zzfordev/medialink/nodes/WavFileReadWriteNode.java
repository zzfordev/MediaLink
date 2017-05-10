package com.zzfordev.medialink.nodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.zzfordev.medialink.Parameter;

public class WavFileReadWriteNode extends Node
{
    private File mFile;
    private int mWaveDataOffset;
    private int mBitWidth;

    private boolean mIsRead;
    private FileOutputStream mFileOutputStream;
    private FileInputStream mFileInputStream;
    private WaveHeader mOutputWareHeader;
    private boolean mNeedToStop;
    private short[] mBuffer16 = null;

    public WavFileReadWriteNode(boolean isRead, File file)
    {
        mIsRead = isRead;
        mFile = file;
    }

    //
    @Override
    protected Result onStart()
    {
        Result result = null;

        if (mIsRead)
        {
            mNeedToStop = false;
            try
            {
                mFileInputStream = new FileInputStream(mFile);
                mFileInputStream.skip(mWaveDataOffset);
            }
            catch (Exception e)
            {

            }
            finally
            {
                if (mFileInputStream != null)
                {
                    try
                    {
                        mFileInputStream.close();
                    }
                    catch (Exception e)
                    {

                    }

                    mFileInputStream = null;
                }
            }
        }
        else
        {
            try
            {
                mFileOutputStream = new FileOutputStream(mFile);
                mOutputWareHeader.write(mFileOutputStream);
            }
            catch (Exception e)
            {
                result = new Result(false, -1, e.getLocalizedMessage());
            }
        }
        return result;
    }

    @Override
    protected Result onStop()
    {
        if (mIsRead)
        {
            mNeedToStop = true;
            if (mFileInputStream != null)
            {
                try
                {
                    mFileInputStream.close();
                }
                catch (Exception e)
                {

                }

                mFileInputStream = null;
            }

        }
        else
        {
            if (mFileOutputStream != null)
            {
                try
                {
                    mFileOutputStream.flush();
                    mFileOutputStream.close();
                }
                catch (Exception e)
                {

                }
                mFileOutputStream = null;
            }
        }

        return null;
    }

    @Override
    protected boolean onIsSourceNode()
    {
        return mIsRead ? true : false;
    }

    @Override
    protected PushResult onPush()
    {
        PushResult result = new PushResult(true,0,"");

        if (mIsRead)
        {
            boolean endOfStream = false;

            if (mFileInputStream != null)
            {
                if (mBitWidth == 16)
                {
                    if (mBuffer16 == null)
                    {
                        mBuffer16 = new short[4096];
                    }
                    int length = 0;

                    for (int i = 0; i < mBuffer16.length; i++)
                    {
                        try
                        {
                            mBuffer16[i] = WaveHeader.readShort(mFileInputStream);
                            length++;
                        }
                        catch (IOException e)
                        {
                            endOfStream = true;
                        }
                    }

                    //
                    pushToNext(mBuffer16, length);
                }

                if (endOfStream)
                {
                    result.dataEnd = true;
                }
            }
        }

        return result;
    }

    @Override
    public Result push(short[] data, long length)
    {
        if (mIsRead)
        {
            return null;
        }
        else
        {
            for (int i=0;i<length;i++)
            {
                try
                {
                    WaveHeader.writeShort(mFileOutputStream,data[i]);
                }
                catch (IOException e)
                {
                }
            }
            return new Result(true,0,"");
        }
    }

    @Override
    protected SetParameterResult onSetParameters(Parameter params)
    {
        SetParameterResult result = null;
        try
        {
            mParams.putAll(params);

            //
            WaveHeader waveHeader = new WaveHeader();

            if (mIsRead)
            {
                FileInputStream is = new FileInputStream(mFile);
                mWaveDataOffset = waveHeader.read(is);

                //
                int bitWidth = waveHeader.getBitsPerSample();
                int sampleRate = waveHeader.getSampleRate();
                int channels = waveHeader.getNumChannels();

                mBitWidth = bitWidth;

                mParams.putInt(PARAMETER_BIT_DEPTH, bitWidth);
                mParams.putInt(PARAMETER_FREQUENCY, sampleRate);
                mParams.putInt(PARAMETER_CHANNELS, channels);
            }
            else
            {
                mOutputWareHeader = new WaveHeader();

                int bitWidth = mParams.getInt(PARAMETER_BIT_DEPTH);
                int sampleRate = mParams.getInt(PARAMETER_FREQUENCY);
                int channels = mParams.getInt(PARAMETER_CHANNELS);

                mOutputWareHeader.setBitsPerSample((short)bitWidth);
                mOutputWareHeader.setSampleRate(sampleRate);
                mOutputWareHeader.setNumChannels((short)channels);
            }
        }
        catch (Exception e)
        {
            result = new SetParameterResult(false, -1, e.getLocalizedMessage());
        }

        return result;
    }


    ////
    public static class WaveHeader
    {
        // follows WAVE format in http://ccrma.stanford.edu/courses/422/projects/WaveFormat
        private static final String TAG = "WaveHeader";

        private static final int HEADER_LENGTH = 44;

        public static final short FORMAT_PCM = 1;
        public static final short FORMAT_ALAW = 6;
        public static final short FORMAT_ULAW = 7;

        private short mFormat;
        private short mNumChannels;
        private int mSampleRate;
        private short mBitsPerSample;
        private int mNumBytes;

        //
        public WaveHeader()
        {
        }

        public WaveHeader(short format, int sampleRage, short channels, short bitsPerSample, int numBytes)
        {
            this.mFormat = format;
            this.mSampleRate = sampleRage;
            this.mNumChannels = channels;
            this.mBitsPerSample = bitsPerSample;
            this.mNumBytes = numBytes;
        }

        public short getFormat()
        {
            return mFormat;
        }
        public void setFormat(short value)
        {
            mFormat = value;
        }

        public short getNumChannels()
        {
            return mNumChannels;
        }
        public void setNumChannels(short value)
        {
            mNumChannels = value;
        }

        public int getSampleRate()
        {
            return mSampleRate;
        }
        public void setSampleRate(int value)
        {
            mSampleRate = value;
        }

        public short getBitsPerSample()
        {
            return mBitsPerSample;
        }
        public void setBitsPerSample(short value)
        {
            mBitsPerSample = value;
        }

        public int getNumBytes()
        {
            return mNumBytes;
        }
        public void setNumBytes(int value)
        {
            mNumBytes = value;
        }

        public int read(InputStream in) throws IOException
        {
            /* RIFF header */
            readId(in, "RIFF");
            int numBytes = readInt(in) - 36;
            readId(in, "WAVE");

            /* fmt chunk */
            readId(in, "fmt ");
            if (16 != readInt(in)) throw new IOException("fmt chunk length not 16");
            mFormat = readShort(in);
            mNumChannels = readShort(in);
            mSampleRate = readInt(in);
            int byteRate = readInt(in);
            short blockAlign = readShort(in);
            mBitsPerSample = readShort(in);
            if (byteRate != mNumChannels * mSampleRate * mBitsPerSample / 8) {
                throw new IOException("fmt.ByteRate field inconsistent");
            }
            if (blockAlign != mNumChannels * mBitsPerSample / 8) {
                throw new IOException("fmt.BlockAlign field inconsistent");
            }

            /* data chunk */
            readId(in, "data");
            mNumBytes = readInt(in);

            return HEADER_LENGTH;
        }

        public int write(OutputStream paramOutputStream)
                throws IOException
        {
            writeId(paramOutputStream, "RIFF");
            writeInt(paramOutputStream, 36 + this.mNumBytes);
            writeId(paramOutputStream, "WAVE");
            writeId(paramOutputStream, "fmt ");
            writeInt(paramOutputStream, 16);
            writeShort(paramOutputStream, this.mFormat);
            writeShort(paramOutputStream, this.mNumChannels);
            writeInt(paramOutputStream, this.mSampleRate);
            writeInt(paramOutputStream, this.mNumChannels * this.mSampleRate * this.mBitsPerSample / 8);
            writeShort(paramOutputStream, (short) (this.mNumChannels * this.mBitsPerSample / 8));
            writeShort(paramOutputStream, this.mBitsPerSample);
            writeId(paramOutputStream, "data");
            writeInt(paramOutputStream, this.mNumBytes);
            return 44;
        }

        private static void readId(InputStream in, String id) throws IOException {
            for (int i = 0; i < id.length(); i++) {
                if (id.charAt(i) != in.read()) throw new IOException( id + " tag not present");
            }
        }

        private static int readInt(InputStream in) throws IOException
        {
            int a = in.read();
            int b = in.read();
            int c = in.read();
            int d = in.read();

            if (a == -1 || b == -1 || c == -1 || d == -1)
            {
                throw new IOException();
            }

            return a | (b << 8) | (c << 16) | (d << 24);
        }

        private static short readShort(InputStream in) throws IOException
        {
            int a = in.read();
            int b = in.read();

            if (a == -1 || b == -1)
            {
                throw new IOException();
            }

            return (short)(a | (b << 8));
        }

        private static void writeId(OutputStream paramOutputStream, String paramString)
                throws IOException
        {
            for (int i = 0; i < paramString.length(); i++)
                paramOutputStream.write(paramString.charAt(i));
        }

        private static void writeInt(OutputStream paramOutputStream, int paramInt)
                throws IOException
        {
            paramOutputStream.write(paramInt >> 0);
            paramOutputStream.write(paramInt >> 8);
            paramOutputStream.write(paramInt >> 16);
            paramOutputStream.write(paramInt >> 24);
        }

        private static void writeShort(OutputStream paramOutputStream, short paramShort)
                throws IOException
        {
            paramOutputStream.write(paramShort >> 0);
            paramOutputStream.write(paramShort >> 8);
        }


        @Override
        public String toString()
        {
            return String.format(
                    "WaveHeader format=%d numChannels=%d sampleRate=%d bitsPerSample=%d numBytes=%d",
                    mFormat, mNumChannels, mSampleRate, mBitsPerSample, mNumBytes);
        }
    }
}
