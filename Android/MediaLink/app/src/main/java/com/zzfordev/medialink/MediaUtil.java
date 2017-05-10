package com.zzfordev.medialink;

import android.media.AudioFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MediaUtil
{
    public static int getAudioFmtPCMEncoding(int bitDepth)
    {
        int result = -1;
        if (bitDepth == 8)
        {
            result = AudioFormat.ENCODING_PCM_8BIT;
        }
        else if (bitDepth == 16)
        {
            result = AudioFormat.ENCODING_PCM_16BIT;
        }
        else if (bitDepth == 32)
        {
            result = AudioFormat.ENCODING_PCM_FLOAT;
        }

        return result;
    }

    public static void fromAssets(android.content.Context context, String relativePathInAssets, File outputFile)
    {
        File result = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            inputStream = context.getAssets().open(relativePathInAssets);
            outputStream = new FileOutputStream(outputFile);

            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0)
            {
                outputStream.write(buffer,0,length);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }

                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (IOException e){}
        }
    }
}
