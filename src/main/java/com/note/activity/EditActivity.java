package com.note.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.note.database.CURD;
import com.note.jin.MainActivity;
import com.note.jin.R;

import java.util.List;

public class EditActivity extends AppCompatActivity {

    private Spinner spCity = null;
    private ArrayAdapter<CharSequence> adapterCity = null;
    private static String[] cityInfo;
    private EditText et;
    private Button backbtn;
    private EditText titleet;

    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        spCity = findViewById(R.id.edit_spinner);
        et = findViewById(R.id.edittext);
        backbtn = findViewById(R.id.edit_back);
        titleet = findViewById(R.id.et_title);
        Intent i = getIntent();
        List<String> l =  i.getStringArrayListExtra("ch_list");

        //下拉框初始化
        cityInfo = (String[])l.toArray(new String[l.size()]);
        adapterCity = new ArrayAdapter<CharSequence>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,cityInfo);
        spCity.setAdapter(adapterCity);
        //Toast.makeText(EditActivity.this, l.toString(),Toast.LENGTH_SHORT).show();
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tag = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(EditActivity.this,tag,Toast.LENGTH_SHORT).show();

                //判断标题是否为空
                if(titleet.getText().toString().trim().isEmpty()&&!et.getText().toString().trim().isEmpty()){
                    new AlertDialog.Builder(EditActivity.this).setMessage("标题为空，请输入标题？")
                            .setPositiveButton("写个标题", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // START THE GAME!
                                }
                            })
                            .setNegativeButton("不写了", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    finish();
                                }
                            }).create().show();
                }else{

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent.putExtra("input", et.getText().toString()));
                    setResult(RESULT_OK, intent.putExtra("title", titleet.getText().toString()));
                    setResult(RESULT_OK, intent.putExtra("tag", tag));
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            //判断标题是否为空
            if(titleet.getText().toString().trim().isEmpty()&&!et.getText().toString().trim().isEmpty()){
                new AlertDialog.Builder(EditActivity.this).setMessage("标题为空，请输入标题？")
                        .setPositiveButton("写个标题", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // START THE GAME!
                            }
                        })
                        .setNegativeButton("不写了", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                finish();
                            }
                        }).create().show();
            }else{

                Intent intent = new Intent();
                setResult(RESULT_OK, intent.putExtra("input", et.getText().toString()));
                setResult(RESULT_OK, intent.putExtra("title", titleet.getText().toString()));
                setResult(RESULT_OK, intent.putExtra("tag", tag));
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}