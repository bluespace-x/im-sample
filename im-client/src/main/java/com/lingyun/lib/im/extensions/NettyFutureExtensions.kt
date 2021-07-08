package com.lingyun.lib.im.extensions

import io.netty.util.concurrent.Future
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.util.concurrent.ExecutionException


fun <T> Future<T>.asDeffer(): Deferred<T> {
    if (isDone()) {
        return try {
            @Suppress("UNCHECKED_CAST")
            (CompletableDeferred(get() as T))
        } catch (e: Throwable) {
            // unwrap original cause from ExecutionException
            val original = (e as? ExecutionException)?.cause ?: e
            CompletableDeferred<T>().also { it.completeExceptionally(original) }
        }
    }

    val result = CompletableDeferred<T>()
    addListener {
        when {
            it.cause() != null -> {
                result.completeExceptionally(it.cause())
            }
            else -> {
                val value = it.get()
                @Suppress("UNCHECKED_CAST")
                result.complete(value as T)
            }
        }
    }

//    if (this is Future<*>) result.cancelFutureOnCompletion(this)
    result.invokeOnCompletion {
        this.cancel(false)
    }
    return result
}