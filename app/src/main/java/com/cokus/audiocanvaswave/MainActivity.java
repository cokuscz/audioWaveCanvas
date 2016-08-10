package com.cokus.audiocanvaswave;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import com.cokus.wavelibrary.draw.WaveCanvas;
import com.cokus.wavelibrary.utils.SamplePlayer;
import com.cokus.wavelibrary.utils.SoundFile;
import com.cokus.wavelibrary.view.WaveSurfaceView;
import com.cokus.wavelibrary.view.WaveformView;
import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 *@author:cokus
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.wavesfv) WaveSurfaceView waveSfv;
    @BindView(R.id.switchbtn) Button switchBtn;
    @BindView(R.id.status)TextView status;
    @BindView(R.id.waveview)WaveformView waveView;

    private static final int frequency = 16000;// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private static final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;// 设置单声道声道
    private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;// 音频数据格式：每个样本16位
    public final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;// 音频获取源
    private int recBufSize;// 录音最小buffer大小
    private AudioRecord audioRecord;
    private WaveCanvas waveCanvas;
    public static final String DATA_DIRECTORY = Environment
            .getExternalStorageDirectory() + "/record/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(waveSfv != null)
        waveSfv.setLine_off(42);

    }

    @OnClick(R.id.switchbtn)
    void click(){
        //开始录音
        if (waveCanvas == null || !waveCanvas.isRecording) {
            status.setText("录音中...");
            switchBtn.setText("停止录音");
            initAudio();
        }else {
            status.setText("停止录音");
            switchBtn.setText("开始录音");
            initWaveView();
            waveCanvas.Stop();
            waveCanvas = null;
        }
    }

    private void  initWaveView(){
     loadFromFile();
    }

    File mFile;
    Thread mLoadSoundFileThread;
    SoundFile mSoundFile;
    boolean mLoadingKeepGoing;
    SamplePlayer mPlayer;
    /** 载入wav文件显示波形 */
    private void loadFromFile() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mFile = new File(DATA_DIRECTORY + "test.wav");
        mLoadingKeepGoing = true;
        // Load the sound file in a background thread
        mLoadSoundFileThread = new Thread() {
            public void run() {
                try {
                    mSoundFile = SoundFile.create(mFile.getAbsolutePath(),null);
                    if (mSoundFile == null) {
                        return;
                    }
                    mPlayer = new SamplePlayer(mSoundFile);
                } catch (final Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (mLoadingKeepGoing) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            finishOpeningSoundFile();
                        }
                    };

                    mHandler.post(runnable);
                }
            }
        };
        mLoadSoundFileThread.start();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };





    float mDensity;
    /**waveview载入波形完成*/
    private void finishOpeningSoundFile() {
        Log.e("test","complete load wav");
        waveView.setSoundFile(mSoundFile);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;
        waveView.recomputeHeights(mDensity);
    }

    private void initAudio(){
        recBufSize = AudioRecord.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding);// 录音组件
        audioRecord = new AudioRecord(AUDIO_SOURCE,// 指定音频来源，这里为麦克风
                frequency, // 16000HZ采样频率
                channelConfiguration,// 录制通道
                audioEncoding,// 录制编码格式
                recBufSize);// 录制缓冲区大小 //先修改
//        audioRecord.release();

        waveCanvas = new WaveCanvas();
        waveCanvas.baseLine = waveSfv.getHeight() / 2;
        waveCanvas.Start(audioRecord, recBufSize, waveSfv, "test", DATA_DIRECTORY, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                return true;
            }
        });

    }


}
