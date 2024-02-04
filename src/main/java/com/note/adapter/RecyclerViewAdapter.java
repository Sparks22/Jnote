package com.note.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.note.activity.CheckActivity;
import com.note.activity.EditActivity;
import com.note.database.CURD;
import com.note.jin.MainActivity;
import com.note.database.Note;
import com.note.jin.R;

import java.util.ArrayList;
import java.util.List;


//recyclerView适配器
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Note> noteList;
    private Context context;
    final String TAG = "test";
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOADER_MORE = 1;


    private OnPullLoaderListener mUpPullLoaderListener;

    public RecyclerViewAdapter(List<Note> noteList, Context context) {
        this.noteList = noteList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
/*        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new MyViweHoder(view);
        }*/
        return new MyViweHoder(View.inflate(parent.getContext(),R.layout.item_list,null));

       // return new FooterHolder(View.inflate(parent.getContext(), R.layout.list_footer, null));


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
     //   if (holder instanceof MyViweHoder && getItemViewType(position) == TYPE_NORMAL) {

            Note note = noteList.get(position);
            ((MyViweHoder) holder).mTitleTv.setText(note.getTitle());
            ((MyViweHoder) holder).mContentTv.setText(note.getContent());
            ((MyViweHoder) holder).mTga.setText(note.getTag());
            ((MyViweHoder) holder).mTime.setText(note.getTime());
            //长按卡片删除
            ((MyViweHoder) holder).mRootViewCard.setOnLongClickListener(v -> {

                new AlertDialog.Builder(context).setMessage("确定删除笔记？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // START THE GAME!

                                CURD curd = new CURD(context);
                                if(curd.delete(note.getId())){
                                    noteList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemChanged(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "已删除" + position, Toast.LENGTH_SHORT).show();
                                }



                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog

                            }
                        }).create().show();

                return false;
            });
            ((MyViweHoder) holder).mRootViewCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,CheckActivity.class);
                    intent.putExtra(CheckActivity.POST_CHECK,note.getId()+"");
                   // startActivity(intent);
                    context.startActivity(intent);

                }
            });
     //   }

 /*       else if (holder instanceof FooterHolder && getItemViewType(position) == TYPE_LOADER_MORE) {
                    ((FooterHolder)holder).update(TYPE_NORMAL);

        }*/


    }

    @Override
    public int getItemViewType(int position) {
        return position == noteList.size() - 1 ? TYPE_LOADER_MORE : TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return noteList != null ? noteList.size() : 0;
    }

    public void setOnPullLoaderListener(OnPullLoaderListener listener) {
            this.mUpPullLoaderListener = listener;
    }
    public interface OnPullLoaderListener{
        void onUpPullLoader(FooterHolder footerHolder);
    }


    public class MyViweHoder extends RecyclerView.ViewHolder {
        private TextView mTitleTv;
        private TextView mContentTv;
        private TextView mTga;
        private TextView mTime;
        ConstraintLayout mRootViewCard;

        public MyViweHoder(@NonNull View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.itemTextViewTitle);
            mContentTv = itemView.findViewById(R.id.itemTextViewContent);
            mTga = itemView.findViewById(R.id.itemTextViewTag);
            mTime = itemView.findViewById(R.id.itemTextViewTime);

            mRootViewCard = itemView.findViewById(R.id.card);
        }
    }

    public class FooterHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public FooterHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.loading);
        }

        //提供给外部调用的方法 刷新数据
        public void update(int state) {
            if (state == TYPE_LOADER_MORE) {
                textView.setVisibility(View.VISIBLE);
                if(mUpPullLoaderListener != null){
                    mUpPullLoaderListener.onUpPullLoader(this);
                }
            } else if (state == TYPE_NORMAL) {
                textView.setVisibility(View.GONE);
            }
        }


    }

}













