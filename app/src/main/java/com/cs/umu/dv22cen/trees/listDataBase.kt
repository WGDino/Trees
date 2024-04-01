package com.cs.umu.dv22cen.trees

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

/**
 * Statistics of found trees activity.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
class listDataBase: AppCompatActivity() {
    private lateinit var database:TreeDatabase
    private lateinit var treeDao: TreeDao
    private lateinit var amountText: TextView
    private lateinit var amountLeft: TextView
    private var treeFound: Int = 0
    private var treeTotal: Int = 0

    /**
     * Runs when activity is created.
     * @param savedInstanceState Possible bundle of saved data
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listdatabase)
        val intent = intent
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = "Umeå Träd"

        database = Room.databaseBuilder(
            this,
            TreeDatabase::class.java, "tree_database.db"
        ).fallbackToDestructiveMigration().build()
        treeTotal = intent.getIntExtra("com.cs.umu.dv22cen.trees.total", 0)
        treeDao = database.treeDao()

        amountText = findViewById(R.id.treeAmount)
        amountLeft = findViewById(R.id.treeAmountLeft)

        lifecycleScope.launch {
            getTrees()
        }
    }

    /**
     * Gets trees and writes amounts found and amount left.
     */
    private suspend fun getTrees(){
        treeFound = treeDao.getRowCount()
        val treesLeft = treeFound
        withContext (Dispatchers.Main) {
            amountText.text = treesLeft.toString()
            amountLeft.text = (treeTotal - treeFound).toString()
        }
    }

    /**
     * Creates the APPbar.
     * @param menu1 The menu.
     * @return true
     */
    override fun onCreateOptionsMenu(menu1: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar, menu1)
        return true
    }

    /**
     * Runs when an item in appbar is pressed.
     * @param item the Item.
     * @return true/false if item identified and handled.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.treeDex -> {
            val intent = intent
            finish()
            startActivity(intent)
            true
        }

        R.id.action_settings -> {
            val myIntent: Intent = Intent(
                this,
                settings::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", treeTotal)
            finish()
            this.startActivity(myIntent)
            true
        }

        R.id.help ->{
            val myIntent: Intent = Intent(
                this,
                help::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", treeTotal)
            finish()
            this.startActivity(myIntent)
            true
        }

        R.id.award ->{
            val myIntent: Intent = Intent(
                this,
                awards::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", treeTotal)
            finish()
            this.startActivity(myIntent)
            true
        }

        else -> {
            false
        }
    }
}