//package com.lingyun.lib.imsample.ui.chat
//
//import android.app.Application
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.util.Log
//import android.widget.ImageView
//import androidx.annotation.Nullable
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.bumptech.glide.request.target.SimpleTarget
//import com.bumptech.glide.request.transition.Transition
//import com.lingyun.lib.imsample.R
//
//
///*
//* Created by mc_luo on 5/14/21 3:23 PM.
//* Copyright (c) 2021 The LingYun Authors. All rights reserved.
//*
//* Licensed under the Apache License, Version 2.0 (the "License");
//* you may not use this file except in compliance with the License.
//* You may obtain a copy of the License at
//*
//*      http://www.apache.org/licenses/LICENSE-2.0
//*
//* Unless required by applicable law or agreed to in writing, software
//* distributed under the License is distributed on an "AS IS" BASIS,
//* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//* See the License for the specific language governing permissions and
//* limitations under the License.
//*/
//class ChatImageLoad(val application: Application) : ImageLoader {
//    private val TAG = "ChatImageLoad"
//
//    val density: Float = application.getResources().getDisplayMetrics().density
//    val MIN_WIDTH = 60 * density
//    val MAX_WIDTH = 200 * density
//    val MIN_HEIGHT = 60 * density
//    val MAX_HEIGHT = 200 * density
//
//    override fun loadAvatarImage(avatarImageView: ImageView, string: String) {
//        // You can use other image load libraries.
//        // You can use other image load libraries.
//        if (string.contains("R.drawable")) {
//            val resId: Int = application.getResources().getIdentifier(
//                string.replace("R.drawable.", ""),
//                "drawable", application.getPackageName()
//            )
//            avatarImageView.setImageResource(resId)
//        } else {
//            Glide.with(application)
//                .load(string)
//                .apply(RequestOptions().placeholder(R.drawable.aurora_headicon_default))
//                .into(avatarImageView)
//        }
//    }
//
//    override fun loadImage(imageView: ImageView?, string: String?) {
//        // You can use other image load libraries.
//        // You can use other image load libraries.
//        Glide.with(application)
//            .asBitmap()
//            .load(string)
//            .apply(RequestOptions().fitCenter().placeholder(R.drawable.aurora_picture_not_found))
//            .into(object : SimpleTarget<Bitmap?>() {
//                override fun onResourceReady(
//                    resource: Bitmap,
//                    @Nullable transition: Transition<in Bitmap?>?
//                ) {
//                    val imageWidth = resource.width
//                    val imageHeight = resource.height
//                    Log.d(TAG, "Image width $imageWidth height: $imageHeight")
//
//                    // 裁剪 bitmap
//                    val width: Float
//                    val height: Float
//                    if (imageWidth > imageHeight) {
//                        if (imageWidth > MAX_WIDTH) {
//                            val temp: Float = MAX_WIDTH / imageWidth * imageHeight
//                            height = if (temp > MIN_HEIGHT) temp else MIN_HEIGHT
//                            width = MAX_WIDTH
//                        } else if (imageWidth < MIN_WIDTH) {
//                            val temp: Float = MIN_WIDTH / imageWidth * imageHeight
//                            height = if (temp < MAX_HEIGHT) temp else MAX_HEIGHT
//                            width = MIN_WIDTH
//                        } else {
//                            var ratio = (imageWidth / imageHeight).toFloat()
//                            if (ratio > 3) {
//                                ratio = 3f
//                            }
//                            height = imageHeight * ratio
//                            width = imageWidth.toFloat()
//                        }
//                    } else {
//                        if (imageHeight > MAX_HEIGHT) {
//                            val temp: Float = MAX_HEIGHT / imageHeight * imageWidth
//                            width = if (temp > MIN_WIDTH) temp else MIN_WIDTH
//                            height = MAX_HEIGHT
//                        } else if (imageHeight < MIN_HEIGHT) {
//                            val temp: Float = MIN_HEIGHT / imageHeight * imageWidth
//                            width = if (temp < MAX_WIDTH) temp else MAX_WIDTH
//                            height = MIN_HEIGHT
//                        } else {
//                            var ratio = (imageHeight / imageWidth).toFloat()
//                            if (ratio > 3) {
//                                ratio = 3f
//                            }
//                            width = imageWidth * ratio
//                            height = imageHeight.toFloat()
//                        }
//                    }
//                    val params = imageView!!.layoutParams
//                    params.width = width.toInt()
//                    params.height = height.toInt()
//                    imageView.layoutParams = params
//                    val matrix = Matrix()
//                    val scaleWidth = width / imageWidth
//                    val scaleHeight = height / imageHeight
//                    matrix.postScale(scaleWidth, scaleHeight)
//                    imageView.setImageBitmap(Bitmap.createBitmap(resource, 0, 0, imageWidth, imageHeight, matrix, true))
//                }
//            })
//    }
//
//    override fun loadVideo(imageCover: ImageView, uri: String?) {
//        val interval = (5000 * 1000).toLong()
//        Glide.with(application)
//            .asBitmap()
//            .load(uri) // Resize image view by change override size.
//            .apply(RequestOptions().frame(interval).override(200, 400))
//            .into(imageCover)
//    }
//}