package io.github.yedaxia.musicnote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import io.github.yedaxia.musicnote.R;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/4.
 */

public class MetronomeActivity extends BeatSettingActivity{

    public static void launch(Context context){
        Intent intent = new Intent(context,MetronomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnSave.setVisibility(View.GONE);
        tvTune.setVisibility(View.GONE);
        setTitle(R.string.metronome);
    }
}
