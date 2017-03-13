package jp.tattyamm.android.checktrend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.tattyamm.android.checktrend.Entity.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import jp.tattyamm.android.checktrend.Entity.Trend;

public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    private Retrofit retrofit;
    private ApiInterface service;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        //google analytics
        //GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
        //final Tracker tracker = analytics.newTracker("UA-XXXX-Y");
        //tracker.setScreenName("android main activity");

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.trend_url_base))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiInterface.class);

        //初回の表示
        reloadView(getActivity(), getString(R.string.view_title01), getString(R.string.trendurl01));

        //ボタンが押された時の表示
        Button button01 = (Button)getActivity().findViewById(R.id.selectButton01);
        Button button02 = (Button)getActivity().findViewById(R.id.selectButton02);
        Button button03 = (Button)getActivity().findViewById(R.id.selectButton03);
        Button button04 = (Button)getActivity().findViewById(R.id.selectButton04);

        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendTrackerButton(tracker, "button01");
                reloadView(getActivity(), getString(R.string.view_title01), getString(R.string.trendurl01));
            }
        });
        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendTrackerButton(tracker, "button02");
                reloadView(getActivity(), getString(R.string.view_title02), getString(R.string.trendurl02));
            }
        });
        button03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendTrackerButton(tracker, "button03");
                reloadView(getActivity(), getString(R.string.view_title03), getString(R.string.trendurl03));
            }
        });
        button04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendTrackerButton(tracker, "button04");
                reloadView(getActivity(), getString(R.string.view_title04), getString(R.string.trendurl04));
            }
        });
    }

    public void reloadView(FragmentActivity fActivity, String viewTitle, String url) {
        TextView textView = (TextView)fActivity.findViewById(R.id.textview);
        textView.setText(viewTitle);
        requestJson(fActivity, url);
    }

    private void requestJson(FragmentActivity fActivity, String url_path) {
        final TextView textView = (TextView)fActivity.findViewById(R.id.textview);
        final ListView listView = (ListView) fActivity.findViewById(R.id.listview);

        final ProgressDialog pDialog = new ProgressDialog(fActivity);
        pDialog.setMessage(getString(R.string.message_loading));
        pDialog.show();

        // リクエスト
        // https://github.com/cookpad/cookpad-internship-2016-summer/blob/master/android/lesson08.md
        // http://blog.techium.jp/entry/2016/04/10/090000
        Call<Trend> call = service.getTrend(url_path);
        call.enqueue(new Callback<Trend>() {
            @Override
            public void onResponse(Call<Trend> call, Response<Trend> response) {
                pDialog.hide();

                final ArrayList<String> listTitle = new ArrayList<>();
                final ArrayList<String> listLink = new ArrayList<>();
                try {
                    Trend container = response.body();
                    Log.d("tracking", container.getValue().getTitle());
                    List<Article> articleList = container.getValue().getArticle();
                    for (Article article : articleList) {
                        String title = article.getTitle();
                        String link = article.getLink();
                        listTitle.add(title);
                        listLink.add(link);
                    }
                }  catch (Exception e) {
                    //エラー処理
                    listTitle.add(getString(R.string.message_error_loading));
                    listLink.add("https://www.google.co.jp/trends/");
                }

                //listに表示
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                        getActivity(), android.R.layout.simple_list_item_1, listTitle
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
                        getActivity(), android.R.layout.simple_list_item_1, Arrays.asList(getString(R.string.message_error_loading_retry))
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
/*
    public void sendTrackerButton(Tracker tracker, String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Button")
                .setAction("click")
                .setLabel(label)
                .build());
    }
*/
}
