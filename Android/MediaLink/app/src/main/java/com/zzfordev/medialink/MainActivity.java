package com.zzfordev.medialink;

import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.zzfordev.medialink.nodes.audio.AudioInputNode;
import com.zzfordev.medialink.nodes.audio.AudioOutputNode;
import com.zzfordev.medialink.nodes.Node;
import com.zzfordev.medialink.nodes.PcmReadNode;
import com.zzfordev.medialink.nodes.PcmWriteNode;
import com.zzfordev.medialink.nodes.SoundTouchNode;
import com.zzfordev.medialink.ui.LevelChanger;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private Node mSoundTouchNode;
    private PcmReadNode mPcmReadWriteNode;

    private short[] mPcmBuff;


    //
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own actionaaa", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //
        Global.application = getApplication();

        //
        initUI();

        //
        final int sampleRate = 16000;
        final int channels = 2;
        final int bitsPerSample = 16;
        final int pcmRecordDuration = 10; //seconds

        mPcmBuff = new short[sampleRate * channels * pcmRecordDuration];


        //
        initNodeTestCase0(sampleRate,bitsPerSample,channels);

        //
        initNodeTestCase2(sampleRate,bitsPerSample,channels);
        //
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static {
        System.loadLibrary("medialink");
    }

    private void initUI()
    {
        final LevelChanger pitchLevelChanger = (LevelChanger)findViewById(R.id.pitch_levelchanger);
        final LevelChanger semiPitchLevelChanger = (LevelChanger)findViewById(R.id.semipitch_levelchanger);
        final LevelChanger octavPitchLevelChanger = (LevelChanger)findViewById(R.id.octavpitch_levelchanger);

        final LevelChanger rateLevelChanger = (LevelChanger)findViewById(R.id.rate_levelchanger);
        final LevelChanger tempoLevelChanger = (LevelChanger)findViewById(R.id.tempo_levelchanger);
        final LevelChanger rateChangeLevelChanger = (LevelChanger)findViewById(R.id.ratechange_levelchanger);
        final LevelChanger tempoChangeLevelChanger = (LevelChanger)findViewById(R.id.tempochange_levelchanger);

        //
        pitchLevelChanger.setParams(0,10,1,1);
        pitchLevelChanger.setOnValueChangedListener(new OnLevelChangerValueChangedListener(SoundTouchNode.PARAMETER_SOUNDTOUCH_PITCH));

        semiPitchLevelChanger.setParams(-12,12,0,1);
        semiPitchLevelChanger.setOnValueChangedListener(new OnLevelChangerValueChangedListener(SoundTouchNode.PARAMETER_SOUNDTOUCH_SEMITONES));

        octavPitchLevelChanger.setParams(-1.0f, 1.0f, 0.0f, 0.01f);
        octavPitchLevelChanger.setOnValueChangedListener(new OnLevelChangerValueChangedListener(SoundTouchNode.PARAMETER_SOUNDTOUCH_OCTAVES));

        rateLevelChanger.setParams(0,10,1,1);
        rateLevelChanger.setOnValueChangedListener(new OnLevelChangerValueChangedListener(SoundTouchNode.PARAMETER_SOUNDTOUCH_RATE));

        tempoLevelChanger.setParams(0,10,1,1);
        tempoLevelChanger.setOnValueChangedListener(new OnLevelChangerValueChangedListener(SoundTouchNode.PARAMETER_SOUNDTOUCH_TEMPO));

        rateChangeLevelChanger.setParams(-0.5f, 1.0f, 0, 0.1f);
        rateChangeLevelChanger.setOnValueChangedListener(new OnLevelChangerValueChangedListener(SoundTouchNode.PARAMETER_SOUNDTOUCH_RATECHANGE));

        tempoChangeLevelChanger.setParams(-0.5f, 1.0f, 0, 0.1f);
        tempoChangeLevelChanger.setOnValueChangedListener(new OnLevelChangerValueChangedListener(SoundTouchNode.PARAMETER_SOUNDTOUCH_TEMPOCHANGE));

        //
        final Button defaultButton = (Button)findViewById(R.id.default_button);
        defaultButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pitchLevelChanger.setToDefault();
                semiPitchLevelChanger.setToDefault();
                octavPitchLevelChanger.setToDefault();
                rateLevelChanger.setToDefault();
                tempoLevelChanger.setToDefault();
                rateChangeLevelChanger.setToDefault();
                tempoChangeLevelChanger.setToDefault();
            }
        });
    }

    private void initNodeTestCase0(int sampleRate, int bitsPerSample, int channels)
    {
        final ArrayList<Pair<Node, Parameter>> nodeParametersPairs = new ArrayList<>();

        //
        final Node node0 = new AudioInputNode();
        Parameter node0Params = node0.getParameters();
        node0Params.putInt(Node.PARAMETER_FREQUENCY, sampleRate);
        node0Params.putInt(Node.PARAMETER_BIT_DEPTH, bitsPerSample);
        node0Params.putInt(Node.PARAMETER_CHANNELS, channels);

        nodeParametersPairs.add(new Pair<Node, Parameter>(node0, node0Params));

        //
        final Node node1 = new SoundTouchNode();
        Parameter node1Params = node0.getParameters();
        node1Params.putInt(Node.PARAMETER_FREQUENCY, sampleRate);
        node1Params.putInt(Node.PARAMETER_BIT_DEPTH, bitsPerSample);
        node1Params.putInt(Node.PARAMETER_CHANNELS, channels);
        nodeParametersPairs.add(new Pair<Node, Parameter>(node1, node1Params));

        //
        final Node node2_0 = new AudioOutputNode();
        Parameter node2_0Params = node0.getParameters();
        node2_0Params.putInt(Node.PARAMETER_FREQUENCY, sampleRate);
        node2_0Params.putInt(Node.PARAMETER_BIT_DEPTH, bitsPerSample);
        node2_0Params.putInt(Node.PARAMETER_CHANNELS, channels);
        nodeParametersPairs.add(new Pair<Node, Parameter>(node2_0, node2_0Params));

        //
        final PcmWriteNode node2_1 = new PcmWriteNode(mPcmBuff);

        //
        mSoundTouchNode = node1;

        //
        final ArrayList<Node> nodeLinkStartSequence = new ArrayList<Node>();

        nodeLinkStartSequence.add(node2_0);
        nodeLinkStartSequence.add(node2_1);
        nodeLinkStartSequence.add(node1);
        nodeLinkStartSequence.add(node0);

        final ArrayList<Node> nodeLinkStopSequence = new ArrayList<Node>();

        nodeLinkStopSequence.add(node0);
        nodeLinkStopSequence.add(node1);
        nodeLinkStopSequence.add(node2_0);
        nodeLinkStopSequence.add(node2_1);

        //
        node0.addNextNode(node1);
        node1.addNextNode(node2_0);
        node1.addNextNode(node2_1);

        //
        final Button micToSoundTouchToSpeakerWav = (Button)findViewById(R.id.start_button1);

        micToSoundTouchToSpeakerWav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.setEnabled(false);

                //
                Node.setNodeLinkParameters(nodeParametersPairs.iterator(), new Node.SetParameterListener()
                {
                    @Override
                    public void onFinished(boolean _isOk, int _errorCode, String _errorDescription)
                    {
                        Node.startNodeLink(nodeLinkStartSequence.iterator(), null);
                    }
                });
            }
        });

        final Button stopMicToSoundTouchToSpeakerWav = (Button)findViewById(R.id.stop_button1);
        stopMicToSoundTouchToSpeakerWav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Node.stopNodeLink(nodeLinkStopSequence.iterator(), new Node.StopListener()
                {
                    @Override
                    public void onFinished(boolean _isOk, int _errorCode, String _errorDescription)
                    {
                        Global.postToMainThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                micToSoundTouchToSpeakerWav.setEnabled(true);
                            }
                        });
                    }
                });
            }
        });
    }

    private void initNodeTestCase2(int sampleRate, int bitsPerSample, int channels)
    {
        final Button wavToSpeaker = (Button)findViewById(R.id.start_button2);
        final Button stopWavToSpeaker = (Button)findViewById(R.id.stop_button2);

        //
        final ArrayList<Pair<Node, Parameter>> nodeParametersPairs = new ArrayList<>();

        final Node node0 = new PcmReadNode(mPcmBuff);

        final Node node1 = new AudioOutputNode();
        Parameter node1Params = node0.getParameters();
        node1Params.putInt(Node.PARAMETER_FREQUENCY, sampleRate);
        node1Params.putInt(Node.PARAMETER_BIT_DEPTH, bitsPerSample);
        node1Params.putInt(Node.PARAMETER_CHANNELS, channels);
        nodeParametersPairs.add(new Pair<Node, Parameter>(node1, node1Params));

        //
        node0.setNodeListener(new Node.NodeListener()
        {
            @Override
            public void onDataEnd()
            {
                Global.postToMainThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        wavToSpeaker.setEnabled(true);
                    }
                });
            }
        });

        //
        final ArrayList<Node> nodeLinkStartSequence = new ArrayList<Node>();

        nodeLinkStartSequence.add(node1);
        nodeLinkStartSequence.add(node0);

        final ArrayList<Node> nodeLinkStopSequence = new ArrayList<Node>();

        nodeLinkStopSequence.add(node0);
        nodeLinkStopSequence.add(node1);

        //
        node0.addNextNode(node1);


        wavToSpeaker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.setEnabled(false);

                //
                Node.setNodeLinkParameters(nodeParametersPairs.iterator(), new Node.SetParameterListener()
                {
                    @Override
                    public void onFinished(boolean _isOk, int _errorCode, String _errorDescription)
                    {
                        Node.startNodeLink(nodeLinkStartSequence.iterator(), null);
                    }
                });
            }
        });

        stopWavToSpeaker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Node.stopNodeLink(nodeLinkStopSequence.iterator(), new Node.StopListener()
                {
                    @Override
                    public void onFinished(boolean _isOk, int _errorCode, String _errorDescription)
                    {
                        Global.postToMainThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                wavToSpeaker.setEnabled(true);
                            }
                        });

                    }
                });
            }
        });


    }




    ////
    class OnLevelChangerValueChangedListener implements LevelChanger.OnValueChangedListener
    {
        private String _parameterKey;

        public OnLevelChangerValueChangedListener(String parameterKey)
        {
            _parameterKey = parameterKey;
        }

        @Override
        public void onValueChanged(float value)
        {
            if (mSoundTouchNode != null)
            {
                Parameter parameter = mSoundTouchNode.getParameters();
                parameter.put(_parameterKey, value);
                mSoundTouchNode.setParameters(parameter, null);
            }
        }
    }
}
