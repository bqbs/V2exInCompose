package com.github.bqbs.v2exincompose.model

data class TopicsBeanItem(
    val content: String,
    val content_rendered: String,
    val created: Int,
    val id: Int,
    val last_modified: Int,
    val last_reply_by: String,
    val last_touched: Int,
    val member: Member,
    val node: Node,
    val replies: Int,
    val title: String,
    val url: String
)