package jp.tattyamm.android.checktrend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.compose.AppTheme
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                //https://developer.android.com/jetpack/compose/state?hl=ja

                //default
                val mylist =
                    remember { mutableStateListOf(Article(title = resources.getString(R.string.message_loading))) }
                val titletext = remember { mutableStateOf("") }
                val context = LocalContext.current
                getFromAPI(
                    context,
                    resources.getString(R.string.trendurl01),
                    resources.getString(R.string.view_title01),
                    mylist,
                    titletext
                )

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column() {
                        MenuButtonLine(mylist, titletext)
                        TitleBar(titletext)
                        MainListView(mylist)
                    }
                }
            }
        }

    }
}


@Composable
fun MenuButtonLine(mylist: MutableList<Article>, titletext: MutableState<String>) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp),
            onClick = {
                getFromAPI(
                    context,
                    context.getString(R.string.trendurl01),
                    context.getString(R.string.view_title01),
                    mylist,
                    titletext
                )
            },
        ) { Text(context.getString(R.string.trendurl01).replaceFirstChar { it.uppercase() }) }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp),
            onClick = {
                getFromAPI(
                    context,
                    context.getString(R.string.trendurl02),
                    context.getString(R.string.view_title02),
                    mylist,
                    titletext
                )
            },
        ) { Text(context.getString(R.string.trendurl02).replaceFirstChar { it.uppercase() }) }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp),
            onClick = {
                getFromAPI(
                    context,
                    context.getString(R.string.trendurl04),
                    context.getString(R.string.view_title04),
                    mylist,
                    titletext
                )
            },
        ) { Text(context.getString(R.string.trendurl04).replaceFirstChar { it.uppercase() }) }
    }
}

@Composable
fun TitleBar(titletext: MutableState<String>) {
    Column() {
        Text(
            titletext.value,
            fontSize = 20.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
        Divider(
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .width(3.dp)
                .padding(6.dp)
        )
    }
}


/*
@Composable
fun MenuButtonLine(mylist : MutableList<Article>) {
    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        MenuButton("google", mylist)
        MenuButton("rakuten", mylist)
        MenuButton("youtube", mylist)
    }
}

// https://at-sushi.work/blog/51/
// https://engawapg.net/jetpack-compose/888/layout-components/#Modifierweight
@Composable
fun MenuButton(label: String, mylist : MutableList<Article>) {
    Button(
        onClick = {
            getFromAPI(label, mylist)
        },
    ) {
        Text("$label")
    }
}
 */

@Composable
fun MainListView(mylist: MutableList<Article>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(2.dp)
            .verticalScroll(rememberScrollState())
    ) {
        mylist.forEach { article ->
            Column(
                modifier = Modifier
                    .padding(2.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            Log.d("MainActivity", "Click : ${article.title}")
                            openUrl(context, article.link)
                        }
                    )
            ) {
                Text(
                    text = article.title,
                    softWrap = false,
                    fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.description.orEmpty(),
                    softWrap = false,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(2.dp)
                )
            }
        }
    }
}

fun getFromAPI(
    context: Context,
    source_name: String,
    title_name: String,
    mylist: MutableList<Article>,
    titletext: MutableState<String>
) {
    Log.d("MainActivity", "call getFromAPI")

    mylist.clear()
    mylist.add(Article(title = context.getString(R.string.message_loading)))
    titletext.value = title_name

    // Retrofitインスタンスを生成する
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://checktrend-rails.fly.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    // GitHubAPIを定義する
    val trendApi = retrofit.create(TrendAPI::class.java)

    // ユーザー情報を取得する
    val call = trendApi.getTrend(source_name)
    call.enqueue(object : Callback<Trend> {
        override fun onResponse(call: Call<Trend>, response: Response<Trend>) {
            Log.d("MainActivity", "通信レスポンスあり status code=" + response.code().toString())
            // レスポンスが正常に取得された場合
            if (response.isSuccessful) {
                // JSONデータをObjectに変換する
                val trend = response.body()!!
                Log.d("MainActivity", trend.toString())

                //取得した情報を更新

                // log
                Log.d("MainActivity", "トレンドタイトル: ${trend.detail.title}")
                trend.detail.article.forEach { article: Article ->
                    Log.d("MainActivity", "個別タイトル: ${article.title}")
                }

                mylist.clear()
                mylist.addAll(trend.detail.article)
            } else {
                Log.d("MainActivity", "通信レスポンスはあったが失敗")
                mylist.clear()
                mylist.add(Article(title = context.getString(R.string.message_error_loading)))
            }
        }

        override fun onFailure(call: Call<Trend>, t: Throwable) {
            // レスポンスが失敗した場合
            Log.e("MainActivity", "通信エラーが発生しました", t)
            mylist.clear()
            mylist.add(Article(title = context.getString(R.string.message_error_loading_retry)))
        }
    })
}

// https://stackoverflow.com/questions/63801346/composable-invocations-can-only-happen-from-the-context-of-an-composable-funct
// https://stackoverflow.com/questions/58743541/how-to-get-context-in-jetpack-compose
fun openUrl(context: Context, uri: String) {
    Log.d("MainActivity", "open : $uri")
    if (uri.isEmpty()) {
        return
    }
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    ContextCompat.startActivity(context, intent, null)
}

interface TrendAPI {
    @GET("api/trend/{name}.json")
    fun getTrend(@Path("name") name: String): Call<Trend>
}

data class Trend(
    @SerializedName("value")
    val detail: Detail
)

data class Detail(
    val title: String,
    val link: String,
    @SerializedName("items")
    val article: List<Article>
)

data class Article(
    val title: String,
    val link: String = "",
    val pubDate: String = "",
    val description: String = ""
)


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
    }
}
