package jp.tattyamm.android.checktrend;

import jp.tattyamm.android.checktrend.Entity.Trend;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("api/trend/{name}.json")
    Call<Trend> getTrend(@Path("name") String name);
}
