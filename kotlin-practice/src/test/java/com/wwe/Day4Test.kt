package com.wwe

import org.junit.Test

// Ref: https://mp.weixin.qq.com/s?__biz=MzAwNDgwMzU4Mw==&mid=2247485289&idx=1&sn=5ae6ef3d32cdcfee171dda8cc8a4026f&chksm=9b2719f9ac5090efe1042a1e3bd87c8fe24d2d2957984a0e1a281728eee0c01f5c8e85de8e3e&scene=178&cur_album_id=1535736585781035010#rd
class Day4Test {

    var data1: String? = "some awesome string value"
    var data2: String? = null

    @Test
    fun test() {
        data1?.let {
            data2?.let {
                println("data2")
            }
        } ?: run {
            println("data1 is null")
        }
    }
}