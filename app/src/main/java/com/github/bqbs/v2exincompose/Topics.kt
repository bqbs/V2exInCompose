package com.github.bqbs.v2exincompose

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.github.bqbs.v2exincompose.model.TopicsBeanItem
import com.github.bqbs.v2exincompose.repository.V2exRepository
import com.github.bqbs.v2exincompose.ui.theme.NodeTextBgColor
import com.github.bqbs.v2exincompose.ui.theme.NodeTextColor
import com.github.bqbs.v2exincompose.ui.theme.V2exInComposeTheme
import com.zj.refreshlayout.SwipeRefreshLayout
import com.zj.shimmer.ShimmerConfig
import com.zj.shimmer.shimmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max


@SuppressLint("UnrememberedMutableState")
@Composable
fun Topics(
    mainActions: MainActions? = null,
    topicsItem: TopicsBeanItem? = null
) {

    val topicsItemState by remember {
        mutableStateOf(topicsItem)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .align(alignment = Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberImagePainter(data = topicsItemState?.member?.avatar_large,
                    onExecute = { _, _ -> true },
                    builder = {
                        crossfade(true)
                        placeholder(R.mipmap.ic_launcher)
                        transformations(CircleCropTransformation())
                    }),
                contentDescription = "",
                modifier = Modifier
                    .padding(8.dp)
                    .size(36.dp)
                    .align(alignment = Alignment.Start)
                    .clip(
                        RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        mainActions?.showProfile?.invoke(topicsItemState?.member?.id)
                    },
                alignment = Alignment.TopCenter
            )
        }

        Column(
            modifier = Modifier.weight(1f, true),
            horizontalAlignment = Alignment.Start

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Surface(
                    color = NodeTextBgColor,
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    Text(
                        modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                        text = topicsItem?.node?.title ?: "",
                        color = NodeTextColor
                    )
                }
                Spacer(modifier = Modifier.size(5.dp))
                Dot(size = 2.dp, color = Color.Black)
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = topicsItem?.member?.username ?: "")
            }

            Row(
                modifier = Modifier.wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp)
                        .wrapContentHeight(),
                    text = topicsItem?.title ?: "",
                    maxLines = 3
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "** min ago")
                Dot(size = 1.dp, color = Color.Black)
                Text(text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.DarkGray)) {
                        append("最后回复来自 ")
                    }
                    withStyle(style = SpanStyle(color = Color.Black, fontSize = 18.sp)) {
                        append(topicsItem?.last_reply_by ?: "")
                    }
                })
            }
        }
        Column(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.End
        ) {
            topicsItem?.replies?.let {
                if (it > 0) {
                    Surface(
                        color = Color.LightGray,
                        modifier = Modifier
                            .padding(8.dp)
                            .wrapContentSize()
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(start = 8.dp, end = 8.dp),
                            text = "$it",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Dot(size: Dp, color: Color) {
    Canvas(modifier = Modifier
        .padding()
        .size(size), onDraw = {
        drawCircle(color = color)
    })
}

@ExperimentalFoundationApi
@SuppressLint("UnrememberedMutableState")
@Composable
fun TopicsPage(
    mainActions: MainActions? = null,
    viewModel: TopicsPageViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
//    val actions by mutableStateOf(mainActions)

    val topicList = viewModel.topicList.observeAsState(initial = emptyArray())
    var refreshing by remember { mutableStateOf(false) }

    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(2000)
            refreshing = false
        }
    }

    SwipeRefreshLayout(isRefreshing = refreshing, onRefresh = {
        refreshing = true
        viewModel.getLatest()
    }) {
        /*     if (topicList.value.isNullOrEmpty()) {
                 Box(
                     modifier = Modifier
                         .fillMaxSize()
                         .verticalScroll(rememberScrollState(), enabled = true),
                     contentAlignment = Alignment.Center
                 ) {
                     Text("下拉试试")
                 }
             } else {*/
        Column(modifier = Modifier.shimmer(visible = refreshing, config = ShimmerConfig())) {

            val nodeList = topicList.value?.map { it.node }?.distinct()
            if (!nodeList.isNullOrEmpty()) {

                SimpleFlowRow(
                    modifier = Modifier.padding(8.dp),
                    verticalGap = 8.dp,
                    horizontalGap = 8.dp
                ) {
                    for (node in nodeList) {
                        Text(
                            text = node.title ?: "",
                            color = NodeTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .background(NodeTextBgColor, RoundedCornerShape(4.dp))
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }

            }

            if (!topicList.value.isNullOrEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(topicList.value!!) {
                        Topics(mainActions, topicsItem = it)
                        Divider()
                    }
                }
            }
        }
//        }
    }

    if (topicList.value.isNullOrEmpty()) {
        refreshing = true
        viewModel.getLatest()
    }

}


class TopicsPageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository by lazy {
        V2exRepository()
    }
    private val _topicList = MutableLiveData<Array<TopicsBeanItem>?>(null)
    val topicList: LiveData<Array<TopicsBeanItem>?>
        get() = _topicList

    fun getLatest() {

        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getLatest()

            _topicList.postValue(list)
        }
    }
}


@Composable
fun SimpleFlowRow(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start,
    verticalGap: Dp = 0.dp,
    horizontalGap: Dp = 0.dp,
    content: @Composable () -> Unit
) = Layout(content, modifier) { measurables, constraints ->
    val hGapPx = horizontalGap.roundToPx()
    val vGapPx = verticalGap.roundToPx()

    val rows = mutableListOf<MeasuredRow>()
    val itemConstraints = constraints.copy(minWidth = 0)

    for (measurable in measurables) {
        val lastRow = rows.lastOrNull()
        val placeable = measurable.measure(itemConstraints)

        if (lastRow != null && lastRow.width + hGapPx + placeable.width <= constraints.maxWidth) {
            lastRow.items.add(placeable)
            lastRow.width += hGapPx + placeable.width
            lastRow.height = max(lastRow.height, placeable.height)
        } else {
            val nextRow = MeasuredRow(
                items = mutableListOf(placeable),
                width = placeable.width,
                height = placeable.height
            )

            rows.add(nextRow)
        }
    }

    val width = rows.maxOfOrNull { row -> row.width } ?: 0
    val height = rows.sumBy { row -> row.height } + max(vGapPx.times(rows.size - 1), 0)

    val coercedWidth = width.coerceIn(constraints.minWidth, constraints.maxWidth)
    val coercedHeight = height.coerceIn(constraints.minHeight, constraints.maxHeight)

    layout(coercedWidth, coercedHeight) {
        var y = 0

        for (row in rows) {
            var x = when (alignment) {
                Alignment.Start -> 0
                Alignment.CenterHorizontally -> (coercedWidth - row.width) / 2
                Alignment.End -> coercedWidth - row.width

                else -> throw Exception("unsupported alignment")
            }
            for (item in row.items) {
                item.place(x, y)
                x += item.width + hGapPx
            }
            y += row.height + vGapPx
        }
    }
}

private data class MeasuredRow(
    val items: MutableList<Placeable>,
    var width: Int,
    var height: Int
)

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    V2exInComposeTheme {
        TopicsPage()
    }
}