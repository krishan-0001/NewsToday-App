package com.example.newstoady

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newstoady.api.Article
import com.example.newstoady.api.Constant
import com.example.newstoady.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException

class NewsViewModel : ViewModel() {
    private val _newsUiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val newsUiState : StateFlow<NewsUiState> = _newsUiState
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
            _newsUiState.value = NewsUiState.Loading
            try {
                val response = RetrofitInstance.api.getTopHeadlines(
                    country = "us",
                    apiKey = Constant.apiKey,
                    category = category
                )
                if (response.isSuccessful) {
                    _newsUiState.value = NewsUiState.Success(response.body()?.articles ?: emptyList())
                    response.body()?.let { newsResponse ->
                        _articles.postValue(newsResponse.articles)
                    }
                } else {
                    _newsUiState.value = NewsUiState.Error("Failed to load news")
                    Log.e("NewsAPI", "Error: ${response.code()}")
                }

            } catch (e: Exception) {
                _newsUiState.value = NewsUiState.Error("Failed to load news")
                Log.e("NewsAPI", "Exception: ${e.message}")
            }
            catch (e: IOException){
                _newsUiState.value = NewsUiState.Error("No internet connection")

            }
            _isLoading.postValue(false)
        }
    }

}