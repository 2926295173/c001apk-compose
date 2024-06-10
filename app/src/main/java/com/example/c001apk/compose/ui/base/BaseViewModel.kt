package com.example.c001apk.compose.ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.compose.constant.Constants.entityTemplateList
import com.example.c001apk.compose.constant.Constants.entityTypeList
import com.example.c001apk.compose.logic.model.HomeFeedResponse
import com.example.c001apk.compose.logic.state.FooterState
import com.example.c001apk.compose.logic.state.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by bggRGjQaUbCoE on 2024/6/10
 */
abstract class BaseViewModel : ViewModel() {

    init {
        fetchData()
    }

    var isRefreshing by mutableStateOf(false)
        private set

    var loadingState by mutableStateOf<LoadingState<List<HomeFeedResponse.Data>>>(LoadingState.Loading)
        private set

    var footerState by mutableStateOf<FooterState>(FooterState.Success)
        private set

    var page = 1
    var firstLaunch = 1
    private var isLoadMore = false
    var isEnd = false
    private var firstItem: String? = null
    var lastItem: String? = null

    abstract suspend fun customFetchData(): Flow<LoadingState<List<HomeFeedResponse.Data>>>

    private fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            customFetchData().collect { result ->
                when (result) {
                    LoadingState.Empty -> {
                        if (loadingState is LoadingState.Success)
                            footerState = FooterState.End
                        else
                            loadingState = result
                        isEnd = true
                    }

                    is LoadingState.Error -> {
                        if (loadingState is LoadingState.Success)
                            footerState = FooterState.Error(result.errMsg)
                        else
                            loadingState = result
                        isEnd = true
                    }

                    LoadingState.Loading -> {
                        if (loadingState is LoadingState.Success)
                            footerState = FooterState.Loading
                        else
                            loadingState = result
                    }

                    is LoadingState.Success -> {
                        page++
                        val response = result.response.filter {
                            it.entityType in entityTypeList || it.entityTemplate in entityTemplateList
                        } // TODO
                        firstItem = response.firstOrNull()?.id
                        lastItem = response.lastOrNull()?.id
                        loadingState =
                            if (isLoadMore)
                                LoadingState.Success(
                                    (((loadingState as? LoadingState.Success)?.response
                                        ?: emptyList()) + response).distinctBy { it.entityId }
                                )
                            else
                                LoadingState.Success(response)
                        footerState = FooterState.Success
                    }
                }
                isLoadMore = false
                isRefreshing = false
            }
        }
    }

    fun refresh() {
        if (!isRefreshing && !isLoadMore) {
            page = 1
            isEnd = false
            isLoadMore = false
            isRefreshing = true
            fetchData()
        }
    }

    fun loadMore() {
        if (!isRefreshing && !isLoadMore) {
            isEnd = false
            isLoadMore = true
            fetchData()
            if (loadingState is LoadingState.Success) {
                footerState = FooterState.Loading
            } else {
                loadingState = LoadingState.Loading
            }
        }
    }

}