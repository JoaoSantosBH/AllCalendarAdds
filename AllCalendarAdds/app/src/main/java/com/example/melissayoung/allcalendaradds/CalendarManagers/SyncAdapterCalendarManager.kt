package com.example.melissayoung.allcalendaradds.CalendarManagers

import android.accounts.Account
import android.annotation.SuppressLint
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.SyncResult
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars

class SyncAdapterCalendarManager(context: Context, autoInitialize: Boolean) :
        AbstractThreadedSyncAdapter(context, autoInitialize) {

    val contentResolver = context.contentResolver

    override fun onPerformSync(account: Account?, extras: Bundle?, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


// Create a Uri using a sync adapter to create a new calendar on an account
fun uriBuilder(uri: Uri, userEmail: String): Uri = uri.buildUpon()
        .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
        .appendQueryParameter(Calendars.ACCOUNT_NAME, userEmail)
        .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.google").build()

@SuppressLint("MissingPermission")
fun createSyncedCalendar(contentResolver: ContentResolver, userEmail: String) {

    val contentValues = ContentValues().apply {
        put(Calendars.ACCOUNT_NAME, userEmail)
        put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        put(Calendars.NAME, "Android Demo Events")
        put(Calendars.CALENDAR_DISPLAY_NAME, "Android Demo Events")
        put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER)
        put(Calendars.OWNER_ACCOUNT, userEmail)
        put(Calendars.CALENDAR_COLOR, "FFF15D23")
    }

    var uri: Uri = Calendars.CONTENT_URI

    uri = uriBuilder(uri, userEmail)

    contentResolver.insert(uri, contentValues)
}