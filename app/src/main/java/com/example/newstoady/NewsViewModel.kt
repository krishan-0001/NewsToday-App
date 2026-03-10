package com.example.newstoady

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newstoady.api.Article
import com.example.newstoady.api.Constant
import com.example.newstoady.api.RetrofitInstance
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading
    private val api = RetrofitInstance.api
    private val _articles = MutableLiveData<List<Article>>()
    val articles : LiveData<List<Article>> = _articles
    init {
        fetchNewsTopHeadlines()
    }
    fun fetchNewsTopHeadlines(category: String = "general") {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {

                val response = RetrofitInstance.api.getTopHeadlines(
                    country = "us",
                    apiKey = Constant.apiKey,
                    category = category
                )

                if (response.isSuccessful) {

                    response.body()?.let { newsResponse ->
                        _articles.postValue(newsResponse.articles)
                    }

                } else {

                    Log.e("NewsAPI", "Error: ${response.code()}")

                }

            } catch (e: Exception) {

                Log.e("NewsAPI", "Exception: ${e.message}")

            }
            _isLoading.postValue(false)
        }
    }

}