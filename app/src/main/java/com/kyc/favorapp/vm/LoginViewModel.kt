package com.kyc.favorapp.vm

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import com.kyc.favorapp.base.BaseViewModel
import com.kyc.favorapp.bean.BaseEntity
import com.kyc.favorapp.bean.LoginInfo
import com.kyc.favorapp.model.LoginMode
import com.kyc.favorapp.util.DataCallback
import com.kyc.favorapp.util.SpConfig
import com.kyc.favorapp.util.nomarlSubscrib
import com.kyc.favorapp.util.saveSpValue
import com.uber.autodispose.autoDisposable

/**
 * vm层   数据调用  和  逻辑处理
 */
class LoginViewModel : BaseViewModel() {
    val loginUserInfo: MutableLiveData<LoginInfo> = MutableLiveData()

    fun toLogin2(userName: String, password: String) {
        LoginMode.toLogin(userName, password).map { it ->
            it.apply {
                SystemClock.sleep(5 * 1000)
                if (isSuccess()) {
                    data.let {
                        saveSpValue(SpConfig.SHOP_ID, it.shopId)
                        saveSpValue(SpConfig.TOKEN, it.jwt)
                        saveSpValue(SpConfig.ACCOUNT_NUMBER, userName)
                        saveSpValue(SpConfig.PASSWORD, password)
                    }
                }
            }
            it
        }.nomarlSubscrib()
            .autoDisposable(this)
            .subscribe(object : DataCallback<BaseEntity<LoginInfo>>() {
                override fun onSuccess(t: BaseEntity<LoginInfo>) {
                    t.data.apply(loginUserInfo::postValue)
                }

                override fun onError(code: Int, msg: String) {}
            })
    }
}