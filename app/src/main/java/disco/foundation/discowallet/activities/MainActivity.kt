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
import disco.foundation.discowallet.viewModels.MainActivityViewModel

/**
 * Main Activity
 */
class MainActivity : AppCompatActivity() {

    /* late init variables to manage the activity view */
    private lateinit var binding: ActivityMainBinding       /* main activity view */
    private lateinit var dialog: CustomDialog               /* progress dialog */
    private lateinit var walletDetails: WalletDetailsDialog /* wallet info dialog */
    private lateinit var viewModel: MainActivityViewModel   /* main activity view model */

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* init activity view */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* init view model */
        viewModel = ViewModelProvider(
            this,
            MainActivityViewModel.FACTORY(ProtoDataStoreManager(this))
        )[MainActivityViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        setupUI()          /* setup activity view */
        subscribeToData()  /* setup activity data observables */
    }

    /**
    *  Setup all the elements of the activity view
    **/
    private fun setupUI() {
        setupButtons()  /* setup buttons */
        setupDialogs()  /* setup dialogs */
        setupMenu()     /* setup right menu */
    }

    private fun setupButtons(){
        binding.actionButton.setupAnimation { goToReadQR() }
        binding.actionButton.buttonText = getString(R.string.camera_qr)
    }

    private fun setupDialogs(){
        dialog = CustomDialog(this)
        walletDetails = WalletDetailsDialog(this)
        walletDetails.setOwnerActivity(this)
    }

    private fun goToReadQR() {
        if(viewModel.walletExist()) {
            val intent = Intent(this, ReadQrCamera::class.java)
            startActivity(intent)
        } else { showError() }
    }

    @SuppressLint("NewApi")
    private fun setupMenu() {
        val showPopUp = PopupMenu(this, binding.menuBtn)

        showPopUp.inflate(R.menu.settings_menu)
        showPopUp.setForceShowIcon(true)
        showPopUp.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item1 -> {
                    viewModel.showDetails = true
                    viewModel.getUW()
                }
                R.id.item2 -> { viewModel.saveUW() }
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
                RequestStatus.LOADING -> { showLoading() }
                RequestStatus.SUCCESS -> { showWalletDetails()  }
                else -> { showError() }
            }
        }

        viewModel.saving.observe(this) {
            when (it) {
                RequestStatus.LOADING -> { showLoading() }
                RequestStatus.SUCCESS -> { showWalletCreated() }
                else -> { showError() }
            }
        }

        viewModel.getUW()
    }

    private fun dismiss(){ if(dialog.isShowing) { dialog.dismiss() } }

    private fun exportKey(){
        dismiss()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(getString(R.string.exportable), viewModel.sKey)

        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.key_copied), Toast.LENGTH_SHORT)
            .show()
    }

    private fun showError(){
        dialog.update(
            getString(viewModel.errorMsg ?: R.string.something_went_wrong),
            true, getString(R.string.ok), action = ::dismiss)
    }

    private fun showLoading(){
        dialog.showPopup(getString(R.string.loading), false)
    }

    private fun showWalletCreated(){
        dialog.update(getString(R.string.wallet_created),
            true, getString(R.string.export), ::exportKey)
    }

    private fun showWalletDetails(){
        dismiss()
        if (viewModel.pKey !== null && viewModel.sKey != null && viewModel.showDetails) {
            walletDetails.showDetails(viewModel.pKey!!, viewModel.sKey!!)
        }
    }

}