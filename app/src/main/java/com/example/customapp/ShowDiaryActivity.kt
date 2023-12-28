package com.example.customapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.properties.Delegates


class ShowDiaryActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var adapter: RowAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var textDiary: TextView
    private lateinit var textDateDiary: TextView
    private lateinit var mDB: AppDatabase
    private lateinit var executor: Executor
    private lateinit var selectedDiaryDate: LocalDate
    private var selectedDiary: String? = null
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_diary)
        setTitle(null);
        setSupportActionBar(findViewById(R.id.toolbar))

        //Create drawer navigation
        val drawerToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Enable click on navigation items
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        init()

        textDateDiary = findViewById(R.id.text_date_diary)
        textDiary = findViewById(R.id.text_diary)

        deleteButton = findViewById<Button>(R.id.delete_button)
        val editButton = findViewById<Button>(R.id.edit_button)

        val vCalendar = findViewById<RecyclerView>(R.id.calendarRecyclerView)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        vCalendar.layoutManager = linearLayoutManager

        var startDate = LocalDate.of(2020, 10, 1)
        val endDate = startDate.plusDays(90)

        var dateList = mutableListOf<LocalDate>()
        dateList.add(startDate)

        while(startDate.isBefore(endDate)) {
            startDate = startDate.plusDays(1)
            dateList.add(startDate)
        }
        adapter = RowAdapter(dateList) {
            getDiary(it)
            editButton.apply {
                isVisible = true
                isClickable = true
            }

            editButton.setOnClickListener {
                val diaryParcel = DiaryParcel(selectedDiaryDate, selectedDiary)
                val i = Intent(this, LogDiaryActivity::class.java).apply { putExtra("diaryParcel", diaryParcel) }
                selectedDiary = null
                startActivity(i)
            }
        }

        vCalendar.adapter = adapter
    }

    private fun init() {
        mDB = AppDatabase.getInstance(this)!!
        executor = Executors.newSingleThreadExecutor()
    }

    private fun deleteControl(visible: Boolean){
        deleteButton.apply {
            isVisible = visible
            isClickable = visible
        }
    }

    private fun getDiary(date: LocalDate) {
        selectedDiaryDate = date
        val dateString = date.toString()
        val formatDate = date.format(DateTimeFormatter.ofPattern("dd-MM-YYYY"))

        executor.execute {
            val record = mDB.diaryDao().get(dateString)
            runOnUiThread {
                textDateDiary.text = formatDate
                textDiary.text = record?.diary ?: "No diary"
                selectedDiary = if (record?.diary == null) {
                    deleteControl(false)
                    null
                } else {
                    deleteButton.setOnClickListener {
                        deleteDiary(date)
                    }
                    deleteControl(true)
                    record.diary
                }

            }
        }

    }

    // Delete data in database
    private fun deleteDiary(date: LocalDate) {
        executor.execute {
            mDB.diaryDao().delete(date.toString())
            getDiary(date)
        }
    }

    //Open drawer navigation
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Enable click on navigation items
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val i = Intent(this,MainActivity::class.java)
                startActivity(i) }
            R.id.nav_show_diary -> {
                val i = Intent(this,ShowDiaryActivity::class.java)
                startActivity(i) }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


}
