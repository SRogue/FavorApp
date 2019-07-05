package com.kyc.favorapp.model

import com.kyc.favorapp.bean.LoginInfo
import com.kyc.favorapp.net.HttpUtils
import com.kyc.favorapp.util.*

object LoginMode {

    /**
     * 仅仅是用来处理数据    设置数据  和  删除数据
     */

    fun toLogin(userName: String, password: String) {
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

        HttpUtils.doHttp(HttpUtils.get().login(transformRequestBody(hashMap)), object : DataCallback<LoginInfo> {
            override fun onSuccess(t: LoginInfo) {
                toast { t.userType.toString() }
                saveSpValue(SpConfig.SHOP_ID, t.shopId)
                saveSpValue(SpConfig.TOKEN, t.jwt)
                saveSpValue(SpConfig.ACCOUNT_NUMBER, userName)
                saveSpValue(SpConfig.PASSWORD, password)

            }

            override fun onError(code: Int, msg: String) {

            }


        })
    }
}