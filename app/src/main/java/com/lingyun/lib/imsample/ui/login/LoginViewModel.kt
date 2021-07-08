package com.lingyun.lib.imsample.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.lingyun.lib.component.plugin.PluginManager
import com.lingyun.lib.imsample.data.LoginRepository
import com.lingyun.lib.imsample.data.Result

import com.lingyun.lib.imsample.R
import com.lingyun.lib.network.api.AuthorizationResult
import com.lingyun.lib.network.api.NetworkPlugin
import com.lingyun.lib.network.api.OauthService
import com.lingyun.lib.user.api.UserPlugin
import com.lingyun.lib.user.api.UserService
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class LoginViewModel() : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val oauthServiceDeferred = viewModelScope.async(start = CoroutineStart.LAZY) {
        val plugin = PluginManager.getPlugin(NetworkPlugin::class.java.simpleName)!!
        plugin.getServiceAsync(OauthService::class.java).await()
    }

    private val userServiceDeferred = viewModelScope.async(start = CoroutineStart.LAZY) {
        val plugin = PluginManager.getPlugin(UserPlugin::class.java)!!
        plugin.getServiceAsync(UserService::class.java).await()
    }

    fun loadHistoryUserInfo(context: Context): Pair<String?, String?> {
        val userName =
            PreferenceManager.getDefaultSharedPreferences(context).getString("username", null)
        val password =
            PreferenceManager.getDefaultSharedPreferences(context).getString("password", null)
        return Pair(userName, password)
    }

    fun loginAsync(
        context: Context,
        username: String,
        password: String
    ): Deferred<AuthorizationResult> {
        saveHistoryUserInfo(context, username, password)
        // can be launched in a separate asynchronous job
        return viewModelScope.async {
            val result = oauthServiceDeferred.await().oauthTokenAsync(username, password).await()
            if (result is AuthorizationResult.Authorization) {
                userServiceDeferred.await().me().await()
            }
            result
        }
    }

    private fun saveHistoryUserInfo(context: Context, username: String, password: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString("username", username)
            .putString("password", password).apply()
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }


    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}