package com.zzfordev.medialink;

import java.util.HashMap;


public class Parameter extends HashMap<String, Object>
{


    //
    public Integer getInt(String key)
    {
        Integer result = null;

        Object value = get(key);

        if(value != null && value instanceof Integer)
        {
            result = (Integer)value;
        }

        return result;
    }

    public Float getFloat(String key)
    {
        Float result = null;

        Object value = get(key);

        if(value != null && value instanceof Float)
        {
            result = (Float)value;
        }

        return result;
    }

    public void putInt(String key, int value)
    {
        Integer object = Integer.valueOf(value);
        put(key, object);
    }

    //
    public String getString(String key)
    {
        String result = null;

        Object value = get(key);

        if(value != null && value instanceof String)
        {
            result = (String)value;
        }

        return result;
    }
}
