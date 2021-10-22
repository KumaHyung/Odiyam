package com.soapclient.place.utilities

import androidx.databinding.BindingConversion
import java.text.SimpleDateFormat
import java.util.*

@BindingConversion
fun convertDateToDisplayedText(date: Date): String = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(date)