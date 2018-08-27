package com.example.melissayoung.allcalendaradds

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.melissayoung.allcalendaradds.CalendarManagers.addCalendarEvent
import com.example.melissayoung.allcalendaradds.CalendarManagers.addLocalCalendar
import com.example.melissayoung.allcalendaradds.CalendarManagers.addToLocalCalendar as addEventLocal
import com.example.melissayoung.allcalendaradds.CalendarManagers.checkForLocalCalendar
import com.example.melissayoung.allcalendaradds.CalendarManagers.deleteLocalCalendar
import com.example.melissayoung.allcalendaradds.CalendarManagers.getAllAccounts
import com.example.melissayoung.allcalendaradds.CalendarManagers.getOwnedAccounts
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            accessCalendar()
        }

        calendar_intent_button.setOnClickListener { addEventViaIntents(this) }

        calendar_sync_button.setOnClickListener {
            addCalendarEvent(contentResolver)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        showToast(true)
                    }, {
                        showToast(false)
                    })
        }

        // Setting up buttons for the sync adapter
        get_accounts_button.setOnClickListener {
            val accountList = getAllAccounts(contentResolver)
            account_list.layoutManager = LinearLayoutManager(this)
            account_list.adapter = Adapter(accountList, baseContext,
                    { account: CalendarAccount -> accountChosen(account) })

        }

        get_main_accounts_button.setOnClickListener {
            val accountList = getOwnedAccounts(contentResolver)
            account_list.layoutManager = LinearLayoutManager(this)
            account_list.adapter = Adapter(accountList, baseContext,
                    { account: CalendarAccount -> accountChosen(account) })
        }

        add_to_local_calendar.setOnClickListener { addToLocalCalendar() }
        delete_local_calendar.setOnClickListener {
            userEmail = user_email_text_field.text.toString()
            if (userEmail.isNotEmpty() && checkForLocalCalendar(contentResolver, userEmail)) {
                deleteLocalCalendar(contentResolver, userEmail)
            }
        }

        create_synced_calendar.setOnClickListener {
            userEmail = user_email_text_field_sync.text.toString()
            //createSyncedCalendar(contentResolver, userEmail)
        }

    }

    private fun addToLocalCalendar(){
        userEmail = user_email_text_field.text.toString()

        if (userEmail.isNotEmpty()) {
            when (checkForLocalCalendar(contentResolver, userEmail)) {
                false -> addLocalCalendar(contentResolver, userEmail)
                true -> addEventLocal(contentResolver, userEmail)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            showToast(true)
                        }, {
                            showToast(false)
                        })
            }
        }
    }

    private fun accountChosen(calendarAccount: CalendarAccount) {
        addCalendarEvent(calendarAccount.calendarId, contentResolver)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showToast(true)
                }, {
                    showToast(false)
                })
    }

    private fun showToast(didSucceed: Boolean) {
        var message: String = when(didSucceed) {
            true -> "Added event to calendar"
            false -> "Failed to add event"
        }

        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }

    // Our calendar permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 8008 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    private fun accessCalendar() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR),
                8008)
    }

    /**
     * Using Calendar Intents - The recommended way, no permissions needed
     * User is able to specify what address they want to use to add calendar events
     * So we don't have to query for an account
     */
    private fun addEventViaIntents(activity: Activity) {

        val beginTime = Calendar.getInstance()
        beginTime.set(2018, 9, 23, 12, 30)

        val endTime = Calendar.getInstance()
        endTime.set(2018, 9, 23, 13, 30)

        val calendarIntent: Intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, beginTime.timeInMillis)
                .putExtra(CalendarContract.Events.TITLE, "Android Demo")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Using Kotlin and Calendars")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)

        activity.startActivity(calendarIntent)
    }

}
