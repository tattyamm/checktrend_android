package jp.tattyamm.android.checktrend.Entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Detail {

    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("items")
    private List<Article> article;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public List<Article> getArticle() {
        return article;
    }

}
