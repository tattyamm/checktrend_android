package jp.tattyamm.android.checktrend.Entity;


import com.google.gson.annotations.SerializedName;

public class Article {
    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("pubDate")
    private String pubDate;

    @SerializedName("description")
    private String description;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }
}
