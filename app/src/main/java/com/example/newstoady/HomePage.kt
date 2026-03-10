package com.example.newstoady

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.newstoady.api.Article
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterialApi::class)
@Composable



fun HomePage(newsViewModel: NewsViewModel){

    val articles by newsViewModel.articles.observeAsState(emptyList())
    val isLoading by newsViewModel.isLoading.observeAsState(false)
    val refreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = {newsViewModel.fetchNewsTopHeadlines()})

        Column() {
            CategoryRow(newsViewModel)
            Box(modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)){
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(articles){article->
                        ArticleItem(article)
                    }
                }
                PullRefreshIndicator(
                    refreshing = isLoading,
                    state = refreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                if(isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

}

@Composable
fun ArticleItem(article: Article){
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .clickable{
                article.url?.let {url->
//                    val intent = Intent(Intent.ACTION_VIEW,Uri.parse(url))
//                    context.startActivity(intent)
                    val customTabsIntent = CustomTabsIntent.Builder()
                        .build()
                    customTabsIntent.launchUrl(context, Uri.parse(url))

                }
            },
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column() {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = "Content Image",
                modifier = Modifier.fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = article.title ?: "No title available",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                    )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = article.description ?: "No description available",
                    fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = formatDate(article.publishedAt),
                    fontSize = 12.sp)

            }
        }
    }
}

fun formatDate(date : String?) : String{
    if(date==null){
        return ""
    }
    return try {
        val parser = java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            java.util.Locale.getDefault()
        )
        parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val dateObj = parser.parse(date)
        val formatter = java.text.SimpleDateFormat(
            "dd MMM yyyy - hh:mm a",
            java.util.Locale.getDefault()
        )
        formatter.format(dateObj!!)
    }
    catch (e : Exception){
        date
    }
}
@Composable
fun CategoryRow(newsViewModel: NewsViewModel) {

    val categories = listOf(
        "general",
        "business",
        "technology",
        "sports",
        "health",
        "science",
        "entertainment"
    )

    var selectedCategory by remember { mutableStateOf("general") }

    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {

        items(categories) { category ->

            Text(
                text = category.uppercase(),
                modifier = Modifier
                    .padding(12.dp)
                    .clickable {

                        selectedCategory = category
                        newsViewModel.fetchNewsTopHeadlines(category)

                    }
            )

        }

    }
}
