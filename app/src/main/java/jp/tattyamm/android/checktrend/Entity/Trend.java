package jp.tattyamm.android.checktrend.Entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trend {

    @SerializedName("value")
    private Detail value;

    public Detail getValue() {
        return value;
    }
}
