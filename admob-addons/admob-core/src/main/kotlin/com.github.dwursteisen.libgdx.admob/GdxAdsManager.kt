package com.github.dwursteisen.libgdx.admob

import com.badlogic.gdx.ApplicationListener


class AdsQuery(private val manager: GdxAdsManager, var key: String, var format: AdsSize = AdsSize.BANNER, var flags: AdsPosition = 0, var callback: () -> Unit = {}) {
    fun close(): Unit {
        manager.close(this)
    }
}


abstract class GdxAdsManager {
    abstract fun initialize(listener: ApplicationListener)

    fun configure(key: String): AdsQueryMaker {
        return AdsQueryMaker(this, key)
    }

    class AdsQueryMaker(manager: GdxAdsManager, key: String) {

        private val query = AdsQuery(manager = manager, key = key)

        fun configure(key: String): AdsQueryMaker {
            query.key = key
            return this
        }

        fun usingFormat(format: AdsSize): AdsQueryMaker {
            query.format = format
            return this
        }

        fun position(flag: AdsPosition): AdsQueryMaker {
            query.flags = flag
            return this
        }


        fun whenLoaded(apply: () -> Unit): AdsQueryMaker {
            query.callback = apply
            return this
        }

        fun create(): AdsQuery = query

    }


    abstract fun load(query: AdsQuery)
    abstract fun close(query: AdsQuery)
}