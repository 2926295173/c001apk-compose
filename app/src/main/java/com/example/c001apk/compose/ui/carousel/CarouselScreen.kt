package com.example.c001apk.compose.ui.carousel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.c001apk.compose.logic.model.HomeFeedResponse
import com.example.c001apk.compose.logic.model.TopicBean
import com.example.c001apk.compose.logic.state.LoadingState
import com.example.c001apk.compose.ui.component.BackButton
import com.example.c001apk.compose.ui.component.CommonScreen
import com.example.c001apk.compose.ui.component.cards.LoadingCard
import com.example.c001apk.compose.util.decode
import kotlinx.coroutines.launch

/**
 * Created by bggRGjQaUbCoE on 2024/6/11
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarouselScreen(
    onBackClick: () -> Unit,
    url: String,
    title: String,
    onViewUser: (String) -> Unit,
    onViewFeed: (String, String?) -> Unit,
    onOpenLink: (String, String?) -> Unit,
    onCopyText: (String?) -> Unit,
) {

    val viewModel =
        hiltViewModel<CarouselViewModel, CarouselViewModel.ViewModelFactory> { factory ->
            factory.create(isInit = true, url = url.decode, title = title)
        }

    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()
    var refreshState by remember { mutableStateOf(false) }

    var pagerState: PagerState

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton { onBackClick() }
                },
                title = {
                    Text(
                        text = viewModel.pageTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                start = paddingValues.calculateLeftPadding(layoutDirection),
                end = paddingValues.calculateRightPadding(layoutDirection)
            ),
        ) {
            when (viewModel.loadingState) {
                LoadingState.Loading, LoadingState.Empty, is LoadingState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LoadingCard(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 10.dp),
                            state = viewModel.loadingState,
                            onClick = if (viewModel.loadingState is LoadingState.Loading) null
                            else viewModel::loadMore
                        )
                    }
                }

                is LoadingState.Success -> {
                    val dataList =
                        (viewModel.loadingState as LoadingState.Success<List<HomeFeedResponse.Data>>).response

                    val isIconTabLinkGridCard =
                        dataList.find { it.entityTemplate == "iconTabLinkGridCard" }
                    if (isIconTabLinkGridCard == null) {
                        HorizontalDivider()
                        CommonScreen(
                            viewModel = viewModel,
                            refreshState = null,
                            resetRefreshState = {},
                            onViewUser = onViewUser,
                            onViewFeed = onViewFeed,
                            onOpenLink = onOpenLink,
                            onCopyText = onCopyText,
                        )
                    } else {
                        isIconTabLinkGridCard.entities?.map {
                            TopicBean(it.url, it.title)
                        }?.let { tabList ->
                            pagerState = rememberPagerState(pageCount = { tabList.size })
                            SecondaryScrollableTabRow(
                                selectedTabIndex = pagerState.currentPage,
                                indicator = {
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier
                                            .tabIndicatorOffset(
                                                pagerState.currentPage,
                                                matchContentSize = true
                                            )
                                            .clip(
                                                RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                            )
                                    )
                                },
                                divider = {}
                            ) {
                                tabList.forEachIndexed { index, tab ->
                                    Tab(
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            if (pagerState.currentPage == index) {
                                                refreshState = true
                                            }
                                            scope.launch { pagerState.animateScrollToPage(index) }
                                        },
                                        text = { Text(text = tab.title) }
                                    )
                                }
                            }

                            HorizontalDivider()

                            HorizontalPager(
                                state = pagerState,
                            ) { index ->
                                CarouselContentScreen(
                                    url = tabList[index].url,
                                    title = tabList[index].title,
                                    bottomPadding = paddingValues.calculateBottomPadding(),
                                    refreshState = refreshState,
                                    resetRefreshState = { refreshState = false },
                                    onViewUser = onViewUser,
                                    onViewFeed = onViewFeed,
                                    onOpenLink = onOpenLink,
                                    onCopyText = onCopyText
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}