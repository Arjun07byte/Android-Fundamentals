package com.appify.secretwindow.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.airbnb.lottie.LottieAnimationView
import com.appify.secretwindow.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlin.random.nextInt

class PaytmConfirmationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var mainContent: NestedScrollView
    private lateinit var choiceContent: LinearLayout
    private lateinit var accList: Array<String>; private var payeeAccName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paytm_confirmation)
        mainContent = findViewById(R.id.main_content)
        choiceContent = findViewById(R.id.choice_content)
        accList = resources.getStringArray(R.array.bank_names_list)

        askChoice()
    }

    private fun askChoice() {
        mainContent.visibility = View.GONE; choiceContent.visibility = View.VISIBLE
        val btnDoneChoice: Button = findViewById(R.id.btnDoneChoice)
        btnDoneChoice.setOnClickListener { initUI() }

        val spinner: Spinner = findViewById(R.id.userBankAccountSpinner)
        val choiceAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.bank_names_list,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
        )
        choiceAdapter.also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }

        spinner.onItemSelectedListener = this
    }

    private fun initUI() {
        choiceContent.visibility = View.GONE; mainContent.visibility = View.VISIBLE

        val mediaPlayer = MediaPlayer.create(this, R.raw.paytm_payment_tune)
        mediaPlayer.start()

        val payAmountText: TextView = findViewById(R.id.txtView_payAmount)
        val stringFormatZeroAbsent = DecimalFormat("#,###.##")
        val amountDouble = intent.getDoubleExtra("pay_amount", 0.0)
        payAmountText.text =
            getString(R.string.amount_text, stringFormatZeroAbsent.format(amountDouble))

        val payeeNameText: TextView = findViewById(R.id.txtView_payeeName)
        payeeNameText.text = intent.getStringExtra("payee_name")

        val txtViewPayeeAccountName: TextView = findViewById(R.id.txtView_payeeAccName)
        txtViewPayeeAccountName.text = payeeAccName

        val btnPayeeName: Button = findViewById(R.id.btn_payeeName)
        val wordsList = intent.getStringExtra("payee_name")?.split("\\s+".toRegex())
        var wordsText = "";
        if (wordsList != null) {
            for (eachWord in wordsList) {
                wordsText += eachWord.first()
            }
        }
        btnPayeeName.text = wordsText

        val timeText: TextView = findViewById(R.id.txtView_time)
        val dateTimeFormatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)
        timeText.text = dateTimeFormatter.format(Date())

        val txnIdText: TextView = findViewById(R.id.txtView_txnId)
        txnIdText.text =
            getString(R.string.txnId_text, Random(System.currentTimeMillis()).nextInt(1000..9999))

        val lottieAnim: LottieAnimationView = findViewById(R.id.animView)
        lottieAnim.playAnimation()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        payeeAccName = accList[position].ifBlank { "UPI Linked Bank A/c" }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        payeeAccName = "UPI Linked Bank A/c"
    }
}