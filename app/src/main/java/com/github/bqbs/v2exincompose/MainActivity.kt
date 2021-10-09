package com.github.bqbs.v2exincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.bqbs.v2exincompose.ui.theme.V2exInComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            V2exInComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {


                    LazyColumn(content = {
                        items(20) {
                            Topics()
                            Divider()
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    V2exInComposeTheme {
        LazyColumn(content = {
            items(20) {
                Topics()
                Divider()
            }
        })
    }
}

@Composable
fun Topics() {
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
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "",
                modifier = Modifier
                    .padding(8.dp)
                    .size(36.dp)
                    .align(alignment = Alignment.Start)
                    .clip(
                        RoundedCornerShape(18.dp)
                    ),
                alignment = Alignment.TopCenter
            )
        }

        Column(
            modifier = Modifier.weight(1f, true),
            horizontalAlignment = Alignment.Start

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Surface(
                    color = Color.LightGray,
                    modifier = Modifier
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    Text(text = "主题", color = Color.Gray)
                }
                Spacer(modifier = Modifier.size(10.dp))
                Dot(size = 1.dp, color = Color.Black)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "作者")
            }
            Text(text = "content\ncontent\ncontent", maxLines = 3)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "** min ago")
                Dot(size = 1.dp, color = Color.Black)
                Text(text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.DarkGray)) {
                        append("最后回复")
                    }
                    withStyle(style = SpanStyle(color = Color.Black)) {
                        append("一窝鸡尼斯")
                    }
                })
            }
        }
        Column(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.End
        ) {

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
                    text = "12",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
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