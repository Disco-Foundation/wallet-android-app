package disco.foundation.discowallet.components

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import disco.foundation.discowallet.R
import disco.foundation.discowallet.databinding.SampleWalletDetailsCardBinding

class WalletDetailsDialog(context: Context) : Dialog(context) {

    private lateinit var binding: SampleWalletDetailsCardBinding
    private var exportableText: String = ""

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = SampleWalletDetailsCardBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupControls(){
        binding.closeBtn.setOnClickListener { this.dismiss() }
        binding.copyBtn.setOnClickListener{
            val clipboard = ownerActivity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("address", binding.walletAddress.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, ownerActivity?.getString(R.string.address_copied), Toast.LENGTH_SHORT).show()
        }
        binding.exportBtn.setOnClickListener{
            val clipboard = ownerActivity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("exportable", exportableText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, ownerActivity?.getString(R.string.key_copied), Toast.LENGTH_SHORT).show()
        }
    }

    fun showDetails(address: String, exportable: String){
        super.show()
        binding.walletAddress.text = address
        exportableText = exportable
        setupControls()
    }
}