package com.himanshurawat.notes.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.analytics.FirebaseAnalytics
import com.himanshurawat.notes.R
import com.himanshurawat.notes.adapter.SearchItemAdapter
import com.himanshurawat.notes.viewmodel.SearchViewModel

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import com.himanshurawat.notes.utils.Constant
import org.jetbrains.anko.share


class Search : AppCompatActivity(), SearchView.OnQueryTextListener, SearchItemAdapter.OnSearchItemClickListener {


    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onItemClick(id: Long) {
        val intent = Intent(Search@this,AddNote::class.java)
        intent.putExtra(Constant.GET_NOTES,id)
        startActivity(intent)
        finish()
    }


    lateinit var adapter: SearchItemAdapter
    lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        adapter = SearchItemAdapter(this,arrayListOf(),this)

        //Setting Up Recycler View
        content_search_recycler_view.adapter = adapter
        content_search_recycler_view.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false)

        searchViewModel = SearchViewModel(this.application)

        searchViewModel.getAllNotes().observe(this, Observer {
            if(it != null) {
                adapter.addSearchList(it)
                adapter.notifyDataSetChanged()
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        if(menu!=null) {
            val search: SearchView? = menu.findItem(R.id.search_menu_search).actionView as SearchView
            if(search!=null) {
                search.isIconified = false
                search.setIconifiedByDefault(true)
                search.maxWidth = Integer.MAX_VALUE
                search.setOnQueryTextListener(this)
            }
        }
        return true
    }



    override fun onQueryTextSubmit(query: String?): Boolean {

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null){
            adapter.filterSearch(newText)
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM,newText)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH,bundle)
        }
        return true
    }


}
