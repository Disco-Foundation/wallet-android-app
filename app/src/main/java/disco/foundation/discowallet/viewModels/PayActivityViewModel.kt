package disco.foundation.discowallet.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import disco.foundation.discowallet.api.models.RequestResult
import disco.foundation.discowallet.api.models.RequestStatus
import disco.foundation.discowallet.utils.solana.createNewWallet
import disco.foundation.discowallet.utils.solana.models.CheckinTransaction
import disco.foundation.discowallet.utils.solana.models.RechargeTransaction
import disco.foundation.discowallet.utils.solana.parsers.parseCheckinTransaction
import disco.foundation.discowallet.utils.solana.parsers.parseRechargeTransaction
import disco.foundation.discowallet.utils.solana.signCheckinTransaction
import disco.foundation.discowallet.utils.solana.signRechargeTransaction
import kotlinx.coroutines.launch



class PayActivityViewModel: ViewModel(){

    var loading: MutableLiveData<RequestStatus> = MutableLiveData()

    fun sendTransaction(action: String, rawQrData: String){
        if (action == "CHECKIN") {
            val qrData = parseCheckinTransaction(rawQrData);
            if(qrData != null) {
                loading.value = RequestStatus.LOADING
                viewModelScope.launch{
                    when(signCheckinTransaction(createNewWallet(), qrData)) {
                        is RequestResult.Success -> loading.value = RequestStatus.SUCCESS
                        is RequestResult.Error -> loading.value = RequestStatus.ERROR
                        else -> {}
                    }
                }
            }
        } else if (action == "RECHARGE") {
            val qrData = parseRechargeTransaction(rawQrData);
            if (qrData != null) {
                loading.value = RequestStatus.LOADING
                viewModelScope.launch{
                    when(signRechargeTransaction(createNewWallet(), qrData)) {
                        is RequestResult.Success -> loading.value = RequestStatus.SUCCESS
                        is RequestResult.Error -> loading.value = RequestStatus.ERROR
                        else -> {}
                    }
                }
            }

        }
    }
}

