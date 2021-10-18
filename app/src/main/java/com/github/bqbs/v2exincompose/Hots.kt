package com.github.bqbs.v2exincompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bqbs.v2exincompose.model.TopicsBeanItem
import com.github.bqbs.v2exincompose.repository.V2exRepository
import com.zj.refreshlayout.SwipeRefreshLayout
import com.zj.shimmer.ShimmerConfig
import com.zj.shimmer.shimmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HotsPage(
    mainActions: MainActions? = null,
    viewModel: HotsPageViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val hotsList = viewModel.hotsList.observeAsState()

    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(2000)
            refreshing = false
        }
    }

    SwipeRefreshLayout(isRefreshing = refreshing, onRefresh = {
        refreshing = true
        viewModel.getHots()
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


            if (!hotsList.value.isNullOrEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(hotsList.value!!) {
                        Topics(mainActions, topicsItem = it)
                        Divider()
                    }
                }
            }
        }
//        }
    }

    if (hotsList.value.isNullOrEmpty()) {
        viewModel.getHots()
    }
}


class HotsPageViewModel : ViewModel() {
    private val repository by lazy { V2exRepository() }
    private val _hotsList = MutableLiveData<Array<TopicsBeanItem>?>(null)
    val hotsList: LiveData<Array<TopicsBeanItem>?>
        get() = _hotsList

    fun getHots() {

        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getHots()

            _hotsList.postValue(list)
        }
    }
}
