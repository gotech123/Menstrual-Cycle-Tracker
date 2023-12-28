package com.example.customapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
data class PeriodParcel(val date: LocalDate, val cycleDays: Int) : Parcelable {

}
