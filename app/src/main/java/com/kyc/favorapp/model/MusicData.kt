package com.kyc.favorapp.model

import android.os.Parcel
import android.os.Parcelable

import java.io.Serializable

/**
 * Created by AchillesL on 2016/11/15.
 */

class MusicData(/*音乐资源id*/
    val musicRes: Int, /*专辑图片id*/
    val musicPicRes: Int, /*音乐名称*/
    val musicName: String, /*作者*/
    val musicAuthor: String
) : Serializable
