package com.example.news

import io.reactivex.rxjava3.core.Observable
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL

//https://api.rss2json.com/v1/api.json?rss_url=https%3A%2F%2Flenta.ru%2Frss


val urlIndex = "https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Flenta.ru%2Frss%2Fnews"

fun createRequest(url: String) = Observable.create<String> {
    val urlConnection = URL(url).openConnection() as HttpURLConnection
    try {
        urlConnection.connect()

        if (urlConnection.responseCode != HttpURLConnection.HTTP_OK){
            it.onError(RuntimeException(urlConnection.responseMessage))
        } else {
            val str = urlConnection.inputStream.bufferedReader().readText()
            it.onNext(str)
        }
    } finally {
        urlConnection.disconnect()
    }

    it.onNext("hello")
}