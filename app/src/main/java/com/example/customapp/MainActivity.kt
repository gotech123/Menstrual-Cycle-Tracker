package com.example.customapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var localDateToday : LocalDate
    private lateinit var nextOvulationDate : LocalDate
    private lateinit var nextPeriodDate : LocalDate
    private lateinit var mDB: AppDatabase
    private lateinit var executor: Executor
    private lateinit var vDaysOvulation: TextView
    private lateinit var vDaysPeriod: TextView
    private lateinit var vDateOvulation: TextView
    private lateinit var vDatePeriod: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()

        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        val drawerToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        val dateToday = findViewById<TextView>(R.id.date_today)
        val yearToday = findViewById<TextView>(R.id.year_today)
        val logPeriod = findViewById<Button>(R.id.log_period)

        vDaysOvulation = findViewById<TextView>(R.id.days_ovulation)
        vDaysPeriod = findViewById<TextView>(R.id.days_period)
        vDateOvulation = findViewById<TextView>(R.id.date_ovulation)
        vDatePeriod = findViewById<TextView>(R.id.date_period)

        localDateToday = LocalDate.now()
        val year = localDateToday.year
        val month = localDateToday.monthValue
        val day = localDateToday.dayOfMonth
        dateToday.text = "${day.toString()} - ${month.toString()}"
        yearToday.text = "${year.toString()}"


        executor.execute {
            val records = mDB.periodDateDao().getAll()
            if(records.isEmpty()) {
                val i = Intent(this, LogPeriodActivity::class.java)
                startActivityForResult(i, 0)
            }
            else {
                val record = mDB.periodDateDao().get()
                calculate( record.date, record.cycleDays.toLong())
            }

            logPeriod.setOnClickListener {
                val i = Intent(this, LogPeriodActivity::class.java)
                startActivityForResult(i, 0)
            }

        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


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


    private fun init() {
        mDB = AppDatabase.getInstance(this)!!
        executor = Executors.newSingleThreadExecutor()
    }


    private fun calculate(lastPeriodDate: LocalDate, cycleDays: Long) {
        val ovulationDays = cycleDays / 2

        nextPeriodDate = lastPeriodDate.plusDays(cycleDays)
        nextOvulationDate = lastPeriodDate.plusDays(ovulationDays)

        while(nextOvulationDate.isBefore(localDateToday)) {
            nextOvulationDate = nextPeriodDate.plusDays(ovulationDays)
        }

        while(nextPeriodDate.isBefore(localDateToday)) {
            nextPeriodDate = nextPeriodDate.plusDays(cycleDays)
        }
        val daysToPeriod = ChronoUnit.DAYS.between(localDateToday, nextPeriodDate)

        val daysToOvulation = ChronoUnit.DAYS.between(localDateToday, nextOvulationDate)

        setText(nextPeriodDate, daysToPeriod, nextOvulationDate, daysToOvulation)
    }

    private fun setText(nextPeriodDate: LocalDate, daysToPeriod: Long, nextOvulationDate: LocalDate, daysToOvulation: Long) {
        vDaysOvulation.text = "${daysToOvulation.toString()} days"
        vDaysPeriod.text = "${daysToPeriod.toString()} days"
        vDateOvulation.text = nextOvulationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        vDatePeriod.text = nextPeriodDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == 0) {
            data?.getParcelableExtra<PeriodParcel>("updatedPeriodDate")?.let {
                val lastPeriodDate = it.date
                val cycleDays = it.cycleDays!!.toLong()

                executor.execute {
                    mDB.periodDateDao().deleteAll()
                    mDB.periodDateDao().insert(PeriodDateEntity(lastPeriodDate, it.cycleDays))
                }
                calculate(lastPeriodDate, cycleDays)
            }
        }
    }

}

