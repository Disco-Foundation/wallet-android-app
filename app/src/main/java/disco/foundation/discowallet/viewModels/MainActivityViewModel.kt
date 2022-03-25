package disco.foundation.discowallet.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import disco.foundation.discowallet.api.models.RequestStatus
import disco.foundation.discowallet.data.ProtoDataStoreManager
import disco.foundation.discowallet.utils.singleArgViewModelFactory
import disco.foundation.discowallet.utils.solana.createNewWallet
import disco.foundation.discowallet.utils.solana.generateWallet
import kotlinx.coroutines.launch

class MainActivityViewModel (private val manager: ProtoDataStoreManager) : ViewModel() {
    companion object {
        val FACTORY = singleArgViewModelFactory(::MainActivityViewModel)
    }

    var loading: MutableLiveData<RequestStatus> = MutableLiveData()
    var errorMsg: String? = null
    var sKey: String? = null
    var pKey: String? = null

    fun getUW(){
        loading.postValue(RequestStatus.LOADING)
        viewModelScope.launch {
            val sArray = manager.get()
            if(sArray != null && sArray.isNotEmpty()){
                val byteArray = sArray.map { it.toByte() }
                pKey = generateWallet(byteArray.toByteArray()).publicKey.toBase58()
                sKey = sArray.toString()
                loading.postValue(RequestStatus.SUCCESS)
            } else {
                errorMsg = "There's no wallet yet, please create a new one"
                loading.postValue(RequestStatus.ERROR)
            }
        }
    }

    fun saveUW(){
        loading.postValue(RequestStatus.LOADING)
        viewModelScope.launch {
            try {
                val newAccount = createNewWallet()
                manager.clear()
                val values = newAccount.secretKey.contentToString()
                manager.save(convertToListOfInt(values))
                sKey = newAccount.secretKey.contentToString()
                loading.postValue(RequestStatus.SUCCESS)
            } catch (e: Exception){
                e.printStackTrace()
                errorMsg = "Something went wrong creating the new wallet"
                loading.postValue(RequestStatus.ERROR)
            }
        }
    }

    private fun convertToListOfInt(value: String): List<Int> {
        var newString = value.replace("]", "")
            .replace("[", "")
            .filter { !it.isWhitespace() }
        return newString.split(",").map { it.toInt() }
    }
}