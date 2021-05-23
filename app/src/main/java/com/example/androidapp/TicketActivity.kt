package com.example.androidapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.androidapp.model.CardInfo
import com.example.androidapp.model.DataModel

class TicketActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_TICKET_ID = "1"

        fun getIntent(context: Context, ticketId: String): Intent {
            val intent = Intent(context, TicketActivity::class.java)
            intent.putExtra(EXTRA_TICKET_ID, ticketId)
            return intent
        }
    }

    private lateinit var toolbar: Toolbar

    private lateinit var cardInfo: CardInfo

    private lateinit var descriptionTicketTextView: TextView
    private lateinit var registrarTextView: TextView
    private lateinit var critLevelTextView: TextView
    private lateinit var errorDateTicketTextView: TextView
    private lateinit var finishDateTicketTextView: TextView
    private lateinit var systemTextView: TextView
    private lateinit var statusTicketTextView: TextView
    private lateinit var deviationTextView: TextView
    private lateinit var timeLengthTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)

        initViews()

        val id = intent.extras!!.getString(EXTRA_TICKET_ID)!!
        cardInfo = DataModel.getCardById(id)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?

        setUpViews()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_ticket)

        descriptionTicketTextView = findViewById(R.id.description_ticket_text_view)
        registrarTextView = findViewById(R.id.registrar_text_view)
        critLevelTextView = findViewById(R.id.crit_level_text_view)
        errorDateTicketTextView = findViewById(R.id.error_date_ticket_text_view)
        finishDateTicketTextView = findViewById(R.id.finish_date_ticket_text_view)
        systemTextView = findViewById(R.id.system_text_view)
        statusTicketTextView = findViewById(R.id.status_ticket_text_view)
        deviationTextView = findViewById(R.id.deviation_text_view)
        timeLengthTextView = findViewById(R.id.time_length_text_view)
    }

    private fun setUpViews() {
        descriptionTicketTextView.text = "${cardInfo.description} (${cardInfo.ticketid})"
        registrarTextView.text = cardInfo.reportedby
        critLevelTextView.text = cardInfo.criticLevel
        errorDateTicketTextView.text = Utils.formatDate(cardInfo.isknownerrordate)
        finishDateTicketTextView.text = Utils.formatDate(cardInfo.targetfinish)
        systemTextView.text = cardInfo.extsysname
        statusTicketTextView.text = cardInfo.status
        deviationTextView.text = cardInfo.norm
        timeLengthTextView.text = if (cardInfo.lnorm == "0") "Не указано" else cardInfo.lnorm
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}