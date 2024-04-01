package com.cs.umu.dv22cen.trees

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Settings activity fro Umeå Träd.
 * Sätter radien för att hitta träd och mängden träd på kartan.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
class settings : AppCompatActivity() {
    private lateinit var radius: SeekBar
    private lateinit var amountTrees: SeekBar
    private lateinit var choose: Button
    private var distance: Int = 50
    private var amount: Int = 20
    private var total: Int = 0

    /**
     * Runs when activity is created.
     * @param savedInstanceState Possible bundle of saved data
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        radius = findViewById(R.id.seekBar)
        amountTrees = findViewById(R.id.seekBar2)
        choose = findViewById(R.id.button)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = "Umeå Träd"

        total = intent.getIntExtra("com.cs.umu.dv22cen.trees.total", 0)

        choose.setOnClickListener{
            val myIntent: Intent = Intent(
                this,
                MainActivity::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.radius", distance)
            myIntent.putExtra("com.cs.umu.dv22cen.trees.amount", amount)
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finish()
            this.startActivity(myIntent)
        }

        radius.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                Toast.makeText(this@settings,
                    "Valt värde: " + seek.progress*10 + "m",
                    Toast.LENGTH_SHORT).show()
                distance = seek.progress*10
            }
        })

        amountTrees.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                Toast.makeText(this@settings,
                    "Valt värde: " + seek.progress*4 + "st",
                    Toast.LENGTH_SHORT).show()
                amount = seek.progress*4
            }
        })
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
            val intent = intent
            finish()
            startActivity(intent)
            true
        }

        R.id.help ->{
            val myIntent: Intent = Intent(
                this,
                help::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", total)
            finish()
            this.startActivity(myIntent)
            true
        }

        R.id.award ->{
            val myIntent: Intent = Intent(
                this,
                awards::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", total)
            finish()
            this.startActivity(myIntent)
            true
        }

        else -> {
            false
        }
    }
}