package disco.foundation.discowallet.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import disco.foundation.discowallet.ProtoUW
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

class ProtoDataStoreManager(context: Context){
    private val uwDatastore = context.uwDataStore

    suspend fun save(secret: List<Int>){
        withContext(Dispatchers.IO){
            try{
                uwDatastore.updateData {
                    it.toBuilder().addAllSecret(secret).build()
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    suspend fun get(): List<Int>? {
        return withContext(Dispatchers.IO){
            try {
                val data = uwDatastore.data.first()
                withContext(Dispatchers.Main){ data.secretList }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { null }
            }
        }
    }

    suspend fun clear(){
        withContext(Dispatchers.IO) {
            uwDatastore.updateData {
                it.toBuilder().clear().build()
            }
        }
    }
}

object ProtoSerializer: Serializer<ProtoUW> {
    override val defaultValue: ProtoUW = ProtoUW.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): ProtoUW {
        return try {
            ProtoUW.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }
    override suspend fun writeTo(t: ProtoUW, output: OutputStream) = t.writeTo(output)
}

private const val DATA_STORE_FILE_NAME = "uw.pb"
val Context.uwDataStore: DataStore<ProtoUW> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = ProtoSerializer
)
