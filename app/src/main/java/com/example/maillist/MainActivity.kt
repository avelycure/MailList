package com.example.maillist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maillist.ui.theme.MailListTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

data class MailItem(val title: String, val from: String, val snippet: String)

object Data {
    val mails = mutableListOf<MailItem>()
    val portal = listOf("Почта", "Календарь", "Заметки", "Облако")

    init {
        repeat(20) {
            mails.add(MailItem("Title $it", "Ivan Emelin", "Hello, check this out"))
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MailListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(false),
                        onRefresh = {}) {
                        MailScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MailScreen() {
    val lazyListState = rememberLazyListState()
    Box {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = R.drawable.background),
            contentDescription = ""
        )
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                ParallaxContent(lazyListState)
            }
            item {
                MailList()
            }
        }
    }
}

@Composable
fun ParallaxContent(
    lazyListState: LazyListState,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
            alpha = countAlpha(lazyListState)
            translationY = countTranslationY(lazyListState)
        }) {
        Column {
            Portal()
            InboxInfo()
        }
    }
}

@Composable
fun Portal() {
    LazyRow {
        items(Data.portal) {
            Text(
                text = it,
                fontSize = 30.sp,
                modifier = Modifier.padding(8.dp),
                color = Color.White
            )
        }
    }
}

@Composable
fun InboxInfo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Входящие 253",
                fontSize = 22.sp,
                maxLines = 1,
                color = Color.White
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.tune_fill0_wght400_grad0_opsz24),
                contentDescription = "Back",
                modifier = Modifier
                    .size(16.dp),
                tint = Color.White
            )
        }
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.search_fill0_wght400_grad0_opsz24),
            contentDescription = "Back",
            modifier = Modifier.size(20.dp),
            tint = Color.White
        )
    }
}

@Composable
fun MailList() {
    Column {
        Data.mails.forEachIndexed { index, item ->
            if (index == 0) {
                ColumnHead {
                    Mail(item)
                }
            } else {
                Mail(item)
            }
        }
    }
}

@Composable
fun Mail(mailItem: MailItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillWidth,
                painter = painterResource(id = R.drawable.pic),
                contentDescription = "Category image"
            )
            Column {
                Text(text = mailItem.from, fontSize = 18.sp)
                Text(text = mailItem.title, fontSize = 18.sp)
                Text(text = mailItem.snippet, fontSize = 16.sp)
            }
        }
        Text(text = "21:27", fontSize = 16.sp)
    }
}

@Composable
fun ColumnHead(
    modifier: Modifier = Modifier,
    block: @Composable () -> Unit = {},
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    ) {
        block()
    }
}

fun countTranslationY(lazyListState: LazyListState): Float {
    return when {
        lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty() && lazyListState.firstVisibleItemIndex == 0 -> lazyListState.firstVisibleItemScrollOffset * 0.6f
        else -> 0f
    }
}

fun countAlpha(lazyListState: LazyListState): Float {
    return when {
        lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty() && lazyListState.firstVisibleItemIndex == 0 -> {
            val imageSize =
                lazyListState.layoutInfo.visibleItemsInfo[0].size
            val scrollOffset =
                lazyListState.firstVisibleItemScrollOffset
            1 - scrollOffset / imageSize.toFloat()
        }

        else -> 0f
    }
}