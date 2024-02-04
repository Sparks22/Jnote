package com.note.jin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.note.activity.EditActivity;
import com.note.activity.MyFtpConfig;
import com.note.adapter.FtpUtils;
import com.note.adapter.RecyclerViewAdapter;
import com.note.database.CURD;
import com.note.database.DataBaseHelper;
import com.note.database.Note;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import java.util.Collections;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private boolean marginTop = true;
    FloatingActionButton etBtn;
    private  int selected = 0;
    private int index = 1;
    final String TAG = "test";
    private RecyclerView recyclerView;
    RecyclerViewAdapter newMyAdapter;
    private CURD curd;
    private List<Note> noteList;
    private DataBaseHelper dataBaseHelper;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Toolbar toolbar;

    private Animation bigAnimation, smallAnimation;

    private ListView listView;
    private Button ch_add;
    private ArrayAdapter<String> adapter;
    private List<String> ch_list;
    private EditText ch_edit;
    private Button ch_qb;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //放大
        bigAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_big);
        //缩小
        smallAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_small);


        ch_qb = findViewById(R.id.ch_qb);
        ch_edit = findViewById(R.id.ch_edit);
        ch_add = findViewById(R.id.ch_add);
        listView = findViewById(R.id.list_view);
        etBtn = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerview);
        swipeRefreshLayout = findViewById(R.id.sfl);

        //侧滑栏
        DrawerLayout dr = findViewById(R.id.dr);
        //toolbar左侧设置菜单按钮
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,dr,toolbar,R.string.app_name,R.string.app_name);
        toggle.syncState();
        //绑定侧滑监听器
        dr.setDrawerListener(toggle);


        //侧滑
        ch_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!ch_edit.getText().toString().trim().equals("")){
                   ch_list.add(ch_edit.getText().toString());
                   ch_edit.setText("");
                   //ch_list.add("新菜单");
                   adapter.notifyDataSetChanged();
               }
            }
        });
        //侧滑栏item点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                noteList = curd.queryAllNoteForTag(ch_list.get(i));
                showListView(false);
              //  Toast.makeText(MainActivity.this,noteList.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        ch_qb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteList = curd.queryAll();
                showListView(false);
            }
        });





        //新建笔记按钮
        etBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putStringArrayListExtra("ch_list", (ArrayList<String>) ch_list);
                activityResultLauncher.launch(intent);
                //  Log.d(TAG, "onClick");
            }
        });


        //recyclerview初始化
        initData();


        //下拉刷新
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新事件要执行的任务

                //添加数据
/*                Note n = new Note();
                n.setTitle("标题" + (noteCount++));
                CURD curd = new CURD(MainActivity.this);
                n.setContent("刷新成功！！！");
                noteList.add(0, n);
*/
                RelativeLayout.LayoutParams top = (RelativeLayout.LayoutParams)toolbar.getLayoutParams();
                top.topMargin=0;
                toolbar.requestLayout();
                //更新UI
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //  newMyAdapter.notifyItemInserted(0);
                        //newMyAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);

            }
        });







        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RelativeLayout.LayoutParams top = (RelativeLayout.LayoutParams)toolbar.getLayoutParams();
               // RelativeLayout.LayoutParams bnrl = (RelativeLayout.LayoutParams)etBtn.getLayoutParams();
                if(newState == RecyclerView.SCROLL_STATE_SETTLING){
                    if(marginTop){
                        top.topMargin=-toolbar.getHeight();
                        toolbar.requestLayout();
                        if(etBtn.getVisibility()==View.VISIBLE){
                            etBtn.startAnimation(smallAnimation);
                            smallAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    etBtn.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }

                    }else{
                        top.topMargin=0;
                        toolbar.requestLayout();
                        if(etBtn.getVisibility()==View.INVISIBLE){
                            etBtn.startAnimation(bigAnimation);
                            etBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RelativeLayout.LayoutParams top = (RelativeLayout.LayoutParams)toolbar.getLayoutParams();
                if(dy<0){//下滑

                    if(top.topMargin<0){
                        top.topMargin+=5;
                        toolbar.requestLayout();
                    }else{
                        top.topMargin=0;
                        toolbar.requestLayout();
                    }
                    marginTop = false;
                }else{

                    if(top.topMargin>-toolbar.getHeight()){
                        top.topMargin+=-5;
                        toolbar.requestLayout();
                    }else{
                        top.topMargin=-toolbar.getHeight();
                        toolbar.requestLayout();
                    }
                    marginTop = true;
                }

            }
        });

    }

    // 返回处理
    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                // 返回处理
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String edit = data.getStringExtra("input");
                        String titler = data.getStringExtra("title");
                        String tag = data.getStringExtra("tag");
                        // Log.d(TAG, edit);
                        if (!edit.trim().isEmpty() && !titler.trim().isEmpty()) {
                            Note nn = new Note();
                            //nn.setTitle("标题" + newMyAdapter.getItemCount());
                            nn.setTitle(titler);
                            nn.setContent(edit);
                            nn.setTag(tag);
                            // noteList.add(0, nn);
                            long newId = curd.insertNote(nn);
                            if (newId != 0) {
                                noteList.add(0, curd.findForID(newId));
                                newMyAdapter.notifyItemInserted(0);
                                newMyAdapter.notifyItemChanged(0);
                                recyclerView.smoothScrollToPosition(0);
                            }

                        }
                    }
                }
            });

    //recyclerview初始化
    public void initData() {


        //数据加载
        dataBaseHelper = new DataBaseHelper(this);
        dataBaseHelper.getWritableDatabase();
        curd = new CURD(MainActivity.this);
        noteList = curd.queryAll();

        //侧滑
        ch_list = curd.queryAllTag();
        showListView(false);

        List<Note> nn = curd.queryAll();
        FtpUtils isf = new FtpUtils(MainActivity.this);
        if (isf.putData(nn)) {//每次打开都保存
             }


    }


    //右上角三个点菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //菜单点击效果
    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {


            case R.id.menu_refresh:



                break;
            case R.id.menu_edit://编辑

                break;
            case R.id.menu_theme://上传云端
                // Android 4.0 之后不能在主线程中请求HTTP请求

                List<Note> nn = curd.queryAll();
                FtpUtils isf = new FtpUtils(MainActivity.this);
                if (isf.putData(nn)) {
                    Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    // Log.d("save", is + "");
                } else {
                    Toast.makeText(MainActivity.this, "失败成功", Toast.LENGTH_SHORT).show();
                }

                new Thread(new Runnable() {
                    CURD c = new CURD(MainActivity.this);
                    List<Note> nn = c.queryAll();

                    @Override
                    public void run() {
                        boolean flag = false;
                        FtpUtils ftpUtils = new FtpUtils(MainActivity.this);
                        FTPClient ftpClient = ftpUtils.getFTPClient(MyFtpConfig.ftpHost, MyFtpConfig.ftpPort, MyFtpConfig.ftpUserName, MyFtpConfig.ftpPassword);
                        try {
                            flag = ftpUtils.uploadFile(ftpClient, "data", new File("data/data/" + MainActivity.this.getPackageName() + "/files/" + ftpUtils.ftpFileName));
                            Log.d("up", flag+"");
                        } catch (IOException e) {
                            Log.d("up", "上传失败:" + e.toString());
                        } finally {
                            ftpUtils.closeFTP(ftpClient);
                            if (flag) {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                Looper.loop();

                            }

                        }
                    }
                }).start();
                break;
            case R.id.menu_quit://云端恢复

                selected = 0;
                List<String> stringList = new ArrayList<>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FtpUtils f = new FtpUtils(MainActivity.this);
                        FTPClient ftp = f.getFTPClient(MyFtpConfig.ftpHost, MyFtpConfig.ftpPort, MyFtpConfig.ftpUserName, MyFtpConfig.ftpPassword);
                        if (ftp != null) {

                            FTPFile[] files = null;
                            // 跳转到文件目录
                            try {
                                ftp.changeWorkingDirectory("data");
                                // 获取目录下文件集合
                                ftp.enterLocalPassiveMode();
                                files = ftp.listFiles();
                            } catch (IOException e) {
                                Log.e(TAG, "downLoadFTP: " + e);
                                e.printStackTrace();
                            }
                            if (files != null) {
                                for (FTPFile file : files) {
                                    if (file.getName().length() > 5) {
                                        stringList.add(file.getName());
                                    }

                                }
                            }
                            Log.d("stringList", stringList.toString());
                            f.closeFTP(ftp);
                        }
                }

                }).start();

                try {
                    int q = 3;
                    //阻塞主线程，等待子线程结果
                    do {
                        Thread.sleep(3000);
                        q--;
                        if(q<0){
                            break;
                        }
                    }while (stringList.size()==0);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String[] strings = stringList.toArray(new String[stringList.size()]);

                AlertDialog dialog =  new AlertDialog.Builder(MainActivity.this)
                        .setTitle("选择备份文件")
                        .setIcon(android.R.drawable.sym_contact_card)
                        .setSingleChoiceItems(strings, selected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                selected = i;
                              //  Log.d("selected","selected-->["+stringList.get(selected)+"]");
                            }
                        })
                        .setPositiveButton("合并", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                File dataf = new File("data/data/" + MainActivity.this.getPackageName() + "/cache/"+ stringList.get(selected));
                                if(!dataf.exists()) {
                                    FtpUtils ftpUtils = new FtpUtils(MainActivity.this);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            FTPClient ftpClient = ftpUtils.getFTPClient(MyFtpConfig.ftpHost, MyFtpConfig.ftpPort, MyFtpConfig.ftpUserName, MyFtpConfig.ftpPassword);
                                            if (ftpClient != null) {
                                                ftpUtils.downLoadFTP(ftpClient, "data", stringList.get(selected), "data/data/" + MainActivity.this.getPackageName() + "/cache");
                                                ftpUtils.closeFTP(ftpClient);
                                            }
                                        }
                                    }).start();

                                    try {
                                        int q = 3;
                                        //阻塞主线程，等待子线程结果
                                        do {
                                            Thread.sleep(3000);
                                            q--;
                                            if (q < 0) {
                                                break;
                                            }
                                        } while (!dataf.exists());
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                ObjectInputStream fos = null;
                                List<Note> notearr = null;
                                try {
                                    fos = new ObjectInputStream(new FileInputStream("data/data/" + MainActivity.this.getPackageName() + "/cache/"+stringList.get(selected)));
                                    notearr = (List<Note>) fos.readObject();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Collections.reverse(notearr);
                                noteList.removeAll(notearr);
                                noteList.addAll(notearr);
                                curd.deleteAll();
                                for (Note note: noteList) {
                                    long newId = curd.insertNoteHaveTime(note);
                                }
                                Collections.reverse(noteList);

                                showListView(false);


                            }
                        })
                        .setNegativeButton("覆盖", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File dataf = new File("data/data/" + MainActivity.this.getPackageName() + "/cache/"+ stringList.get(selected));
                                if(!dataf.exists()) {
                                    FtpUtils ftpUtils = new FtpUtils(MainActivity.this);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            FTPClient ftpClient = ftpUtils.getFTPClient(MyFtpConfig.ftpHost, MyFtpConfig.ftpPort, MyFtpConfig.ftpUserName, MyFtpConfig.ftpPassword);
                                            if (ftpClient != null) {
                                                ftpUtils.downLoadFTP(ftpClient, "data", stringList.get(selected), "data/data/" + MainActivity.this.getPackageName() + "/cache");
                                                ftpUtils.closeFTP(ftpClient);
                                            }
                                        }
                                    }).start();

                                    try {
                                        int q = 3;
                                        //阻塞主线程，等待子线程结果
                                        do {
                                            Thread.sleep(3000);
                                            q--;
                                            if (q < 0) {
                                                break;
                                            }
                                        } while (!dataf.exists());
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                ObjectInputStream fos = null;
                                List<Note> notearr = null;
                                try {
                                    fos = new ObjectInputStream(new FileInputStream("data/data/" + MainActivity.this.getPackageName() + "/cache/"+stringList.get(selected)));
                                    notearr = (List<Note>) fos.readObject();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Collections.reverse(notearr);
                                curd.deleteAll();

                                for (Note note: notearr) {
                                    long newId = curd.insertNoteHaveTime(note);
                                }
                                noteList = curd.queryAll();

                               showListView(false);

                            }
                        })
                        .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                File dataf = new File("data/data/" + MainActivity.this.getPackageName() + "/cache/"+ stringList.get(selected));
                                if(!dataf.exists()) {
                                    FtpUtils ftpUtils = new FtpUtils(MainActivity.this);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            FTPClient ftpClient = ftpUtils.getFTPClient(MyFtpConfig.ftpHost, MyFtpConfig.ftpPort, MyFtpConfig.ftpUserName, MyFtpConfig.ftpPassword);
                                            if (ftpClient != null) {

                                                    // 跳转到文件目录
                                                    try {
                                                        ftpClient.changeWorkingDirectory("data");
                                                        // 获取目录下文件集合
                                                        ftpClient.enterLocalPassiveMode();
                                                      boolean  isDelete = ftpClient.deleteFile(stringList.get(selected));
                                                      if(isDelete){
                                                          selected = -1;
                                                      }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                ftpUtils.closeFTP(ftpClient);
                                            }
                                        }
                                    }).start();


                                    try {
                                            Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    if(selected==-1){
                                        Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                    }



                                }else {
                                    dataf.delete();
                                }


                            }
                        }).create();
               if(stringList.size()!=0){
                   dialog.show();
                   dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(R.color.red);

               }else {
                   Toast.makeText(MainActivity.this, "获取备份失败，请检查网络", Toast.LENGTH_SHORT).show();
               }




/*                //MainActivity.this.getDataDir();-------->data/data/包名/files
                FtpUtils ftpUtils = new FtpUtils(MainActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FTPClient ftpClient = ftpUtils.getFTPClient(MyFtpConfig.ftpHost, MyFtpConfig.ftpPort, MyFtpConfig.ftpUserName, MyFtpConfig.ftpPassword);
                        ftpUtils.downLoadFTP(ftpClient, "data", "data.txt", "data/data/" + MainActivity.this.getPackageName() + "/cache");
                        ftpUtils.closeFTP(ftpClient);
                    }
                }).start();


                ObjectInputStream fos = null;
                List<Note> notearr = null;
                try {
                    fos = new ObjectInputStream(new FileInputStream("data/data/" + MainActivity.this.getPackageName() + "/cache/data.txt"));
                    notearr = (List<Note>) fos.readObject();
                    fos.close();
                    Log.d("openDownload", notearr.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    /***
     * ListView效果
     */
    public void showListView(boolean isBottom) {
        //笔记
        newMyAdapter = new RecyclerViewAdapter(noteList, MainActivity.this);
        recyclerView.setAdapter(newMyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        //浏览方向(垂直)
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //加载方向(从上方加载为false)
        linearLayoutManager.setReverseLayout(isBottom);


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ch_list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }


}

