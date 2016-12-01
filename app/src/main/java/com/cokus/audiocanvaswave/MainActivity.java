package com.cokus.audiocanvaswave;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.cokus.audiocanvaswave.util.MusicSimilarityUtil;
import com.cokus.audiocanvaswave.util.U;
import com.cokus.wavelibrary.draw.WaveCanvas;
import com.cokus.wavelibrary.utils.SamplePlayer;
import com.cokus.wavelibrary.utils.SoundFile;
import com.cokus.wavelibrary.view.WaveSurfaceView;
import com.cokus.wavelibrary.view.WaveformView;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 *@author:cokus
 *@email:czcoku@gmail.com
 */
@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.wavesfv) WaveSurfaceView waveSfv;
    @BindView(R.id.switchbtn) Button switchBtn;
    @BindView(R.id.status)TextView status;
    @BindView(R.id.waveview)WaveformView waveView;
    @BindView(R.id.play)Button playBtn;
    @BindView(R.id.socreaudio)Button scoreBtn;

    private static final int FREQUENCY = 16000;// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private static final int CHANNELCONGIFIGURATION = AudioFormat.CHANNEL_IN_MONO;// 设置单声道声道
    private static final int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;// 音频数据格式：每个样本16位
    public final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;// 音频获取源
    private int recBufSize;// 录音最小buffer大小
    private AudioRecord audioRecord;
    private WaveCanvas waveCanvas;
    private String mFileName = "test";//文件名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        U.createDirectory();
        if(waveSfv != null) {
            waveSfv.setLine_off(42);
            //解决surfaceView黑色闪动效果
            waveSfv.setZOrderOnTop(true);
            waveSfv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }
        waveView.setLine_offset(42);
    }

    @OnClick({R.id.switchbtn,R.id.play,R.id.socreaudio})
    void click(View view){
        switch (view.getId()) {
            case R.id.switchbtn:
            if (waveCanvas == null || !waveCanvas.isRecording) {
                status.setText("录音中...");
                switchBtn.setText("停止录音");
                waveSfv.setVisibility(View.VISIBLE);
                waveView.setVisibility(View.INVISIBLE);
                initAudio();
            } else {
                status.setText("停止录音");
                switchBtn.setText("开始录音");
                waveCanvas.Stop();
                waveCanvas = null;
                initWaveView();
            }
                break;
            case R.id.play:
                   onPlay(0);
                break;
            case R.id.socreaudio:
                float sim = 0;
                try {
                    // new FileInputStream(new File(DATA_DIRECTORY + mFileName + ".wav"))
                    sim = MusicSimilarityUtil.getScoreByCompareFile(getResources().getAssets().open("coku1.wav"), getResources().getAssets().open("coku2.wav"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this,sim+"",Toast.LENGTH_LONG).show();
                break;
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
            Thread.sleep(300);//让文件写入完成后再载入波形 适当的休眠下
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mFile = new File(U.DATA_DIRECTORY + mFileName + ".wav");
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
                            waveSfv.setVisibility(View.INVISIBLE);
                            waveView.setVisibility(View.VISIBLE);
                        }
                    };
                    MainActivity.this.runOnUiThread(runnable);
                }
            }
        };
        mLoadSoundFileThread.start();
    }



    float mDensity;
    /**waveview载入波形完成*/
    private void finishOpeningSoundFile() {
        waveView.setSoundFile(mSoundFile);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;
        waveView.recomputeHeights(mDensity);
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    private void initAudio(){
        recBufSize = AudioRecord.getMinBufferSize(FREQUENCY,
                CHANNELCONGIFIGURATION, AUDIOENCODING);// 录音组件
        audioRecord = new AudioRecord(AUDIO_SOURCE,// 指定音频来源，这里为麦克风
                FREQUENCY, // 16000HZ采样频率
                CHANNELCONGIFIGURATION,// 录制通道
                AUDIO_SOURCE,// 录制编码格式
                recBufSize);// 录制缓冲区大小 //先修改
        waveCanvas = new WaveCanvas();
        waveCanvas.baseLine = waveSfv.getHeight() / 2;
        waveCanvas.Start(audioRecord, recBufSize, waveSfv, mFileName, U.DATA_DIRECTORY, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return true;
            }
        });
    }


    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    void showRationaleForRecord(final PermissionRequest request){
        new AlertDialog.Builder(this)
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("是否开启录音权限")
                .show();
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    void showRecordDenied(){
        Toast.makeText(MainActivity.this,"拒绝录音权限将无法进行挑战",Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO)
    void onRecordNeverAskAgain() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 2016/11/10 打开系统设置权限
                        dialog.cancel();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("您已经禁止了录音权限,是否现在去开启")
                .show();
    }

    private int mPlayStartMsec;
    private int mPlayEndMsec;
    private final int UPDATE_WAV = 100;
    /**播放音频，@param startPosition 开始播放的时间*/
    private synchronized void onPlay(int startPosition) {
        if (mPlayer == null)
            return;
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            updateTime.removeMessages(UPDATE_WAV);
        }
            mPlayStartMsec = waveView.pixelsToMillisecs(startPosition);
            mPlayEndMsec = waveView.pixelsToMillisecsTotal();
            mPlayer.setOnCompletionListener(new SamplePlayer.OnCompletionListener() {
                @Override
                public void onCompletion() {
                    waveView.setPlayback(-1);
                    updateDisplay();
                    updateTime.removeMessages(UPDATE_WAV);
                    Toast.makeText(getApplicationContext(),"播放完成",Toast.LENGTH_LONG).show();
                }
            });
            mPlayer.seekTo(mPlayStartMsec);
            mPlayer.start();
            Message msg = new Message();
            msg.what = UPDATE_WAV;
            updateTime.sendMessage(msg);
    }

    Handler updateTime = new Handler() {
        public void handleMessage(Message msg) {
            updateDisplay();
            updateTime.sendMessageDelayed(new Message(), 10);
        };
    };

    /**更新upd
     * ateview 中的播放进度*/
    private void updateDisplay() {
            int now = mPlayer.getCurrentPosition();// nullpointer
            int frames = waveView.millisecsToPixels(now);
            waveView.setPlayback(frames);//通过这个更新当前播放的位置
            if (now >= mPlayEndMsec ) {
                waveView.setPlayFinish(1);
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.pause();
                    updateTime.removeMessages(UPDATE_WAV);
                }
            }else{
                waveView.setPlayFinish(0);
            }
            waveView.invalidate();//刷新真个视图
    }


}
