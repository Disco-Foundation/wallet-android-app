package disco.foundation.discowallet.api.models

import com.google.gson.annotations.SerializedName


data class RawTransactionInstructionKeys(
    @SerializedName("pubkey") val pubkey: String,
    @SerializedName("isWritable") val isWritable: Boolean,
    @SerializedName("isSigner") val isSigner: Boolean,
)

data class RawTransactionInstructionData(
    @SerializedName("type") val type: String,
    @SerializedName("data") val data: Array<Int>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawTransactionInstructionData

        if (type != other.type) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

data class RawTransactionInstruction(
    @SerializedName("keys") val keys: Array<RawTransactionInstructionKeys>,
    @SerializedName("programId") val programId: String,
    @SerializedName("data") val data: RawTransactionInstructionData
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawTransactionInstruction

        if (!keys.contentEquals(other.keys)) return false
        if (programId != other.programId) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keys.contentHashCode()
        result = 31 * result + programId.hashCode()
        result = 31 * result + data.hashCode()
        return result
    }
}

data class RawTransaction (
    @SerializedName("signatures") val signatures: Array<Int>,
    @SerializedName("instructions") val instructions: Array<RawTransactionInstruction>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawTransaction

        if (!signatures.contentEquals(other.signatures)) return false
        if (!instructions.contentEquals(other.instructions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = signatures.contentHashCode()
        result = 31 * result + instructions.contentHashCode()
        return result
    }
}
