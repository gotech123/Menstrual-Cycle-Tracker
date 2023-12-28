package com.example.customapp
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

class LogPeriodActivity : AppCompatActivity() {
    lateinit var periodParcel: PeriodParcel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_period)
        val datePicker = findViewById<DatePicker>(R.id.date_picker)
        val vCycleDays = findViewById<EditText>(R.id.edit_cycle_date)
        val saveButton = findViewById<Button>(R.id.log_period_save)
        val cancelButton = findViewById<Button>(R.id.log_period_cancel)

        saveButton.setOnClickListener {
            val dateObject = LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
            val cycleDaysText = vCycleDays.text.toString()
            val cycleDays = cycleDaysText.toIntOrNull()
            val errMsg = validateDate(dateObject) + validateCycleDays(cycleDays)
            if (errMsg != "") {
                onAlertDialog(errMsg)
            }
            else {
                periodParcel = PeriodParcel(dateObject, cycleDays!!)
                val i = intent.apply { putExtra("updatedPeriodDate", periodParcel) }
                setResult(Activity.RESULT_OK, i)
                super.onBackPressed()
                showToast()
            }
        }

        cancelButton.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }

    //Validate date input
    private fun validateDate(date: LocalDate) : String {
        var errMsg = ""
        if (date.isAfter(LocalDate.now())) {
            errMsg = "You cannot select a future period date. Please select again.\n"
        }
        return errMsg
    }

    //Validate cycle days input
    private fun validateCycleDays(days: Int?) : String {
        var errMsg = ""
        if (days == null) {
            errMsg = "Your cycle length must be a number. Please enter again."
        }
        else if (days < 10) {
            errMsg = "Your cycle length must not be less than 10 days. Please enter again."
        }
        return errMsg
    }

    //Show alert for data validation
    private fun onAlertDialog(errMsg: String) {
        //Instantiate builder variable
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Unable to update period start date")
        builder.setMessage(errMsg)
        builder.setPositiveButton(
            "OK") { dialog, id ->
        }
        builder.show()
    }

    //Show toast on click
    private fun showToast() {
        val text = "Period start date has been updated."
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

}