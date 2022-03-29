package disco.foundation.discowallet.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import disco.foundation.discowallet.R
import disco.foundation.discowallet.activities.PayActivity
import disco.foundation.discowallet.api.models.RequestStatus
import disco.foundation.discowallet.components.CustomDialog
import disco.foundation.discowallet.data.ProtoDataStoreManager
import disco.foundation.discowallet.databinding.FragmentPayFragmentBinding
import disco.foundation.discowallet.utils.solana.models.CheckinTransaction
import disco.foundation.discowallet.utils.solana.parsers.parseBasicData
import disco.foundation.discowallet.utils.solana.parsers.parseCheckinTransaction
import disco.foundation.discowallet.viewModels.MainActivityViewModel
import disco.foundation.discowallet.viewModels.PayActivityViewModel

class PayFragment : Fragment() {
    private lateinit var binding: FragmentPayFragmentBinding
    private lateinit var viewModel: PayActivityViewModel
    private lateinit var dialog: CustomDialog

    companion object {
        const val ARG_DATA = "data"

        fun newInstance(name: String): PayFragment {
            val fragment = PayFragment()
            val bundle = Bundle().apply {
                putString(ARG_DATA, name)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPayFragmentBinding.inflate(inflater, container,false)
        dialog = CustomDialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            PayActivityViewModel.FACTORY(ProtoDataStoreManager(requireContext()))
        )[PayActivityViewModel::class.java]
        dialog = CustomDialog(requireContext())
        subscribeToData()
        setupUI()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(){
        val savedInfo = arguments?.getString(ARG_DATA)
        if (savedInfo != null) {

            val qrData = parseBasicData(savedInfo)
            println("QR DATA $qrData")
            binding.infoTextView.text = "Action: ${qrData?.action}"
            // setup UI
            binding.actionButton.setupAnimation {
                if (qrData != null) {
                    action(qrData.action, savedInfo)
                }
            }
            binding.actionButton.buttonText = getString(R.string.pay)
        }
    }

    private fun subscribeToData(){
        viewModel.loading.observe(viewLifecycleOwner){
            when(it){
                RequestStatus.LOADING -> dialog.showPopup(
                    "Sending transaction", false
                )
                RequestStatus.SUCCESS -> dialog.update(
                    "Transaction send, it will confirmed in a few moments",
                    true,
                    "Continue", action = ::goToMain
                )
                RequestStatus.ERROR -> dialog.update(
                    "Something went wrong",
                    true,
                    "Try again", action = ::finalize
                )
                else -> { dialog.dismiss() }
            }
        }
    }

    private fun finalize(){
        dialog.dismiss()
        activity?.finish()
    }

    private fun goToMain(){
        dialog.dismiss()
        (activity as PayActivity).goToMainActivity()
    }

    private fun action(action: String, rawQrData: String){
        viewModel.sendTransaction(action, rawQrData)
    }

}