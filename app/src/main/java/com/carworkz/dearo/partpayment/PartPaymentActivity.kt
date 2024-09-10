package com.carworkz.dearo.partpayment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityPartPaymentBinding
import com.carworkz.dearo.databinding.LayoutPartPaymentTransactionDetailsBinding
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Utility
import java.util.Calendar
import java.util.GregorianCalendar
import javax.inject.Inject

class PartPaymentActivity : ScreenContainerActivity(), PartPaymentContract.View {
    private lateinit var binding: ActivityPartPaymentBinding

    @Inject
    lateinit var presenter: PartPaymentPresenter

    @Inject
    lateinit var pdfMediator: PdfMediator

    private lateinit var invoiceId: String
    private lateinit var displayInvoiceId: String

    // jobCardId is used to to get whatsapp message template, has no use if whatsapp is not enabled.
    private lateinit var jobCardId: String

    private var paymentType: String? = null

    private val methodMap = mapOf(
        "Cash" to "CASH",
        "Card" to "CARD",
        "Cheque" to "CHEQUE",
        "Bank Transfer" to "BANK_TRANSFER",
        "Wallet" to "WALLET",
        "Mobile Transfer" to "MOBILE_TRANSFER",
        "Upi" to "UPI",
        "Razor pay" to "RAZOR_PAY"
    )

    private var chequeDateCalendar = Calendar.getInstance()

    private lateinit var datePicker: DatePickerDialog
    private lateinit var datePickerDOD: DatePickerDialog

    private var selectedYear: Int = chequeDateCalendar.get(Calendar.YEAR)
    private var selectedMonth: Int = chequeDateCalendar.get(Calendar.MONTH)
    private var selectedDay: Int = chequeDateCalendar.get(Calendar.DAY_OF_MONTH)

    private var drawnDateServer: String = ""
    private var chequeDateServer: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        invoiceId = intent!!.extras!!.getString(ARG_INVOICE_ID)!! // "1526307583973749"//
        displayInvoiceId = intent!!.extras!!.getString(ARG_DISPLAY_INVOICE_ID)!! // "INV-008"//
        jobCardId = intent!!.extras!!.getString(ARG_DISPLAY_JOBCARD_ID)!! // "INV-008"//
        super.onCreate(savedInstanceState)
        (application as DearOApplication).repositoryComponent.COMPONENT(
                PartPaymentPresenterModule(
                    this
                )
            ).inject(this)
        initViews()
        if (checkIfNetworkAvailable()) {
            presenter.getPayments(invoiceId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun createScreenContainer(): ScreenContainer =
        SingleTextActionScreenContainer(object : ToolBarInteractionProvider {
            override fun getToolBarTitle(): String = displayInvoiceId
            override fun getActionBtnTitle(): String = ""
            override fun onActionBtnClick() = Unit
        })

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityPartPaymentBinding.inflate(layoutInflater)
        return binding
    }

    override fun getProgressView(): View = ProgressBar(this)

    override fun onPaymentFetched(invoice: Invoice) {
        binding.amountView.setText(invoice.outStandingAmount.toString())
        binding.totalAmountDueView.text = String.format(
            getString(R.string.part_payment_total_amount_due), Utility.round(
                invoice.outStandingAmount ?: 0.0, 1
            )
        )
        binding.layPartPaySummary.totalAmtDueView.text =
            Utility.convertToCurrency(invoice.outStandingAmount)
        binding.layPartPaySummary.totalAmtReceivedView.text =
            Utility.convertToCurrency(invoice.totalPaidAmount)
        binding.layPartPaySummary.totalInvoiceAmtView.text = Utility.convertToCurrency(
            (invoice.totalPaidAmount ?: 0.0).plus(
                invoice.outStandingAmount ?: 0.0
            )
        )

        invoice.payments?.forEach { payment ->
            // Inflate the layout using ViewBinding
            val bindingPartPayment =
                LayoutPartPaymentTransactionDetailsBinding.inflate(layoutInflater)

            @SuppressLint("SetTextI18n")
            bindingPartPayment.transactionDetailsView.text = "${
                methodMap.entries.find { it.value == payment.method }?.key ?: ""
            } ${payment.remarks?.let { "/$it" } ?: ""}"

            bindingPartPayment.transactionDetailsAmtView.text =
                Utility.convertToCurrency(payment.amount)
            bindingPartPayment.transactionDetailsDateView.text = Utility.formatDate(
                payment.createdOn, Utility.TIMESTAMP, Utility.DATE_FORMAT_10, Utility.TIMEZONE_UTC
            )
            bindingPartPayment.receiptNumber.text = "Receipt Number: " + payment.receiptNumber
            bindingPartPayment.viewReceipt.setOnClickListener {
                // Show payment PDF
                pdfMediator.startPaymentReceiptPdf(this, payment.id!!, payment.receiptNumber!!)
            }
            binding.partPaymentDetailsContainer.addView(binding.root)
        }

        binding.updatePaymentButton.setOnClickListener {

            if (isValidData() && binding.amountView.text.isNotEmpty() && binding.amountView.text.toString()
                    .toDouble() <= invoice.outStandingAmount ?: 0.0
            ) {
                if (checkIfNetworkAvailable()) {
                    // show notify check box only if full amount is being paid.
                    val showNotify = binding.amountView.text.toString()
                        .toDouble() >= invoice.outStandingAmount ?: 0.0 && SharedPrefHelper.isNotifyEnabled() && SharedPrefHelper.isNotifyOnCloseJC()

                    DialogFactory.notifyAlertDialog(this,
                        getString(R.string.part_payment_title_update_payment),
                        getString(R.string.part_payment_message_update_payment),
                        showNotify,
                        SharedPrefHelper.getDefaultOptionCloseJC(),
                        false,
                        object : DialogFactory.NotifyButtonListener {
                            override fun positiveButton(notify: Boolean?) {
                                if (binding.amountView.text.toString()
                                        .toDouble() == invoice.outStandingAmount ?: 0.0
                                ) {
                                    setResult(Activity.RESULT_OK)
                                }

                                presenter.updatePayment(
                                    invoiceId = invoiceId,
                                    jobCardId = jobCardId,
                                    paymentMethod = paymentType!!,
                                    amount = binding.amountView.text.toString().toDouble(),
                                    transactionNumber = binding.transactionNumber.text.toString(),
                                    transactionDetails = binding.transactionDetails.text.toString(),
                                    bankName = binding.bankName.text.toString(),
                                    cardNumber = binding.cardNumber.text.toString(),
                                    drawnOnDate = drawnDateServer,
                                    chequeDate = chequeDateServer,
                                    chequeNumber = binding.chequeNumber.text.toString(),
                                    remarks = binding.remark.text.toString(),
                                    notify ?: false
                                )
                            }

                            override fun neutralButton() {
                            }
                        }).show()
                }
            } else {
                if (chequeDateServer.isEmpty()) {
                    binding.chequeDate.error = "Select Date"
                } else if (drawnDateServer.isEmpty()) {
                    binding.drawnDate.error = "Select Date"
                } else if (binding.amountView.text.isEmpty()) {
                    binding.amountView.error = "Amount cannot be empty"
                } else {
                    binding.amountView.error = "Entered amount is greater than due amount"
                }
            }
        }

        if (invoice.status == Invoice.STATUS_PAID) {
            binding.addPaymentDetailParentView.visibility = View.GONE
        } else {
            binding.addPaymentDetailParentView.visibility = View.VISIBLE
        }
        binding.paymentMethodView.adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, Utility.getKeys(methodMap)
        )
        binding.paymentMethodView.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    paymentType = Utility.getKeys<String, String>(methodMap)[0]
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    paymentType =
                        methodMap[(view as? TextView)?.text ?: Utility.getKeys<String, String>(
                            methodMap
                        )[0]]

                    //check for paymentType with methodMap
                    //show and hide fields as per type
                    when (paymentType) {
                        "CASH" -> {
                            visiblePaymentFields(
                                transactionNumberVisible = false,
                                transactionDetailsVisible = true,
                                bankNameVisible = false,
                                cardNumberVisible = false,
                                chequeDateVisible = false,
                                chequeNumberVisible = false
                            )
                        }

                        "CARD" -> {
                            visiblePaymentFields(
                                transactionNumberVisible = false,
                                transactionDetailsVisible = true,
                                bankNameVisible = true,
                                cardNumberVisible = true,
                                chequeDateVisible = false,
                                chequeNumberVisible = false
                            )
                        }

                        "CHEQUE" -> {
                            visiblePaymentFields(
                                transactionNumberVisible = false,
                                transactionDetailsVisible = true,
                                bankNameVisible = true,
                                cardNumberVisible = false,
                                chequeDateVisible = true,
                                chequeNumberVisible = true
                            )
                        }

                        "BANK_TRANSFER" -> {
                            visiblePaymentFields(
                                transactionNumberVisible = true,
                                transactionDetailsVisible = true,
                                bankNameVisible = false,
                                cardNumberVisible = false,
                                chequeDateVisible = false,
                                chequeNumberVisible = false
                            )
                        }

                        "WALLET" -> {
                            visiblePaymentFields(
                                transactionNumberVisible = true,
                                transactionDetailsVisible = true,
                                bankNameVisible = false,
                                cardNumberVisible = false,
                                chequeDateVisible = false,
                                chequeNumberVisible = false
                            )
                        }

                        "MOBILE_TRANSFER" -> {
                            visiblePaymentFields(
                                transactionNumberVisible = true,
                                transactionDetailsVisible = true,
                                bankNameVisible = false,
                                cardNumberVisible = false,
                                chequeDateVisible = false,
                                chequeNumberVisible = false
                            )
                        }

                        "UPI" -> {
                            visiblePaymentFields(
                                transactionNumberVisible = true,
                                transactionDetailsVisible = true,
                                bankNameVisible = false,
                                cardNumberVisible = false,
                                chequeDateVisible = false,
                                chequeNumberVisible = false
                            )
                        }

                        "RAZOR_PAY" -> {
                            visiblePaymentFields(
                                transactionNumberVisible = true,
                                transactionDetailsVisible = true,
                                bankNameVisible = false,
                                cardNumberVisible = false,
                                chequeDateVisible = false,
                                chequeNumberVisible = false
                            )
                        }

                        else -> {
                            visiblePaymentFields(
                                transactionNumberVisible = false,
                                transactionDetailsVisible = false,
                                bankNameVisible = false,
                                cardNumberVisible = false,
                                chequeDateVisible = false,
                                chequeNumberVisible = false
                            )
                        }
                    }

                }
            }
    }

    private fun isValidData(): Boolean {
        if (paymentType?.equals("Cheque", true) == true) {
            if (chequeDateServer.isEmpty()) return false
            if (drawnDateServer.isEmpty()) return false
        }
        return true
    }

    private fun visiblePaymentFields(
        transactionNumberVisible: Boolean,
        transactionDetailsVisible: Boolean,
        bankNameVisible: Boolean,
        cardNumberVisible: Boolean,
        chequeDateVisible: Boolean,
        chequeNumberVisible: Boolean
    ) {
        binding.transactionNumberLayout.visibility =
            if (transactionNumberVisible) View.VISIBLE else View.GONE
        binding.transactionDetailsLayout.visibility =
            if (transactionDetailsVisible) View.VISIBLE else View.GONE
        binding.bankNameLayout.visibility = if (bankNameVisible) View.VISIBLE else View.GONE
        binding.cardNumberLayout.visibility = if (cardNumberVisible) View.VISIBLE else View.GONE
        binding.chequeDateLayout.visibility = if (chequeDateVisible) View.VISIBLE else View.GONE
        binding.chequeNumberLayout.visibility = if (chequeNumberVisible) View.VISIBLE else View.GONE

        //clear fields
        binding.bankName.text.clear()
        binding.cardNumber.text.clear()
        binding.drawnDate.text = getString(R.string.dummy_date)
        binding.chequeDate.text = getString(R.string.dummy_date)
        binding.chequeNumber.text.clear()
        binding.transactionNumber.text.clear()
        binding.transactionDetails.text.clear()
        binding.remark.text.clear()
        drawnDateServer = ""
        chequeDateServer = ""
    }

    override fun launchWhatsApp(contactNumber: String, message: String) {
        Utility.sendWhatsApp(this, contactNumber, message)
    }

    override fun onPaymentUpdateFinished() {
        toast("Payment Updated")
        finish()
    }

    override fun showProgressIndicator() {
        showProgressBar()
    }

    override fun dismissProgressIndicator() {
        dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        // just in case update payment fails reset result to avoid unnecessary consequences.
        setResult(Activity.RESULT_CANCELED)
        displayError(errorMsg)
    }

    private fun initViews() {
        binding.paymentMethodView.adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, Utility.getKeys(methodMap)
        )
        binding.paymentMethodView.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                }
            }

        datePicker = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->

            val date = GregorianCalendar(year, monthOfYear, dayOfMonth)
            binding.chequeDate.text =
                Utility.formatDate(Utility.DATE_FORMAT_3, year, monthOfYear, dayOfMonth)
            binding.chequeDate.error = null
            chequeDateServer = Utility.formatToServerTime(date.time, Utility.DATE_FORMAT_1)

        }, selectedYear, selectedMonth, selectedDay)

        datePickerDOD = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val date = GregorianCalendar(year, monthOfYear, dayOfMonth)
            binding.drawnDate.text =
                Utility.formatDate(Utility.DATE_FORMAT_3, year, monthOfYear, dayOfMonth)
            binding.drawnDate.error = null
            drawnDateServer = Utility.formatToServerTime(date.time, Utility.DATE_FORMAT_1)

        }, selectedYear, selectedMonth, selectedDay)


        binding.drawnDate.setOnClickListener {
            datePickerDOD.show()
        }
        binding.chequeDate.setOnClickListener {
            datePicker.show()
        }
    }

    companion object {
        private const val ARG_INVOICE_ID = "arg_invoice_id"
        private const val ARG_DISPLAY_INVOICE_ID = "arg_display_invoice_id"
        private const val ARG_DISPLAY_JOBCARD_ID = "arg_display_jobcard_id"

        fun getIntent(
            context: Context, invoiceId: String, displayInvoiceId: String, jobCardId: String
        ): Intent {
            return Intent(context, PartPaymentActivity::class.java).apply {
                putExtra(ARG_INVOICE_ID, invoiceId)
                putExtra(ARG_DISPLAY_INVOICE_ID, displayInvoiceId)
                putExtra(ARG_DISPLAY_JOBCARD_ID, jobCardId)
            }
        }
    }
}
