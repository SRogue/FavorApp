package com.kyc.favorapp.vm

import android.os.SystemClock
import com.kyc.favorapp.base.BaseViewModel
import com.kyc.favorapp.bean.BaseEntity
import com.kyc.favorapp.bean.LoginInfo
import com.kyc.favorapp.model.LoginMode
import com.kyc.favorapp.util.SpConfig
import com.kyc.favorapp.util.nomarlSubscrib
import com.kyc.favorapp.util.saveSpValue
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.autoDisposable

/**
 * vm层  类似于mvp中的p层，进行 数据调用  和  逻辑处理
 */
class LoginViewModel : BaseViewModel() {
    fun toLogin(userName: String, password: String): ObservableSubscribeProxy<BaseEntity<LoginInfo>> {
        return LoginMode.toLogin(userName, password).map { it ->
            SystemClock.sleep(5*1000)
            it?.data?.let {
                saveSpValue(SpConfig.SHOP_ID, it.shopId)
                saveSpValue(SpConfig.TOKEN, it.jwt)
                saveSpValue(SpConfig.ACCOUNT_NUMBER, userName)
                saveSpValue(SpConfig.PASSWORD, password)
            }
            it
        }.nomarlSubscrib().autoDisposable(this)
    }
}