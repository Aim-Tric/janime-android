package cn.codebro.j_anime_android.net

import cn.codebro.j_anime_android.JAnimeApplication
import cn.codebro.j_anime_android.data.CookieDao
import cn.codebro.j_anime_android.data.CookieEntity
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap


class PersistentCookieStore {
    private val cookies: MutableMap<String, ConcurrentHashMap<String?, Cookie>>
    private val cookieDao: CookieDao

    init {
        cookies = HashMap()
        cookieDao = JAnimeApplication.db!!.cookieDao()
        val cookieEntities = cookieDao.getAll()
        //将持久化的cookies缓存到内存中 即map cookies
        cookieEntities.forEach {
            val key = it.host
            if (cookies[key] == null) {
                cookies[key] = ConcurrentHashMap()
            }
            val cookie = convert(it)
            cookies[key]?.set(getCookieToken(cookie), cookie)
        }
    }

    private fun convert(okHttpCookie: CookieEntity): Cookie {
        val cookieBuilder = Cookie.Builder()
            .name(okHttpCookie.name)
            .value(okHttpCookie.value)
            .domain(okHttpCookie.domain)
            .path(okHttpCookie.path)
            .expiresAt(okHttpCookie.expiredAt)
        if (okHttpCookie.secure) cookieBuilder.secure()
        if (okHttpCookie.httpOnly) cookieBuilder.httpOnly()
        if (okHttpCookie.hostOnly) cookieBuilder.hostOnlyDomain(okHttpCookie.domain)
        return cookieBuilder.build()
    }

    private fun convert(host: String, cookie: Cookie): CookieEntity {
        return CookieEntity(
            null,
            host,
            cookie.name,
            cookie.value,
            cookie.domain,
            cookie.path,
            cookie.expiresAt,
            cookie.secure,
            cookie.httpOnly,
            cookie.hostOnly
        )
    }

    private fun getCookieToken(cookie: Cookie): String {
        return cookie.name + "@" + cookie.domain
    }

    private fun Cookie.isExpire(): Boolean {
        return persistent && expiresAt < System.currentTimeMillis()
    }

    fun add(url: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)

        //将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (!cookie.isExpire()) {
            if (!cookies.containsKey(url.host)) {
                cookies[url.host] = ConcurrentHashMap()
            }
            cookies[url.host]!![name] = cookie
        } else {
            if (cookies.containsKey(url.host)) {
                cookies[url.host]!!.remove(name)
            }
        }
        val cookieEntity = convert(url.host, cookie)
        cookieDao.insertAll(cookieEntity)
    }

    operator fun get(url: HttpUrl): List<Cookie> {
        return get(url.host)
    }

    operator fun get(url: String): List<Cookie> {
        val ret = ArrayList<Cookie>()
        if (cookies.containsKey(url)) {
            ret.addAll(cookies[url]!!.values)
        }
        return ret
    }

    fun removeAll(): Boolean {

        return true
    }

    fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val name = getCookieToken(cookie)
        return false
    }

    fun getCookies(): List<Cookie> {
        val ret = ArrayList<Cookie>()
        for (key in cookies.keys) {
            ret.addAll(cookies[key]!!.values)
        }
        return ret
    }

}