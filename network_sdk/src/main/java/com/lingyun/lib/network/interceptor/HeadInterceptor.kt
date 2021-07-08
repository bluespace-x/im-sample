package com.lingyun.lib.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Description：
 * Author：mc_luo
 * Date： 2017/11/20 上午10:55
 */

open class HeadInterceptor(val headParamsProperties: HashMap<String, String>) : Interceptor {

    private val TAG = "HeadInterceptor"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val compressedRequest = originalRequest.newBuilder()
        val params = headParamsProperties

        val toAddParams = params.entries.filter {
            //add only not special set
            originalRequest.header(it.key) == null
        }

        if (toAddParams.isNotEmpty()) {
            toAddParams.forEach {
                val key = it.key
                val param = it.value
                compressedRequest.addHeader(key, param)
            }
        }

        return chain.proceed(compressedRequest.build())
    }

}
