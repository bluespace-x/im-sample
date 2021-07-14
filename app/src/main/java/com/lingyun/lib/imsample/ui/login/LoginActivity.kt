package com.lingyun.lib.imsample.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.lingyun.lib.imsample.R
import com.lingyun.lib.imsample.ui.chat.ChatActivity
import com.lingyun.lib.imsample.ui.chat.main.ChatMainActivity
import com.lingyun.lib.network.api.AuthorizationResult
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.launch
import proto.message.GroupId
import proto.message.GroupIdType
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels<LoginViewModel>()

    companion object {
        fun launcher(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })


        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> doLogin()
                }
                false
            }

            login.setOnClickListener {
                doLogin()
            }
        }

        loadHistoryUser()
    }

    @SuppressLint("TimberArgCount")
    private fun doLogin() {
        loading.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val result = loginViewModel.loginAsync(
                    this@LoginActivity,
                    username.text.toString(),
                    password.text.toString()
                ).await()
                when (result) {
                    is AuthorizationResult.Authorization -> {
                        gotoMainActivity()
                    }
                    is AuthorizationResult.Unauthorization -> {
                        showLoginFailed(result.errorDescription)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                showLoginFailed(e.toString())
            } finally {
                try {
                    loading.visibility = View.INVISIBLE
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun loadHistoryUser() {
        val user = loginViewModel.loadHistoryUserInfo(this)
        username.setText(user.first)
        password.setText(user.second)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun gotoMainActivity() {
//        val ChatGroupId = GroupId.newBuilder().also {
//            it.id = 10
//            it.idType = GroupIdType.GROUP_TYPE
//        }.build()
//        ChatActivity.launcher(this, ChatGroupId)

        ChatMainActivity.launcher(this)
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}