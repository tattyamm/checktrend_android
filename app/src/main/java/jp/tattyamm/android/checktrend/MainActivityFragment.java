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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import android.widget.AdapterView.OnItemClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
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

        Button button01 = (Button)getActivity().findViewById(R.id.selectButton01);
        Button button02 = (Button)getActivity().findViewById(R.id.selectButton02);
        Button button03 = (Button)getActivity().findViewById(R.id.selectButton03);
        Button button04 = (Button)getActivity().findViewById(R.id.selectButton04);

        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(getActivity(), "Googleトレンド", "http://checktrend.herokuapp.com/api/trend/google.json");
            }
        });
        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(getActivity(), "Yahoo急上昇", "http://checktrend.herokuapp.com/api/trend/yahoo.json");
            }
        });
        button03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(getActivity(), "twitterトレンド", "http://checktrend.herokuapp.com/api/trend/twitter.json");
            }
        });
        button04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(getActivity(), "Amazonランキング", "http://checktrend.herokuapp.com/api/trend/amazon.json");
            }
        });
    }

    public void reloadView(FragmentActivity fActivity, String viewTitle, String url) {
        TextView textView = (TextView)fActivity.findViewById(R.id.textview);
        textView.setText(viewTitle);
        requestJson(fActivity, url);
    }

    //http://dev.classmethod.jp/smartphone/android/android-tips-51-volley/
    private void requestJson(FragmentActivity fActivity, String url) {
        final TextView textView = (TextView)fActivity.findViewById(R.id.textview);
        final ListView listView = (ListView) fActivity.findViewById(R.id.listview);

        final ProgressDialog pDialog = new ProgressDialog(fActivity);
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Volley でリクエスト
        mQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "response : " + response.toString());
                        //textView.setText(response.toString());

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
                            listTitle.add("読み込みエラーが発生しました");
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
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        pDialog.hide();

                        //listに表示
                        String errorMsg = "通信に失敗しました。時間を置いて再度接続してください。";
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                getActivity(), android.R.layout.simple_list_item_1, Arrays.asList(errorMsg)
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

}
