package com.nahin.imageupload;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ImageShower extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_image_shower );
        getSupportActionBar().hide();

    }
}
