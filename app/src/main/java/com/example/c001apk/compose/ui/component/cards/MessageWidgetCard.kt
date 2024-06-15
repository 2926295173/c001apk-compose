package com.example.c001apk.compose.ui.component.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.c001apk.compose.ui.ffflist.FFFListType
import com.example.c001apk.compose.util.CookieUtil

/**
 * Created by bggRGjQaUbCoE on 2024/6/10
 */
@Composable
fun MessageWidgetCard(
    modifier: Modifier = Modifier,
    onViewFFFList: (String?, String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))

    ) {
        FFFCardRow(
            dataList = listOf(
                FFFCardItem(
                    title = "本地收藏",
                    value = null,
                    imageVector = Icons.Outlined.Archive,
                    FFFListType.FAV.name
                ),
                FFFCardItem(
                    title = "浏览历史",
                    value = null,
                    imageVector = Icons.Outlined.History,
                    FFFListType.HISTORY.name
                ),
                FFFCardItem(
                    title = "我的常去",
                    value = null,
                    imageVector = Icons.Outlined.MyLocation,
                    FFFListType.RECENT.name
                )
            ),
            onViewFFFList = { type ->
                onViewFFFList(CookieUtil.uid, type)
            }
        )
        FFFCardRow(
            dataList = listOf(
                FFFCardItem(
                    title = "我的收藏",
                    value = null,
                    imageVector = Icons.Outlined.StarOutline,
                    FFFListType.COLLECTION.name
                ),
                FFFCardItem(
                    title = "我的赞",
                    value = null,
                    imageVector = Icons.Outlined.FavoriteBorder,
                    FFFListType.LIKE.name
                ),
                FFFCardItem(
                    title = "我的回复",
                    value = null,
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    FFFListType.REPLY.name
                )
            ),
            onViewFFFList = { type ->
                onViewFFFList(if (type == FFFListType.COLLECTION.name) null else CookieUtil.uid, type)
            }
        )
    }

}