package jp.tattyamm.android.checktrend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import android.widget.AdapterView.OnItemClickListener;

public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    private RequestQueue mQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        //google analytics
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
        final Tracker tracker = analytics.newTracker("UA-XXXX-Y");
        tracker.setScreenName("android main activity");

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
                sendTrackerButton(tracker, "button01");
                reloadView(getActivity(), getString(R.string.view_title01), getString(R.string.trendurl01));
            }
        });
        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTrackerButton(tracker, "button02");
                reloadView(getActivity(), getString(R.string.view_title02), getString(R.string.trendurl02));
            }
        });
        button03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTrackerButton(tracker, "button03");
                reloadView(getActivity(), getString(R.string.view_title03), getString(R.string.trendurl03));
            }
        });
        button04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTrackerButton(tracker, "button04");
                reloadView(getActivity(), getString(R.string.view_title04), getString(R.string.trendurl04));
            }
        });
    }

    public void reloadView(FragmentActivity fActivity, String viewTitle, String url) {
        TextView textView = (TextView)fActivity.findViewById(R.id.textview);
        textView.setText(viewTitle);
        requestJson(fActivity, url);
    }

    private void requestJson(FragmentActivity fActivity, String url) {
        final TextView textView = (TextView)fActivity.findViewById(R.id.textview);
        final ListView listView = (ListView) fActivity.findViewById(R.id.listview);

        final ProgressDialog pDialog = new ProgressDialog(fActivity);
        pDialog.setMessage(getString(R.string.message_loading));
        pDialog.show();

        // Volley でリクエスト
        mQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.hide();

                        //jsonにして、テーブル表示
                        String temp = "";
                        final ArrayList<String> listTitle = new ArrayList<>();
                        final ArrayList<String> listLink = new ArrayList<>();
                        try {
                            JSONArray items = response.getJSONObject("value").getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject data = items.getJSONObject(i);
                                String title = data.getString("title");
                                String link = data.getString("link");
                                temp = temp + "[" + title + "]" + "(" + link + ")";
                                listTitle.add(title);
                                listLink.add(link);
                            }
                        } catch (JSONException e) {
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
                        listView.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                jumpToBrowser(listLink.get(position));
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();

                        //listに表示
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                getActivity(), android.R.layout.simple_list_item_1, Arrays.asList(getString(R.string.message_error_loading_retry))
                        );
                        listView.setAdapter(arrayAdapter);

                    }
                }
        ));
        mQueue.start();
    }

    public void jumpToBrowser(String urlText) {
        Uri uri = Uri.parse(urlText);
        Intent i = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(i);
    }

    public void sendTrackerButton(Tracker tracker, String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Button")
                .setAction("click")
                .setLabel(label)
                .build());
    }
}
