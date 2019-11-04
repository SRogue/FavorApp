package com.kyc.favorapp.activity

import android.os.Bundle
import com.kyc.favorapp.base.BaseActivity
import com.kyc.favorapp.util.toast


/*
 *  @项目名：  favorapp 
 *  @包名：    com.kyc.favorapp.activity
 *  @文件名:   ProxyActivity
 *  @创建者:   frostfire
 *  @创建时间:  2019/8/14 23:17
 *  @描述：    TODO
 */
class ProxyActivity :BaseActivity(){
    private val tag = ProxyActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast{"doing proxyActivity...."}
    }
}