package com.vk.widget;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements  SeekBar.OnSeekBarChangeListener{

    SeekBar seekBar_sur,seekBar_kill,seekBar_defense,seekBar_magic,seekBar_assist,seekBar_money,seekBar_physical;
    PolygonsView mPolygonsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(this.getClass().getName(), "onCreate");
        setContentView(R.layout.content_a);
        init();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(getClass().getName(), "onRestoreInstanceState");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d(getClass().getName(), "onSaveInstanceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getName(), "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getClass().getName(), "onResume");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(getClass().getName(), "onNewIntent");
    }

    void init(){
        mPolygonsView = (PolygonsView)findViewById(R.id.polygonsView);

        seekBar_sur = (SeekBar)findViewById(R.id.survive);
        seekBar_kill = (SeekBar)findViewById(R.id.kill);
        seekBar_defense = (SeekBar)findViewById(R.id.defense);
        seekBar_magic = (SeekBar)findViewById(R.id.magic);
        seekBar_assist = (SeekBar)findViewById(R.id.assist);
        seekBar_money = (SeekBar)findViewById(R.id.money);
        seekBar_physical = (SeekBar)findViewById(R.id.physical);


        seekBar_sur.setOnSeekBarChangeListener(this);
        seekBar_kill.setOnSeekBarChangeListener(this);
        seekBar_defense.setOnSeekBarChangeListener(this);
        seekBar_magic.setOnSeekBarChangeListener(this);
        seekBar_assist.setOnSeekBarChangeListener(this);
        seekBar_money.setOnSeekBarChangeListener(this);
        seekBar_physical.setOnSeekBarChangeListener(this);



        int progress = seekBar_kill.getProgress();
        mPolygonsView.setDefaultStrength(PolygonsView.TYPE_KILL,progress/100.0f);

        progress = seekBar_assist.getProgress();
        mPolygonsView.setDefaultStrength(PolygonsView.TYPE_ASSIST,progress/100.0f);

        progress = seekBar_defense.getProgress();
        mPolygonsView.setDefaultStrength(PolygonsView.TYPE_DEFENSE,progress/100.0f);

        progress = seekBar_magic.getProgress();
        mPolygonsView.setDefaultStrength(PolygonsView.TYPE_MAGIC,progress/100.0f);

        progress = seekBar_money.getProgress();
        mPolygonsView.setDefaultStrength(PolygonsView.TYPE_MONEY,progress/100.0f);

        progress = seekBar_physical.getProgress();
        mPolygonsView.setDefaultStrength(PolygonsView.TYPE_PHYSICAL,progress/100.0f);

        progress = seekBar_sur.getProgress();
        mPolygonsView.setDefaultStrength(PolygonsView.TYPE_SURVIVE,progress/100.0f);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        update(seekBar,progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    void update(SeekBar seekBar, int progress){
        if(seekBar == seekBar_kill)
            mPolygonsView.setStrength(PolygonsView.TYPE_KILL,progress/100.0f);
        if(seekBar == seekBar_assist)
            mPolygonsView.setStrength(PolygonsView.TYPE_ASSIST,progress/100.0f);
        if(seekBar == seekBar_defense)
            mPolygonsView.setStrength(PolygonsView.TYPE_DEFENSE,progress/100.0f);
        if(seekBar == seekBar_magic)
            mPolygonsView.setStrength(PolygonsView.TYPE_MAGIC,progress/100.0f);
        if(seekBar == seekBar_money)
            mPolygonsView.setStrength(PolygonsView.TYPE_MONEY,progress/100.0f);
        if(seekBar == seekBar_physical)
            mPolygonsView.setStrength(PolygonsView.TYPE_PHYSICAL,progress/100.0f);
        if(seekBar == seekBar_sur)
            mPolygonsView.setStrength(PolygonsView.TYPE_SURVIVE,progress/100.0f);
    }
}
