package disco.foundation.discowallet.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import disco.foundation.discowallet.api.models.RequestStatus
import disco.foundation.discowallet.data.ProtoDataStoreManager
import disco.foundation.discowallet.utils.singleArgViewModelFactory
import disco.foundation.discowallet.utils.solana.createNewWallet
import disco.foundation.discowallet.utils.solana.generateWallet
import disco.foundation.discowallet.utils.toByteArray
import disco.foundation.discowallet.utils.toListOfInt
import kotlinx.coroutines.launch

class MainActivityViewModel (private val manager: ProtoDataStoreManager) : ViewModel() {
    companion object {
        val FACTORY = singleArgViewModelFactory(::MainActivityViewModel)
    }

    var loading: MutableLiveData<RequestStatus> = MutableLiveData()
    var saving: MutableLiveData<RequestStatus> = MutableLiveData()
    var errorMsg: String? = null
    var sKey: String? = null
    var pKey: String? = null
    var showDetails: Boolean = false

    fun getUW(){
        loading.postValue(RequestStatus.LOADING)
        viewModelScope.launch {
            val uw = manager.get()
            if(uw != null && uw.isNotEmpty()){
                pKey = generateWallet(uw.toByteArray()).publicKey.toBase58()
                sKey = uw.toString()
                loading.postValue(RequestStatus.SUCCESS)
            } else {
                errorMsg = "There's no wallet yet, please create a new one"
                loading.postValue(RequestStatus.ERROR)
            }
        }
    }

    fun saveUW(){
        saving.postValue(RequestStatus.LOADING)
        viewModelScope.launch {
            try {
                val uw = createNewWallet()
                manager.clear()
                sKey = uw.secretKey.contentToString()
                manager.save(sKey!!.toListOfInt())
                saving.postValue(RequestStatus.SUCCESS)
            } catch (e: Exception){
                e.printStackTrace()
                errorMsg = "Something went wrong creating the new wallet"
                saving.postValue(RequestStatus.ERROR)
            }
        }
    }

    fun walletExist():Boolean{
        return pKey != null && sKey != null
    }

}