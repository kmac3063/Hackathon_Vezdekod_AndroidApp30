package com.example.androidapp

import android.app.SearchManager
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var searchRecyclerView: RecyclerView
    lateinit var recyclerView: RecyclerView
    lateinit var shadeView: View

    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView

    private var adapter: CardAdapter? = null
    private var searchAdapter: SearchAdapter? = null

    private var searchEnable = false
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DataModel.onAttach(this)

        initViews()
        searchIsEnable(false)

        recyclerView.layoutManager = LinearLayoutManager(this)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        GlobalScope.launch {
            withContext(Dispatchers.Default) {
                val data = DataModel.getCardsFromSDCARD()
                Log.d("gog", data.toString())
                withContext(Dispatchers.Main) {
                    adapter = CardAdapter(data) { id ->
                        val intent = TicketActivity.getIntent(this@MainActivity, id)
                        this@MainActivity.startActivity(intent)
                    }
                    recyclerView.adapter = adapter
                    searchAdapter = SearchAdapter(data) { id ->
                        val intent = TicketActivity.getIntent(this@MainActivity, id)
                        this@MainActivity.startActivity(intent)
                    }
                    searchRecyclerView.adapter = searchAdapter
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
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView = menu!!.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchAdapter?.filter?.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query?.isEmpty() == true) {
                    searchIsEnable(false)
                } else if (!searchEnable) {
                    searchIsEnable(true)
                }
                this@MainActivity.query = query!!
                searchAdapter?.filter?.filter(query)
                return false
            }
        })

        return true
    }

    private fun searchIsEnable(b: Boolean) {
        searchEnable = b
        searchRecyclerView.visibility = if (b) View.VISIBLE else View.GONE
        shadeView.visibility = if (b) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_search) {
            Log.d("gog", "R.id.action_search")
            searchIsEnable(true)
            return true;
        }
        Log.d("gog", "else")
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        searchRecyclerView = findViewById(R.id.search_recycler_view)
        recyclerView = findViewById(R.id.card_recycler_view)
        toolbar = findViewById(R.id.toolbar)
        shadeView = findViewById(R.id.shade_view)
    }

    inner class CardAdapter(list: List<CardInfo>, val callback: (String) -> Unit) :
        RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

        var items = list

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
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size
    }

    inner class SearchAdapter(list: List<CardInfo>, val callback: (String) -> Unit) :
        RecyclerView.Adapter<SearchAdapter.SearchViewHolder>(), Filterable {

        var items = list
        var itemsFiltered = items

        inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var searchTextView: TextView = itemView.findViewById(R.id.search_text_view)

            fun bind(cardInfo: CardInfo) {
                searchTextView.setOnClickListener {
                    callback(cardInfo.ticketid)
                }

                val startIndex = cardInfo.description.lowercase(Locale.getDefault())
                    .indexOf(query.lowercase(Locale.getDefault()))

                if (query.isEmpty() || startIndex == -1) {
                    searchTextView.text = cardInfo.description
                    return
                }


                val highlightedName = SpannableString(cardInfo.description)
                highlightedName.setSpan(
                    BackgroundColorSpan(Color.YELLOW), startIndex,
                    startIndex + query.length, 0
                )

                searchTextView.text = highlightedName
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_search_recycler, parent, false)
            return SearchViewHolder(view)
        }

        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
            holder.bind(itemsFiltered[position])
        }

        override fun getItemCount() = itemsFiltered.size

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val query = constraint!!.toString().lowercase(Locale.getDefault())
                    var filtered = ArrayList<CardInfo>()
                    if (query.isEmpty()) {
                        filtered = ArrayList(this@SearchAdapter.items)
                    } else {
                        for (card in items) {
                            if (card.description.lowercase(Locale.getDefault()).contains(query)) {
                                filtered.add(card)
                            }
                        }
                    }
                    val results = FilterResults()
                    results.count = filtered.size
                    results.values = filtered
                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    itemsFiltered = results!!.values as ArrayList<CardInfo>
                    notifyDataSetChanged()
                }

            }
        }
    }

}