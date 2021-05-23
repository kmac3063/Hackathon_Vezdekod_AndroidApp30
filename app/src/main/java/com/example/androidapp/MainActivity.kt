package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.model.CardInfo
import com.example.androidapp.model.DataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        DataModel.onAttach(this)

        initViews()

        recyclerView.layoutManager = LinearLayoutManager(this)
        GlobalScope.launch {
            withContext(Dispatchers.Default) {
                val data = DataModel.getCardsFromSDCARD()
                Log.d("gog", data.toString())
                withContext(Dispatchers.Main) {
                    recyclerView.adapter = CardAdapter(data) { id ->
                        val intent = TicketActivity.getIntent(this@MainActivity, id)
                        this@MainActivity.startActivity(intent)
                    }
                }
            }
        }

        setSupportActionBar(toolbar)

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        DataModel.onDetach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        return if (id == R.id.action_settings) {
//            true
//        } else super.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.card_recycler_view)
        toolbar = findViewById(R.id.toolbar)
    }

    class CardAdapter(var list: List<CardInfo>, val callback: (String) -> Unit) :
        RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
        inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var containerItemCardView: CardView = itemView.findViewById(R.id.item_card_view)

            var titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
            var descriptionTextView: TextView = itemView.findViewById(R.id.description_text_view)
            var errorDateTextView: TextView = itemView.findViewById(R.id.error_date_text_view)
            var finishDateTextView: TextView = itemView.findViewById(R.id.finish_date_text_view)
            var statusTextView: TextView = itemView.findViewById(R.id.status_text_view)

            fun bind(cardInfo: CardInfo) {
                containerItemCardView.setOnClickListener {
                    callback(cardInfo.ticketid)
                }
                titleTextView.text = cardInfo.extsysname
                descriptionTextView.text = cardInfo.description
                errorDateTextView.text = Utils.formatDate(cardInfo.isknownerrordate)
                finishDateTextView.text = Utils.formatDate((cardInfo.targetfinish))
                statusTextView.text = cardInfo.status
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_card_recycler, parent, false)
            return CardViewHolder(view)
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount() = list.size
    }
}