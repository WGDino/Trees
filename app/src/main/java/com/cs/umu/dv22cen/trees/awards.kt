package com.cs.umu.dv22cen.trees

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Awards activity for Umeå Trees.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
class awards: AppCompatActivity()  {
    private lateinit var database:TreeDatabase
    private lateinit var treeDao: TreeDao
    private var treeTotal: Int = 0
    private var treeFound: Int = 0
    private var amounts = arrayOf(10, 100, 500, 1000, 2500, 5000, 10000, 20000, 30878)//default max to current max
    private var drawables = arrayOf(R.drawable.dirt, R.drawable.seed, R.drawable.firts,
        R.drawable.second, R.drawable.tree1, R.drawable.tree2, R.drawable.tree3, R.drawable.tree4,
        R.drawable.finaltree)
    private lateinit var imageViews: List<ImageView>

    /**
     * Runs when activity is created.
     * @param savedInstanceState Possible bundle of saved data
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.awards)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = "Umeå Träd"

        imageViews = listOf(findViewById(R.id.imageView10), findViewById(R.id.imageView100),
            findViewById(R.id.imageView500), findViewById(R.id.imageView1000),
            findViewById(R.id.imageView2500), findViewById(R.id.imageView5000),
            findViewById(R.id.imageView10000), findViewById(R.id.imageView20000),
            findViewById(R.id.imageViewAll))

        database = Room.databaseBuilder(
            this,
            TreeDatabase::class.java, "tree_database.db"
        ).fallbackToDestructiveMigration().build()

        treeTotal = intent.getIntExtra("com.cs.umu.dv22cen.trees.total", 0)
        amounts[8] = treeTotal
        treeDao = database.treeDao()

        lifecycleScope.launch {
            getTrees()
        }
    }

    /**
     * Gets trees and writes amounts found and amount left.
     */
    private suspend fun getTrees(){
        treeFound = treeDao.getRowCount()

        withContext (Dispatchers.Main) {
            for (i in 0..8){
                if(treeFound >= amounts[i]){
                    imageViews[i].setImageResource(drawables[i])
                }
            }
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
            val myIntent: Intent = Intent(
                this,
                listDataBase::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", treeTotal)
            finish()
            this.startActivity(myIntent)
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
            val intent = intent
            finish()
            this.startActivity(intent)
            true
        }

        else -> {
            false
        }
    }
}