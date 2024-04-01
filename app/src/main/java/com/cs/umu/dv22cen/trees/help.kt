package com.cs.umu.dv22cen.trees

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

/**
 * Help activity for Umeå Trees.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
class help: AppCompatActivity()  {
    private var total: Int = 0

    /**
     * Runs when activity is created.
     * @param savedInstanceState Possible bundle of saved data
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = "Umeå Träd"
        total = intent.getIntExtra("com.cs.umu.dv22cen.trees.total", 0)

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
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", total)
            finish()
            this.startActivity(myIntent)
            true
        }

        R.id.action_settings -> {
            val myIntent: Intent = Intent(
                this,
                settings::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", total)
            finish()
            startActivity(myIntent)
            true
        }

        R.id.help ->{
            val intent = intent
            finish()
            startActivity(intent)
            true
        }

        else -> {
            false
        }
    }
}