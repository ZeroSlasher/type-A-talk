package com.zeroslasher.typeAtalk;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText mastertext;
    ImageView btnspeech,btntext;
    TextView speed,pitch;
    TextToSpeech mtts;
    SeekBar mSeekBarPitch;
    SeekBar mSeekBarSpeed;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);//hide taskbar
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //hide keyboard on startup
        mastertext = (EditText) findViewById(R.id.mastertext);
        btnspeech = (ImageView) findViewById(R.id.btnspeech);
        btntext = (ImageView) findViewById(R.id.btntext);
        mSeekBarPitch = (SeekBar)findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = (SeekBar)findViewById(R.id.seek_bar_speed);
        speed = (TextView) findViewById(R.id.speed);
        pitch = (TextView) findViewById(R.id.pitch);
        mtts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) { //initilize TTS engine and language
                if (status == TextToSpeech.SUCCESS) {
                    int result = mtts.setLanguage(Locale.US);
                }
            }
        });
        btnspeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pitch.setVisibility(View.INVISIBLE);
                speed.setVisibility(View.INVISIBLE);
                mSeekBarSpeed.setVisibility(View.INVISIBLE);
                mSeekBarPitch.setVisibility(View.INVISIBLE);
                askSpeechInput();
            }
        });
        btntext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String ed_text = mastertext.getText().toString().trim();
                if(ed_text.length() == 0 || ed_text.equals("") || ed_text == null)
                {
                    Toast.makeText(MainActivity.this,"Nothing to Speak",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pitch.setVisibility(View.VISIBLE);
                    speed.setVisibility(View.VISIBLE);
                    mSeekBarSpeed.setVisibility(View.VISIBLE);
                    mSeekBarPitch.setVisibility(View.VISIBLE);
                    speak();
                }
            }
        });
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //call speech input dialog using intent
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hey there..Start Speaking");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a) {
        }
    }
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mastertext.append(result.get(0));
                }
                break;
            }
        }
    }
    private void speak(){
        String textToSpeak = mastertext.getText().toString();
        float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;
        mtts.setPitch(pitch);
        mtts.setSpeechRate(speed);
        mtts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    public void onDestroy() {
        if (mtts != null) {
            mtts.stop();
            mtts.shutdown();
        }
        super.onDestroy();
}
}
