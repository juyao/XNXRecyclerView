package com.xncredit.xnxrecyclerview;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jcodecraeer.xrecyclerview.BDXRecyclerView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class MainActivity extends AppCompatActivity {
    BDXRecyclerView mXRecyclerView;
    MyAdapter mMyAdapter;
    int progress;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progress++;
            if(progress<100){
                mXRecyclerView.setUpdateMsg(progress+"%正在更新");
                mHandler.sendEmptyMessageDelayed(0,200);
            }else{
                mXRecyclerView.refreshComplete();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mXRecyclerView = findViewById(R.id.recyclerview);
        mMyAdapter=new MyAdapter();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mXRecyclerView.setLayoutManager(linearLayoutManager);
        mXRecyclerView.setAdapter(mMyAdapter);
        mXRecyclerView.setLoadingListener(new BDXRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                progress=0;
               mHandler.sendEmptyMessageDelayed(0,200);
            }

            @Override
            public void onLoadMore() {

            }
        });




    }
}
