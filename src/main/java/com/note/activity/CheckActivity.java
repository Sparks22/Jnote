package com.note.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.note.database.CURD;
import com.note.database.Note;
import com.note.jin.R;

import java.util.ArrayList;

public class CheckActivity extends AppCompatActivity {
    private TextView title,content,time,tag;
    public static final String POST_CHECK = "CHECK";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        title = findViewById(R.id.tv_title);
        content = findViewById(R.id.tv_content);
        time = findViewById(R.id.check_time);
        tag = findViewById(R.id.check_tag);

       // Log.d("check",getIntent().getStringExtra(POST_CHECK));
        Note note = new CURD(this).findForID(Long.parseLong(getIntent().getStringExtra(POST_CHECK)));
        time.setText(note.getTime());
        tag.setText(note.getTag());
        title.setText(note.getTitle());
        content.setText(note.getContent());






    }

}