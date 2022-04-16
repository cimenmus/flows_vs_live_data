package com.icmen.flows

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    /**
     * https://www.youtube.com/watch?v=6Jc6-INantQ
     * https://medium.com/@musticmen/kotlin-cheatsheet-6-flows-50906c521d17
     * https://medium.com/@musticmen/kotlin-cheatsheet-7-flows-vs-livedata-39c246f60ab1
     *
     * Cold flow: Do not emit anything values even if there is no collector. (Flow build edilip calıstırılmaz) Flow and LiveData are cold flow(state holders)
     * Hot flow: Keep emitting values even if there are no collectors. StateFlow and Shared flow are hot flows
     */

    /**
     * live data is a cold state holder. Yani bağlı olduğu lifecycle ayakta oldugunda ve observe edildiğinde fire ediyor, yoksa etmiyor.
     * UI değişse de state'ini tutuyor, kaybetmiyor.
     * Boylece mesela orientation değiştiğinde yeni state'i fire ediyor ve UI bunu yakalıyor
     *
     * içindeki value her update edildiğinde observer tetiklenir. yeni gelen veri ile eskisi aynı olsa bile
     */
    private val _liveData = MutableLiveData("Hello World")
    val liveData: LiveData<String> = _liveData

    /**
     * Flow is a cold state holder, ama livedata gibi state tutmuyor.
     * State tutan versiyonu StateFlow. yani live data'nın flow alternatifi
     * StateFlow scope.launch ile kullanılmamalı, launchWhenStarted, launchWhenResumed, launchWhenCreated ile kullanılmalı.
     * Çünkü livedata lifecycle'a bağlıyken StateFlow değil. launch ile kullanırsak bağlı oldugu scope sonlandırılınca kendisi hala dinlemeye devame eder.
     * Bunu engellemek yani lifecycle'a bağlamak için launchWhenStarted, launchWhenResumed, launchWhenCreated ile kullanılmalı.
     *
     * * içindeki value değiştiğinde observer tetiklenir.
     * yeni gelen veri ile eskisi aynı olursa tetiklenmez, farklı is tetiklenir.
     * yani state değiştiğinde tetikler
     */
    private val _stateFlow = MutableStateFlow("Hello World")
    val stateFlow: StateFlow<String> = _stateFlow.asStateFlow()

    /**
     * Shared flow is a hot flow. Difference with the StateFlow is;
     * shared flow is more used to send one-time events like showing a snackbar one time.
     * For example if we use state flow and device orientation changed, event is fired every time orientation changed
     * and we show snackbar for all of them. But if we use shared flow for it, we will see the snacbar everytime device orientation changed
     *
     * Because of it is a hot flow, we need to collect inside launchWhenStarted, launchWhenResumed or launchWhenCreated.
     *
     * Yani Flow ile StateFlow'un ortasi: Hem StateFlow gibi hot, hem de Flow gibi state tutmuyor.
     * Flow'un hot versiyonu
     * StateFlow'un state tutmayan versiyonu.
     * Normal flow'un sadece bir collector'u olabilirken(çünkü create edip observe ediyoruz), SharedFlow'un birden fazla collector'u olabilir.
     *
     * emit edildiğinde her seferinde tetiklenir, sadece state tutmuyor.
     *
     * Mesela api den gelen error'u SharedFlow olarak tutarsak kullanıcı bir kere gorur, device rotate oldugunda error'u tekrar gormez.
     * Validasyon islmelerinde de kullanılabilir
     */
    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow: SharedFlow<String> = _sharedFlow.asSharedFlow()

    fun triggerLiveData() {
        _liveData.value = "LiveData"
    }

    fun triggerFlow(): Flow<String> = flow {
        repeat(times = 5) {
            emit(value = "Item $it")
            delay(1000L)
        }
    }

    fun triggerStateFlow() {
        _stateFlow.value = "StateFlow"
    }

    fun triggerSharedFlow() {
        viewModelScope.launch {
            _sharedFlow.emit(value = "SharedFlow ")
        }
    }
}
