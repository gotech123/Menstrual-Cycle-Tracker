package com.example.customapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class LogDiaryActivity : AppCompatActivity() {

    private lateinit var mDB: AppDatabase
    private lateinit var executor: Executor
    private lateinit var saveButton : Button
    private lateinit var cancelButton : Button
    private lateinit var dateText : TextView
    private lateinit var enterText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_diary)

        init()

        saveButton = findViewById<Button>(R.id.log_diary_save)
        cancelButton = findViewById<Button>(R.id.log_diary_cancel)
        dateText = findViewById(R.id.date_diary)
        enterText = findViewById<EditText>(R.id.enter_text_diary)

        intent.getParcelableExtra<DiaryParcel>("diaryParcel")?.let {
            val date = it.date
            dateText.text = date.format(DateTimeFormatter.ofPattern("dd-MM-YYYY"))
            val diaryText = it.diary

            cancelButton.setOnClickListener {
                val i = Intent(this, ShowDiaryActivity::class.java)
                startActivity(i)
            }

            // Save data on click
            saveButton.setOnClickListener {
                var errMsg = validateDate(date) + validateEntry()
                if (errMsg != "") {
                    onAlertDialog(errMsg)
                } else {
                    if (diaryText.isNullOrEmpty()) {
                        addDiary(date)
                    }
                    else {
                        editDiary(date)
                    }
                    val i = Intent(this, ShowDiaryActivity::class.java)
                    startActivity(i)
                    showToast()
                }
            }
        }
    }


    private fun init() {
        mDB = AppDatabase.getInstance(this)!!
        executor = Executors.newSingleThreadExecutor()
    }


    private fun addDiary(date: LocalDate) {
        executor.execute {
            mDB.diaryDao().insert(DiaryEntity(date, enterText.text.toString()))
        }
    }


    private fun editDiary(date: LocalDate) {
        executor.execute {
            mDB.diaryDao().update(DiaryEntity(date, enterText.text.toString()))
        }
    }

    private fun showToast() {
        val text = "New diary has been added"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    //Validate text input
    private fun validateEntry() : String {
        var errMsg = ""
        val entry = enterText.text.toString()
        if (entry == "") {
            errMsg = "Your diary entry cannot be blank. Please enter again."
        }
        return errMsg
    }

    //Validate date input
    private fun validateDate(date: LocalDate) : String {
        var errMsg = ""
        if (date.isBefore(LocalDate.of(2020, 10, 1)) || date.isAfter(LocalDate.of(2020, 12, 31))) {
            errMsg = "The beta version has limited calendar. Please select a date between 1/10/2020 and 31/12/2020.\n"
        }
        return errMsg
    }

    //Show alert for data validation
    private fun onAlertDialog(errMsg: String) {
        //Instantiate builder variable
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Unable to save diary")
        builder.setMessage(errMsg)
        builder.setPositiveButton(
            "OK") { dialog, id ->
        }
        builder.show()
    }
}