package com.example.classes

import redis.clients.jedis.Jedis

object RedisConnector {
    var db: Jedis? = Jedis("localhost", 6379)
    fun tryConnection(){
        if (db == null || !db!!.isConnected) {
            db = Jedis("localhost", 6379)
        }
    }
}