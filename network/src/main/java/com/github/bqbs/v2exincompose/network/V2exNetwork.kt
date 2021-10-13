package com.github.bqbs.v2exincompose.network

import com.github.bqbs.v2exincompose.model.Member
import com.github.bqbs.v2exincompose.model.TopicsBeanItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

object V2exNetwork {

    // https://www.v2ex.com/api/topics/hot.json
    fun getHots() {}

    // https://www.v2ex.com/api/topics/latest.json
    fun getLatest() {}

    // https://www.v2ex.com/api/nodes/show.json
    fun showNode(node: String) {}

    // https://www.v2ex.com/api/members/show.json
    suspend fun getProfile(username: String): Member = getService().getProfile(username)

    // https://www.v2ex.com/api/members/show.json
    suspend fun getProfile(id: Long): Member {
        return getService().getProfile(id)
    }

    fun getService() = ServiceCreator.create(V2exService::class.java)
}

interface V2exService {


    // https://www.v2ex.com/api/topics/hot.json
    @GET("api/topics/hot.json")
    suspend fun getHots()

    // https://www.v2ex.com/api/topics/latest.json
    @GET("api/topics/latest.json")
    suspend fun getLatest(): Array<TopicsBeanItem>

    // https://www.v2ex.com/api/nodes/show.json
    @GET("api/nodes/show.json")
    suspend fun showNode(node: String)

    // https://www.v2ex.com/api/members/show.json
    @GET("api/members/show.json")
    suspend fun getProfile(@Query("id") id: Long): Member

    // https://www.v2ex.com/api/members/show.json
    @GET("api/members/show.json")
    suspend fun getProfile(@Query("username") username: String): Member
}