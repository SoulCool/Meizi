package com.example.girl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.girl.R.id.lv;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {


    //@BindView(R.id.lv)
    //ListView lv;
    public static final String TAG = "MainActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    List<GrilBean.ResultsBean> listData;
    Gson mgson = new Gson();
    private MyAdapter adapter;
    private ListView listView;
    boolean isLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);
        listView = (ListView) findViewById(lv);
        listData = new ArrayList<GrilBean.ResultsBean>();
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        loadAsyncRequest();
        listView.setOnScrollListener(this);

        //loadSyncRequest();
    }

    private void loadMoreData() {
        isLoading=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                int index = listData.size()/10 + 1;
                String url = "http://gank.io/api/data/福利/10/"+index;
                Request request = new Request.Builder().get().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        GrilBean grilBean = mgson.fromJson(string, GrilBean.class);
                        listData.addAll(grilBean.getResults());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                isLoading = false;
            }
        }).start();
    }

    private void loadAsyncRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = "http://gank.io/api/data/福利/10/1";
                Request request = new Request.Builder().get().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        GrilBean grilBean = mgson.fromJson(string, GrilBean.class);
                        listData.addAll(grilBean.getResults());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

            }
        }).start();
    }

    //    同步请求
    private void loadSyncRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = "http://gank.io/api/data/福利/10/1";
                Request request = new Request.Builder().get().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.d(TAG, result);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (listView.getLastVisiblePosition() == listData.size() - 1 && !isLoading) {
                loadMoreData();
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(MainActivity.this, R.layout.list_item, null);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_item);
                holder.tvText = (TextView) convertView.findViewById(R.id.tv_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GrilBean.ResultsBean resultBean = listData.get(position);
            holder.tvText.setText(resultBean.getPublishedAt());
            String url = resultBean.getUrl();
            Glide.with(MainActivity.this).load(url).centerCrop().bitmapTransform(new BlurTransformation(MainActivity.this, 25),new CropCircleTransformation(MainActivity.this)).into(holder.ivIcon);
            return convertView;

        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvText;
    }
}
