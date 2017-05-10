package com.zzfordev.medialink.nodes;

import android.util.Pair;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.zzfordev.medialink.Parameter;
import com.zzfordev.medialink.nodes.audio.AudioNode;

abstract public class Node
{
    public static final int DATA_TYPE_SHORT = 0;
    public static final int DATA_TYPE_BYTE = 1;
    public static final int DATA_TYPE_FLOAT = 2;

    // parameters keys
    public static final String PARAMETER_FREQUENCY = "parameter_frequency";
    public static final String PARAMETER_BIT_DEPTH = "parameter_bit_depth";
    public static final String PARAMETER_CHANNELS = "parameter_channels";

    // node status
    private final static int NODE_STATE_STARTED = 0;
    private final static int NODE_STATE_STOPPED = 1;
    private final static int NODE_STATE_PUSHING = 2;
    private final static int NODE_STATE_PUSHING_STOPPED = 4;

    //
    protected final ArrayList<Node> mLastNodes = new ArrayList<>();
    protected final ArrayList<Node> mNextNodes = new ArrayList<>();

    //
    protected final Parameter mParams = new Parameter();
    protected NodeListener mNodeListener;
    private StopListener mStopListener;
    private Integer mNodeStatus = NODE_STATE_STOPPED;
    private boolean mStopPush;

    //
    public static class Result
    {
        public Result(boolean _isOk, int _errorCode, String _errorDescription)
        {
            isOk = _isOk;
            errorCode = _errorCode;
            errorDescription = _errorDescription;
        }

        public boolean isOk;
        public int errorCode;
        public String errorDescription;
    }

    public static class SetParameterResult extends Result
    {
        public boolean needRestartNode;

        public SetParameterResult(boolean _isOk, int _errorCode, String _errorDescription)
        {
            super(_isOk, _errorCode, _errorDescription);
        }
    }

    public static class PushResult extends Result
    {
        public boolean dataEnd;

        public PushResult(boolean _isOk, int _errorCode, String _errorDescription)
        {
            super(_isOk, _errorCode, _errorDescription);
        }
    }


    public interface StartListener
    {
        void onFinished(boolean _isOk, int _errorCode, String _errorDescription);
    }

    public interface StopListener
    {
        void onFinished(boolean _isOk, int _errorCode, String _errorDescription);
    }

    public interface SetParameterListener
    {
        void onFinished(boolean _isOk, int _errorCode, String _errorDescription);
    }

    public interface NodeListener
    {
        void onDataEnd();
    }

    //
    public static void startNodeLink(final Iterator<Node> nodeLink, final StartListener listener)
    {
        if (nodeLink.hasNext())
        {
            Node node = nodeLink.next();
            node.start(new StartListener()
            {
                @Override
                public void onFinished(boolean _isOk, int _errorCode, String _errorDescription)
                {
                    startNodeLink(nodeLink, listener);
                }
            });
        }
        else
        {
            if (listener != null)
            {
                listener.onFinished(true, 0, "");
            }
        }
    }

    public static void stopNodeLink(final Iterator<Node> nodeLink, final StopListener listener)
    {
        if (nodeLink.hasNext())
        {
            Node node = nodeLink.next();
            node.stop(new StopListener()
            {
                @Override
                public void onFinished(boolean _isOk, int _errorCode, String _errorDescription)
                {
                    stopNodeLink(nodeLink, listener);
                }
            });
        }
        else
        {
            if (listener != null)
            {
                listener.onFinished(true, 0, "");
            }
        }
    }

    public static void setNodeLinkParameters(final Iterator<Pair<Node, Parameter>> nodeLinkParameterPairs, final SetParameterListener listener)
    {
        if (nodeLinkParameterPairs.hasNext())
        {
            Pair nodeParamPair = nodeLinkParameterPairs.next();
            Node node = (Node)nodeParamPair.first;
            Parameter params = (Parameter)nodeParamPair.second;

            node.setParameters(params, new SetParameterListener()
            {
                @Override
                public void onFinished(boolean _isOk, int _errorCode, String _errorDescription)
                {
                    setNodeLinkParameters(nodeLinkParameterPairs, listener);
                }
            });
        }
        else
        {
            if (listener != null)
            {
                listener.onFinished(true, 0, "");
            }
        }

    }

    //
    abstract protected Result onStart();
    abstract protected PushResult onPush();
    abstract protected Result onStop();
    abstract protected SetParameterResult onSetParameters(Parameter params);
    abstract protected boolean onIsSourceNode();

    //
    public final void setParameters(Parameter params, final SetParameterListener listener)
    {
        mParams.putAll(params);

        //
        SetParameterResult result = onSetParameters(params);

        //
        boolean invokeListener = true;

        boolean isOk = true;
        int errCode = 0;
        String errMsg = null;

        //
        if (result != null)
        {
            isOk = result.isOk;
            errCode = result.errorCode;
            errMsg = result.errorDescription;

            if (result.isOk && result.needRestartNode)
            {
                boolean canRestart = false;

                synchronized (mNodeStatus)
                {
                    if (containStatus(NODE_STATE_STARTED))
                    {
                        canRestart = true;
                    }
                }

                if (canRestart)
                {
                    invokeListener = false;

                    stop(new Node.StopListener()
                    {
                        @Override
                        public void onFinished(boolean _isOk, int _errorCode, String _errorDescription)
                        {
                            // 2. start again
                            start(new Node.StartListener()
                            {
                                @Override
                                public void onFinished(boolean _isOk, int _errorCode, String _errorDescription)
                                {
                                    if (listener != null)
                                    {
                                        listener.onFinished(_isOk, _errorCode, _errorDescription);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }

        //
        if (invokeListener)
        {
            if (listener != null)
            {
                listener.onFinished(isOk, errCode, errMsg);
            }
        }
    }

    //
    public final void start(final StartListener listener)
    {
        boolean needQuit = false;

        synchronized (mNodeStatus)
        {
            needQuit =  (containStatus(NODE_STATE_STARTED));
        }

        //
        if(needQuit && listener != null)
        {
            listener.onFinished(true, 0, "");
            return;
        }

        //
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Result result = onStart();

                boolean ok = true;
                String errMsg = null;
                int errCode = 0;

                if (result != null)
                {
                    ok = result.isOk;
                    errMsg = result.errorDescription;
                    errCode = result.errorCode;
                }

                if (listener != null)
                {
                    listener.onFinished(ok, errCode, errMsg);
                }

                if (!ok)
                {
                    return;
                }

                //
                synchronized (mNodeStatus)
                {
                    setStatus(NODE_STATE_STARTED);
                }

                //
                if (onIsSourceNode())
                {
                    PushResult pushResult = null;

                    mStopPush = false;

                    do
                    {
                        synchronized (mNodeStatus)
                        {
                            addStatus(NODE_STATE_PUSHING);
                        }

                        pushResult = onPush();
                    }
                    while( ! mStopPush && (pushResult == null || ! pushResult.dataEnd));

                    //
                    synchronized (mNodeStatus)
                    {
                        if(mStopPush)
                        {
                            setStatus(NODE_STATE_STOPPED);
                        }
                        else
                        {
                            setStatus(NODE_STATE_PUSHING_STOPPED);
                        }
                    }

                    //
                    if (mStopListener != null)
                    {
                        mStopListener.onFinished(true, 0, "");
                        mStopListener = null;
                    }

                    if (pushResult != null && pushResult.dataEnd)
                    {
                        if (mNodeListener != null)
                        {
                            mNodeListener.onDataEnd();
                        }
                    }

                }
            }
        });
        t.start();
    }

    public final void stop(final StopListener listener)
    {
        boolean needQuit = false;
        Result result = null;

        synchronized (mNodeStatus)
        {
            needQuit = (containStatus(NODE_STATE_STOPPED));
        }

        //
        if(needQuit && listener != null)
        {
            listener.onFinished(true, 0, "");
            return;
        }

        //
        boolean invokeListener = false;

        synchronized (mNodeStatus)
        {
            if (containStatus(NODE_STATE_PUSHING))
            {
                mStopListener = listener;
                mStopPush = true;
            }
            else
            {
                invokeListener = true;
                result = onStop();
                setStatus(NODE_STATE_STOPPED);
            }
        }

        //
        if (invokeListener && listener != null)
        {
            boolean ok = true;
            String errMsg = null;
            int errCode = 0;

            if (result != null)
            {
                ok = result.isOk;
                errMsg = result.errorDescription;
                errCode = result.errorCode;
            }

            listener.onFinished(ok,errCode,errMsg);
        }
    }

    //
    public final Parameter getParameters()
    {
        return mParams;
    }

    public final void setNodeListener(NodeListener listener)
    {
        mNodeListener = listener;
    }

    public Result push(short[] data, long length)
    {
        return new Result(false, -1, "not support short[] data");
    }

    public Result push(float[] data, long length)
    {
        return new Result(false, -1, "not support float[] data");
    }

    final public void addNextNode(Node node)
    {
        mNextNodes.add(node);
        node.mLastNodes.add(this);
    }

    //
    final protected void pushToNext(short[] data, long length)
    {
        for (Node node : mNextNodes)
        {
            node.push(data,length);
        }
    }

    final protected void pushToNext(float[] data, long length)
    {
        for (Node node : mNextNodes)
        {
            node.push(data,length);
        }
    }

    //
    private boolean containStatus(int value)
    {
        return (mNodeStatus & value) > 0;
    }

    private void addStatus(int value)
    {
        mNodeStatus = mNodeStatus | value;
    }

    private void setStatus(int value)
    {
        mNodeStatus = value;
    }
}
