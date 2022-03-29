package disco.foundation.discowallet.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import disco.foundation.discowallet.api.models.RequestResult
import disco.foundation.discowallet.api.models.RequestStatus
import disco.foundation.discowallet.data.ProtoDataStoreManager
import disco.foundation.discowallet.utils.singleArgViewModelFactory
import disco.foundation.discowallet.utils.solana.generateWallet
import disco.foundation.discowallet.utils.solana.parsers.parseCheckinTransaction
import disco.foundation.discowallet.utils.solana.parsers.parseRechargeTransaction
import disco.foundation.discowallet.utils.solana.signCheckinTransaction
import disco.foundation.discowallet.utils.solana.signRechargeTransaction
import disco.foundation.discowallet.utils.toByteArray
import kotlinx.coroutines.launch

class PayActivityViewModel (private val manager: ProtoDataStoreManager) : ViewModel() {
    companion object {
        val FACTORY = singleArgViewModelFactory(::PayActivityViewModel)
    }

    var loading: MutableLiveData<RequestStatus> = MutableLiveData()

    fun sendTransaction(action: String, rawQrData: String){
        if (action == "CHECKIN") {
            val qrData = parseCheckinTransaction(rawQrData)
            if(qrData != null) {
                loading.postValue(RequestStatus.LOADING)
                viewModelScope.launch{
                    val uw = manager.get()
                    if(uw != null && uw.isNotEmpty()){
                        when(signCheckinTransaction(generateWallet(uw.toByteArray()), qrData)) {
                            is RequestResult.Success -> loading.postValue(RequestStatus.SUCCESS)
                            is RequestResult.Error -> loading.postValue(RequestStatus.ERROR)
                            else -> {}
                        }
                    } else {
                        loading.postValue(RequestStatus.ERROR)
                    }
                }
            }
        } else if (action == "RECHARGE") {
            val qrData = parseRechargeTransaction(rawQrData)
            if (qrData != null) {
                loading.postValue(RequestStatus.LOADING)
                viewModelScope.launch{
                    val uw = manager.get()
                    if(uw != null && uw.isNotEmpty()) {
                        val result = signRechargeTransaction(generateWallet(uw.toByteArray()), qrData)
                        when (result) {
                            is RequestResult.Success -> loading.postValue(RequestStatus.SUCCESS)
                            is RequestResult.Error -> {
                                println("-__________________ERROR__________________")
                                println(result.message)
                                loading.postValue(RequestStatus.ERROR)
                            }
                            else -> {}
                        }
                    } else {
                        loading.postValue(RequestStatus.ERROR)
                    }
                }
            }
        }
    }
}

