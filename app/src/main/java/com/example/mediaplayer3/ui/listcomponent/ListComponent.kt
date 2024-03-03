package com.example.mediaplayer3.ui.listcomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.ui.LoadingScreen
import com.example.mediaplayer3.ui.screen.ItemImage
import com.example.mediaplayer3.ui.screen.RegularText
import com.example.mediaplayer3.ui.screen.SubTitleText
import com.example.mediaplayer3.ui.screen.TitleText
import com.example.mediaplayer3.ui.theme.ItemBackground
import com.example.mpcore.api.log.MPLog


@Composable
fun Item(
    modifier: Modifier = Modifier,
    itemData: ItemData,
    isPlaying: Boolean = false,
    onItemClicked: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(100.dp)
            .clickable {
                onItemClicked()
            }, elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(color = ItemBackground)
                .fillMaxSize()
        ) {
            ItemImage(
                imageUri = itemData.imageUri,
                modifier = modifier
                    .height(50.dp)
                    .width(50.dp)
                    .weight(1F)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier.weight(3F)) {
                TitleText(
                    text = itemData.title,
                    modifier = modifier.padding(8.dp),
                    isPlaying = isPlaying
                )
                itemData.subtitle?.let {
                    SubTitleText(text = it, isPlaying = isPlaying)
                }
            }
                Spacer(modifier = Modifier.weight(1F))

                RegularText(text = itemData.endText)
        }
    }
}

@Composable
fun ListComponent(
    modifier: Modifier = Modifier,
    dataList: List<ItemData>,
    isEndReached: Boolean,
    isNextItemLoading: Boolean,
    selectedItem: ItemData? = null,
    onListItemClick: (ItemData)->Unit,
    loadNextItem: () -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(dataList.size, key = {
            dataList[it].id
        }) {
            val item = dataList[it]
            if (it >= dataList.size - 1 && !isEndReached && !isNextItemLoading) {
                LaunchedEffect(key1 = Unit) {
                    loadNextItem()
                }
            }
            if (selectedItem != null && selectedItem.id == item.id) {
                MPLog.d(
                    Constant.TrackList.CLASS_NAME,
                    "AudioList",
                    Constant.TrackList.TAG,
                    "selectedItem: $item"
                )
                Item(itemData = selectedItem, isPlaying = true) {
                    onListItemClick(item)
                }
            } else {
                Item(itemData = item) {
                    onListItemClick(item)
                }
            }
            if (it >= dataList.size - 1 && isEndReached && !isNextItemLoading) {
                Spacer(modifier = Modifier.padding(bottom = 50.dp))
            } else if (isNextItemLoading && it >= dataList.size - 1) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LoadingScreen()
                }
            }
        }
    }
}