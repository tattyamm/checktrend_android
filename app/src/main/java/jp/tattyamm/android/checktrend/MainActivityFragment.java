package jp.tattyamm.android.checktrend;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

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

        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(getActivity(), "1 url");
            }
        });

        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadView(getActivity(), "2 url");
            }
        });
    }

    public void reloadView(FragmentActivity fActivity, String url) {
        TextView textView = (TextView)fActivity.findViewById(R.id.textview);
        textView.setText(url);

        Toast.makeText(fActivity, url, Toast.LENGTH_SHORT).show();

        requestJson(fActivity);
    }

    //http://dev.classmethod.jp/smartphone/android/android-tips-51-volley/
    private void requestJson(FragmentActivity fActivity) {
        final TextView textView = (TextView)fActivity.findViewById(R.id.textview);

        // Volley でリクエスト
        String url = "http://checktrend.herokuapp.com/api/trend/google.json";
        mQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "response : " + response.toString());
                        textView.setText(response.toString());
                    }
                }, null));
        mQueue.start();
    }

}
