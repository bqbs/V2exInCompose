package com.github.bqbs.v2exincompose.repository

import androidx.lifecycle.MutableLiveData
import com.github.bqbs.v2exincompose.model.Member
import com.github.bqbs.v2exincompose.model.TopicsBeanItem
import com.github.bqbs.v2exincompose.network.V2exNetwork

class V2exRepository {
    suspend fun getProfile(id: Long): Member? =
        V2exNetwork.getProfile(id)

    suspend fun getProfile(username: String): Member? =
        V2exNetwork.getProfile(username)

    suspend fun getLatest(): Array<TopicsBeanItem>? =
        V2exNetwork.getLatest()

    suspend fun getHots() :Array<TopicsBeanItem>? = V2exNetwork.getHots()
}
