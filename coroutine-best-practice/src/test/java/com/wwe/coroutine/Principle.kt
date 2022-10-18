package com.wwe.coroutine

import kotlinx.coroutines.*
// Ref: https://mp.weixin.qq.com/s/2q1dxT4QxsuxP3wmrUa4Ag
fun main() {
    postItem(Item("start"))
}

fun postItem(item: Item) {
    GlobalScope.launch {
        // 获取签名
        val deferredToken: Deferred<Token> = async { preparePost() }
        val token: Token = deferredToken.await()

        // 发送网络请求
        val deferredPost: Deferred<Post> = async { submitPost(token, item) }
        val post = deferredPost.await()

        // 处理结果
        processPost(post)
    }
}

suspend fun preparePost() : Token = withContext(Dispatchers.IO) { Token("preparePost") }
suspend fun submitPost(token: Token, item: Item) : Post = withContext(Dispatchers.IO) { Post("${token.result} ${item.result}") }
fun processPost(post: Post) {  }

data class Token(val result: String? = null)
data class Item(val result: String? = null)
data class Post(val result: String? = null)