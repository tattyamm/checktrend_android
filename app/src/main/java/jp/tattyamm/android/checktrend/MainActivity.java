package jp.tattyamm.android.checktrend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.tattyamm.android.checktrend.Entity.Article;
import jp.tattyamm.android.checktrend.Entity.Trend;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.trend_url_base))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiService.class);

        //初回の表示は固定で出す
        reloadView(MainActivity.this, getString(R.string.view_title01), getString(R.string.trendurl01));

        //ボタンが押された時の表示
        Button button01 = (Button)MainActivity.this.findViewById(R.id.selectButton01);
        Button button02 = (Button)MainActivity.this.findViewById(R.id.selectButton02);
        Button button03 = (Button)MainActivity.this.findViewById(R.id.selectButton03);
        Button button04 = (Button)MainActivity.this.findViewById(R.id.selectButton04);

        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(MainActivity.this, getString(R.string.view_title01), getString(R.string.trendurl01));
            }
        });
        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(MainActivity.this, getString(R.string.view_title02), getString(R.string.trendurl02));
            }
        });
        button03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(MainActivity.this, getString(R.string.view_title03), getString(R.string.trendurl03));
            }
        });
        button04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(MainActivity.this, getString(R.string.view_title04), getString(R.string.trendurl04));
            }
        });

        /*
        //debug temp
        Call<Trend> repos = service.getTrend("google");
        repos.enqueue(new Callback<Trend>() {
            @Override
            public void onResponse(Call<Trend> call, Response<Trend> response) {
                Log.d("MyLog", "Hello internet");


                Log.d("MyLog", response.body().getValue().getTitle());
            }

            @Override
            public void onFailure(Call<Trend> call, Throwable t) {
                Log.d("MyLog", "missing");
            }

        });
        */
    }

    public void reloadView(Activity fActivity, String viewTitle, String url) {
        Log.d("tracking", "view: " + viewTitle);
        TextView textView = (TextView)fActivity.findViewById(R.id.textview);
        textView.setText(viewTitle);
        requestJson(fActivity, url);
    }

    private void requestJson(Activity fActivity, String url_path) {
        final TextView textView = (TextView)fActivity.findViewById(R.id.textview);
        final ListView listView = (ListView) fActivity.findViewById(R.id.listview);

        final ProgressDialog pDialog = new ProgressDialog(fActivity);
        pDialog.setMessage(getString(R.string.message_loading));
        pDialog.show();

        // リクエスト
        Call<Trend> call = service.getTrend(url_path);
        call.enqueue(new Callback<Trend>() {
            @Override
            public void onResponse(Call<Trend> call, Response<Trend> response) {
                pDialog.hide();

                final ArrayList<String> listTitle = new ArrayList<>();
                final ArrayList<String> listLink = new ArrayList<>();
                try {
                    Trend container = response.body();
                    Log.d("tracking", "container : " + container.getValue().getTitle());
                    List<Article> articleList = container.getValue().getArticle();
                    for (Article article : articleList) {
                        String title = article.getTitle();
                        String link = article.getLink();
                        listTitle.add(title);
                        listLink.add(link);
                    }
                }  catch (Exception e) {
                    //エラー処理
                    Log.d("tracking", "request error! : " + e.getMessage());

                    listTitle.add(getString(R.string.message_error_loading));
                    listLink.add("https://www.google.co.jp/trends/");
                }

                //listに表示
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                        fActivity, android.R.layout.simple_list_item_1, listTitle
                );
                listView.setAdapter(arrayAdapter);

                //listクリック処理
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        jumpToBrowser(listLink.get(position));
                    }
                });
            }
            @Override
            public void onFailure(Call<Trend> call, Throwable t) {
                Log.d("tracking", "Failed to request");
                //listに表示
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                        fActivity, android.R.layout.simple_list_item_1, Arrays.asList(getString(R.string.message_error_loading_retry))
                );
                listView.setAdapter(arrayAdapter);
            }
        });
    }


    public void jumpToBrowser(String urlText) {
        Uri uri = Uri.parse(urlText);
        Intent i = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(i);
    }

}