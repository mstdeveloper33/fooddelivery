package com.mst.fooddelivery.ui.features.auth.signup

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mst.fooddelivery.data.FoodApi
import com.mst.fooddelivery.data.models.SignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
@Inject constructor(
    /* FoodApi nesnesini inject ediyoruz. Buradaki oturum acma islemleri ve kullanici bilgileri almak icin */
    val foodApi: FoodApi
) : ViewModel() {
    // UI durumunu temsil eden bir StateFlow. Başlangıç değeri Nothing.
    private val _uiState = MutableStateFlow<SignupEvent>(SignupEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    // Navigasyon olaylarını temsil eden bir SharedFlow.
    private val _navigationEvent = MutableSharedFlow<SigupNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // Kullanıcının e-posta adresini tutan bir StateFlow.
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    // Kullanıcının şifresini tutan bir StateFlow.
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // Kullanıcının ismini tutan bir StateFlow.
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    // Kullanıcının e-posta adresi değiştiğinde çağrılır.
    fun onEmailChanged(email: String) {
        _email.value = email
    }

    // Kullanıcının şifresi değiştiğinde çağrılır.
    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    // Kullanıcının ismi değiştiğinde çağrılır.
    fun onNameChanged(name: String) {
        _name.value = name
    }

    // Kullanıcı kayıt butonuna tıkladığında apiye istek yapılır. Bir sonraki ekrana gecmek için navigateToHome olayı yayınlanır.
    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.value = SignupEvent.Loading
            try {
                val response =
                    foodApi.signUp(
                        SignUpRequest(
                            name = _name.value,
                            email = email.value,
                            password = password.value
                        )
                    )
                if (response.token.isNotEmpty()) {
                    _uiState.value = SignupEvent.Success
                    _navigationEvent.emit(SigupNavigationEvent.NavigateToHome)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SignupEvent.Error
            }
        }

    }


    // Navigasyon olaylarını temsil eden sealed sınıf.
    sealed class SigupNavigationEvent {
        // Kullanıcıyı login (giriş) ekranına yönlendirme olayı.
        object NavigateToLogin : SigupNavigationEvent()
        // Kullanıcıyı home (ana sayfa) ekranına yönlendirme olayı.
        object NavigateToHome : SigupNavigationEvent()
    }

    // UI durumlarını temsil eden sealed sınıf.
    sealed class SignupEvent {
        // Başlangıç durumu, herhangi bir işlem yapılmadığında kullanılır.
        object Nothing : SignupEvent()
        // Kayıt işlemi başarılı olduğunda kullanılır.
        object Success : SignupEvent()
        // Kayıt işlemi sırasında hata meydana geldiğinde kullanılır.
        object Error : SignupEvent()
        // Kayıt işlemi sırasında yüklenme (loading) durumu.
        object Loading : SignupEvent()
    }
}
