package com.kyc.favorapp.model

import com.kyc.favorapp.bean.BaseEntity
import com.kyc.favorapp.bean.LoginInfo
import com.kyc.favorapp.net.HttpUtils
import com.kyc.favorapp.util.*
import io.reactivex.Observable

object LoginMode {
    /**
     * 仅仅是用来处理数据    设置数据  和  删除数据
     */
    fun toLogin(userName: String, password: String): Observable<BaseEntity<LoginInfo>> {
        val hashMap = HashMap<String, Any>()
        hashMap["userName"] = userName
        hashMap["code"] = password
        SpUtil.getString(SpConfig.TOKEN).let {
            if (it.isNotEmpty()) {
                hashMap["accessToken"] = it
            }
        }

        SpUtil.getString(SpConfig.WECHAT_UID).let {
            if (it.isNotEmpty()) {
                hashMap["wechatUid"] = it
            }
        }
        SpUtil.getString(SpConfig.SHOP_ID).let {
            if (it.isNotEmpty()) {
                hashMap["shopId"] = it
            }
        }

      return  HttpUtils.get().login(hashMap.toRequestBody())
    }
}