package com.example.mapdemoapplication.utils

import java.util.*
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan


object StringUtils {

    fun getString(bundle: Bundle?, key: String): String {
        bundle?.let { tempBundle ->
            if (tempBundle.containsKey(key)) {
                val stringExtra = tempBundle.getString(key)
                stringExtra?.let { return it }
            }
        }
        return ""
    }


    fun getString(intent: Intent?, key: String): String {
        intent?.let { tempIntent ->
            if (tempIntent.hasExtra(key)) {
                val stringExtra = tempIntent.getStringExtra(key)
                stringExtra?.let { it -> return it }
            }
        }
        return ""
    }

    fun getBoolean(intent: Intent?, key: String): Boolean {
        intent?.let { tempIntent ->
            if (tempIntent.hasExtra(key)) {
                return tempIntent.getBooleanExtra(key, false)
            }
        }
        return false
    }

    fun getBoolean(bundle: Bundle?, key: String): Boolean {
        bundle?.let { tempBundle ->
            if (tempBundle.containsKey(key)) {
                return tempBundle.getBoolean(key, false)
            }
        }
        return false
    }

    fun getSourceScreen(intent: Intent?): String {
        return getString(intent, IntentKeys.SOURCE_SCREEN)
    }

    fun getSourceScreen(bundle: Bundle?): String {
        return getString(bundle, IntentKeys.SOURCE_SCREEN)
    }

    fun getString(value: String?, defaultReturn: String): String {
        value?.let {
            if (it.isNotEmpty() && !it.equals("null", true)) {
                return it
            }
        }
        return defaultReturn
    }

    fun isDigit(string: String): Boolean {
        return string.matches(Regex("\\d+(?:\\.\\d+)?"))
    }

    fun getSourceScreenBundle(sourceScreen: String): Bundle {
        return setBundleValue(IntentKeys.SOURCE_SCREEN, sourceScreen)
    }

    fun setBundleValue(key: String, value: String): Bundle {
        val bundle = Bundle()
        bundle.putString(key, value)
        return bundle
    }

    fun toTitleCase(string: String): String {
        var whiteSpace = true
        val builder = StringBuilder(string) // String builder to store string
        val builderLength = builder.length

        // Loop through builder
        for (i in 0 until builderLength) {
            val c = builder[i] // Get character at builders position
            if (whiteSpace) {
                // Check if character is not white space
                if (!Character.isWhitespace(c)) {

                    // Convert to title case and leave whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c))
                    whiteSpace = false
                }
            } else if (Character.isWhitespace(c)) {
                whiteSpace = true // Set character is white space
            } else {
                builder.setCharAt(i, Character.toLowerCase(c)) // Set character to lowercase
            }
        }
        return builder.toString() // Return builders text
    }

    fun stringFirstLetterCaps(string: String): String {
        return string.substring(0, 1).toUpperCase(Locale.getDefault()) + string.substring(1)
    }



    fun getUnderLineText(
        normalString: String,
        underLineString: String,
        underLineColor: Int
    ): SpannableString {
        val spannableString = SpannableString(normalString + underLineString)

        spannableString.setSpan(
            ForegroundColorSpan(underLineColor),
            normalString.length,
            normalString.length + underLineString.length,
            0
        )
        spannableString.setSpan(
            UnderlineSpan(),
            normalString.length,
            normalString.length + underLineString.length,
            0
        )
        return spannableString
    }

    fun getFormattedString(
        partOne: String, partTwo: String, colorOne: Int, colorTwo: Int
    ): SpannableString {
        val spannableString = SpannableString(partOne + partTwo)
        if (colorOne != 0) { //Set font color
            spannableString.setSpan(
                ForegroundColorSpan(colorOne),
                0,
                partOne.length,
                0
            ) // set color
        }
        if (colorTwo != 0) {
            spannableString.setSpan(
                ForegroundColorSpan(colorTwo),
                partOne.length,
                partOne.length + partTwo.length,
                0
            ) // set color
        }
        return spannableString
    }


    fun getCommaSeparatedString(stringList: ArrayList<String>, addSpace: Boolean): String {
        var string = ""
        val stringBuilder = StringBuilder()
        for (stringElement in stringList) {
            if (stringElement.isNotEmpty()) {
                stringBuilder.append(stringElement)
                if (addSpace) {
                    stringBuilder.append(", ")
                } else {
                    stringBuilder.append(",")

                }
            }
        }
        if (stringBuilder.isNotEmpty()) {
            string = if (addSpace) {
                stringBuilder.substring(0, stringBuilder.length - 2)
            } else {
                stringBuilder.substring(0, stringBuilder.length - 1)
            }
        }
        return string
    }

    fun getSlashSeparatedString(stringList: ArrayList<String>): String {
        var string = ""
        val stringBuilder = StringBuilder()
        for (stringElement in stringList) {
            stringBuilder.append(stringElement)
            stringBuilder.append("/")
        }
        if (stringBuilder.isNotEmpty()) {
            string = stringBuilder.substring(0, stringBuilder.length - 1)
        }
        return string
    }

    fun getSeparatedString(
        stringList: ArrayList<String>,
        separation: String,
        removeLastChars: Int
    ): String {
        var string = ""
        val stringBuilder = StringBuilder()
        for (stringElement in stringList) {
            stringBuilder.append(stringElement)
            stringBuilder.append(separation)
        }
        if (stringBuilder.isNotEmpty()) {
            string = stringBuilder.substring(0, stringBuilder.length - removeLastChars)
        }
        return string
    }



}