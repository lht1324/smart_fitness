package com.overeasy.smartfitness

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.url

suspend inline fun <reified T> HttpClient.simplePost(
    baseUrl: String,
    block: HttpRequestBuilder.() -> Unit = {}
): T = post {
    url(baseUrl)
    block()
}.body()

suspend inline fun <reified T> HttpClient.simpleGet(
    baseUrl: String,
    block: HttpRequestBuilder.() -> Unit = {}
): T = get {
    url(baseUrl)
    block()
}.body()

suspend inline fun <reified T> HttpClient.simplePut(
    baseUrl: String,
    block: HttpRequestBuilder.() -> Unit = {}
): T = put {
    url(baseUrl)
    block()
}.body()

suspend inline fun <reified T> HttpClient.simpleDelete(
    baseUrl: String,
    block: HttpRequestBuilder.() -> Unit = {}
): T = delete {
    url(baseUrl)
    block()
}.body()