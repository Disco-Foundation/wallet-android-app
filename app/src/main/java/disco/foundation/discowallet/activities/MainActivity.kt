package disco.foundation.discowallet.activities

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import disco.foundation.discowallet.R
import disco.foundation.discowallet.api.models.RequestStatus
import disco.foundation.discowallet.components.CustomDialog
import disco.foundation.discowallet.components.WalletDetailsDialog
import disco.foundation.discowallet.data.ProtoDataStoreManager
import disco.foundation.discowallet.databinding.ActivityMainBinding
import disco.foundation.discowallet.utils.solana.createNewWallet
import disco.foundation.discowallet.viewModels.MainActivityViewModel
import okio.ByteString.Companion.toByteString

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: CustomDialog
    private lateinit var walletDetails: WalletDetailsDialog
    private lateinit var viewModel: MainActivityViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // init view model
        viewModel = ViewModelProvider(
            this,
            MainActivityViewModel.FACTORY(ProtoDataStoreManager(this))
        )[MainActivityViewModel::class.java]

        // setup UI
        setupUI()
        // setup observables
        subscribeToData()
    }

    private fun setupUI() {
        dialog = CustomDialog(this)
        walletDetails = WalletDetailsDialog(this)
        walletDetails.setOwnerActivity(this)
        binding.actionButton.setupAnimation { goToReadQR() }
        binding.actionButton.buttonText = getString(R.string.camera_qr)
        setupMenu()
    }

    private fun goToReadQR() {
        val intent = Intent(this, ReadQrCamera::class.java)
        startActivity(intent)
    }

    @SuppressLint("NewApi")
    private fun setupMenu() {
        val showPopUp = PopupMenu(
            this,
            binding.menuBtn
        )
        showPopUp.inflate(R.menu.settings_menu)
        showPopUp.setForceShowIcon(true)
        showPopUp.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item1 -> {
                    viewModel.getUW()
                }
                R.id.item2 -> {
                    viewModel.saveUW()
                }
            }
            false
        }
        binding.menuBtn.setOnClickListener {
            showPopUp.show()
        }
    }

    private fun subscribeToData() {
        viewModel.loading.observe(this) {
            when (it) {
                RequestStatus.LOADING -> {
                    dialog.showPopup("Loading...", false)
                }
                RequestStatus.SUCCESS -> {
                    if (viewModel.pKey == null) {
                        dialog.update(
                            "Wallet created successfully, " +
                                    "please store your secret key somewhere save",
                            true, "Export", ::exportKey)
                    } else {
                        dismiss()
                        if (viewModel.pKey !== null && viewModel.sKey != null) {
                            walletDetails.showDetails(viewModel.pKey!!, viewModel.sKey!!)
                        }
                    }
                }
                else -> {
                    dialog.update(
                        viewModel.errorMsg ?: "ERROR",
                        true, "Ok", action = ::dismiss)
                }
            }
        }
    }

    private fun dismiss(){
        dialog.dismiss()
    }


    private fun exportKey(){
        dismiss()
        val clipboard =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("exportable", viewModel.sKey)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.key_copied), Toast.LENGTH_SHORT)
            .show()
    }

}