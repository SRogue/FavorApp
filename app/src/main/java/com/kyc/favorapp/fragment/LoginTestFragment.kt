package com.kyc.favorapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.kyc.favorapp.R
import com.kyc.favorapp.util.*
import com.kyc.favorapp.vm.LoginViewModel
import com.uber.autodispose.autoDisposable
import kotlinx.android.synthetic.main.fragment_login_test.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *要持有viewMode，观察viewMode的数据进行视图处理
 */
class LoginTestFragment : AutoDisposeFragment(), View.OnClickListener {
    private lateinit var loginViewModel: LoginViewModel
    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.button -> {
                    loginViewModel.toLogin2(editext1.text.toString(), editext2.text.toString())
                }
                else -> {

                }
            }
        }
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getStringSp(SpConfig.ACCOUNT_NUMBER).let {
            if (it.isNotEmpty()) {
                editext1.setText(it)
            }
        }

        getStringSp(SpConfig.PASSWORD).let {
            if (it.isNotEmpty()) {
                editext2.setText(it)
            }

        }

        button.setOnClickListener(this)


        loginViewModel.loginUserInfo
            .toObservable()
            .autoDisposable(scopeProvider)
            .subscribe {
            result_text.text = it.shopId
        }

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginTestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }

            }
    }

}
