package com.github.bqbs.v2exincompose

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.github.bqbs.v2exincompose.model.TopicsBeanItem
import com.github.bqbs.v2exincompose.repository.V2exRepository
import com.github.bqbs.v2exincompose.ui.theme.NodeTextBgColor
import com.github.bqbs.v2exincompose.ui.theme.NodeTextColor
import com.github.bqbs.v2exincompose.ui.theme.V2exInComposeTheme
import com.zj.refreshlayout.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun Topics(
    mainActions: MainActions? = null,
    topicsItem: TopicsBeanItem? = null
) {

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

                painter = rememberImagePainter(data = topicsItem?.member?.avatar_mini,
                    onExecute = ImagePainter.ExecuteCallback { _, _ -> true },
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
                        mainActions?.showProfile?.invoke()
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
        if (topicList.value.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState(), enabled = true),
                contentAlignment = Alignment.Center
            ) {
                Text("下拉试试")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxHeight(),
                content = {
                    items(topicList.value!!) {
                        Topics(mainActions, topicsItem = it)
                        Divider()
                    }
                })
        }
    }
}


class TopicsPageViewModel(application: Application) : AndroidViewModel(application) {
    val repository by lazy {
        V2exRepository()
    }
    val _topicList = MutableLiveData<Array<TopicsBeanItem>?>(null)
    val topicList: LiveData<Array<TopicsBeanItem>?>
        get() = _topicList

    fun getLatest() {

        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getLatest()

            _topicList.postValue(list)
        }
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    V2exInComposeTheme {
        TopicsPage()
    }
}