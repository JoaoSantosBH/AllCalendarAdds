    package com.example.melissayoung.allcalendaradds.CalendarManagers

    import android.annotation.SuppressLint
    import android.content.ContentResolver
    import android.content.ContentValues
    import android.database.Cursor
    import android.net.Uri
    import android.provider.CalendarContract
    import android.provider.CalendarContract.Events
    import android.provider.CalendarContract.Calendars
    import io.reactivex.Single
    import io.reactivex.android.schedulers.AndroidSchedulers
    import io.reactivex.schedulers.Schedulers
    import java.util.*

    /**
     * Local Calendar approach - Don't have to worry about where it saves
     * But also doesn't sync anywhere else besides your phone
     * Also you can only delete it from this app
     */
    @SuppressLint("MissingPermission")
    fun addLocalCalendar(contentResolver: ContentResolver, userEmail: String) {

        // Create calendar
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
        uri = uri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, userEmail)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL).build()

        contentResolver.insert(uri, contentValues)

        addToLocalCalendar(contentResolver, userEmail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

    }

    // Checks if you already have our calendar yet
    @SuppressLint("MissingPermission")
    fun checkForLocalCalendar(contentResolver: ContentResolver, userEmail: String): Boolean {

        val calendarQueryColumns = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        val calendarAccountData: Cursor

        val uri: Uri = CalendarContract.Calendars.CONTENT_URI

        val selection: String = ("((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND (" +
                CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND (" +
                CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))")

        val selectionArgs: Array<String> = arrayOf(userEmail, CalendarContract.ACCOUNT_TYPE_LOCAL, userEmail)

        calendarAccountData = contentResolver.query(uri, calendarQueryColumns, selection, selectionArgs, null)

        while (calendarAccountData.moveToNext()) {
            val displayName: String = calendarAccountData.getString(calendarAccountData.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
            val accountName: String = calendarAccountData.getString(calendarAccountData.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME))

            if (displayName == "Android Demo Events" && accountName == userEmail) {
                return true
            }
        }
        return false
    }

    // Adds event to our local calendar
    @SuppressLint("MissingPermission")
    fun addToLocalCalendar(contentResolver: ContentResolver, userEmail: String): Single<Uri> {

        val beginTime = Calendar.getInstance()
        beginTime.set(2018, 7, 24, 12, 30)

        val endTime = Calendar.getInstance()
        endTime.set(2018, 7, 24, 13, 30)

        val calendarProjection: Array<String> = arrayOf(
                Calendars._ID,
                Calendars.ACCOUNT_NAME,
                Calendars.OWNER_ACCOUNT
        )

        var calID: Long = 0

        return Single.create { subscriber ->

            // Get our calendar
            contentResolver.query(
                    CalendarContract.Calendars.CONTENT_URI,
                    calendarProjection,
                    null,
                    null,
                    null
            ).use {
                while (it.moveToNext()) {
                    val accountName: String = it.getString(1)
                    val displayName: String = it.getString(2)

                    if (displayName == "Android Demo Events" && accountName == userEmail) {
                        calID = it.getLong(0)
                    }
                }
            }

            // put stuff in the calendar
            try {

                val contentValues = ContentValues().apply {
                    put(Events.CALENDAR_ID, calID)
                    put(Events.TITLE, "Android Demo - Local Calendar")
                    put(Events.DESCRIPTION, "This using a local calendar")
                    put(Events.EVENT_TIMEZONE, TimeZone.getAvailableIDs().toString())
                    put(Events.DTSTART, beginTime.timeInMillis)
                    put(Events.DTEND, endTime.timeInMillis)
                }

                val uri: Uri = contentResolver.insert(Events.CONTENT_URI, contentValues)
                subscriber.onSuccess(uri)
            } catch (e: Exception) {
                subscriber.onError(e)
            }
        }
    }

    // Deletes the local calendar if it exists for that user
    @SuppressLint("MissingPermission")
    fun deleteLocalCalendar(contentResolver: ContentResolver, userEmail: String) {

        var uri: Uri = Calendars.CONTENT_URI

        uri = uri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, userEmail)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL).build()

        contentResolver.delete(uri, null, null)
    }

