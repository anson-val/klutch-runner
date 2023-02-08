package com.example.classes

import redis.clients.jedis.Jedis

object RedisConnector {
    var db: Jedis? = Jedis(ConfigLoader.config.cacheDatabase.host, ConfigLoader.config.cacheDatabase.port)
    fun tryConnection() {
        if (db == null || !db!!.isConnected) {
            db = Jedis(ConfigLoader.config.cacheDatabase.host, ConfigLoader.config.cacheDatabase.port)
        }
    }
}