    package com.example.melissayoung.allcalendaradds.CalendarManagers

    import android.annotation.SuppressLint
    import android.content.ContentResolver
    import android.content.ContentValues
    import android.database.Cursor
    import android.net.Uri
    import android.provider.CalendarContract
    import com.example.melissayoung.allcalendaradds.CalendarAccount
    import io.reactivex.Single
    import java.util.*

    /**
     * The select an account approach
     * First we're going to query for accounts
     */
    private fun queryCalendarAccounts(contentResolver: ContentResolver): Cursor {

        val calendarProjection: Array<String> = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.ACCOUNT_TYPE
        )

        val cursor: Cursor
        val uri: Uri = CalendarContract.Calendars.CONTENT_URI

        @SuppressWarnings("MissingPermissions")
        cursor = contentResolver.query(uri, calendarProjection, null, null, null )

        return cursor
    }

    //Gets a random account using the query, so it could be anything on the phone!
    @SuppressLint("MissingPermission")
    fun addCalendarEvent(contentResolver: ContentResolver): Single<Uri> {

        val beginTime = Calendar.getInstance()
        beginTime.set(2018, 7, 25, 12, 30)

        val endTime = Calendar.getInstance()
        endTime.set(2018, 7, 25, 13, 30)

        val cursor = queryCalendarAccounts(contentResolver)
        var randomAccount = ""

        while (cursor.moveToNext()) {
            randomAccount = cursor.getString(0)
        }

        return Single.create { subscriber ->
            try {
                val contentValues = ContentValues().apply {
                    put(CalendarContract.Events.CALENDAR_ID, randomAccount)
                    put(CalendarContract.Events.TITLE, "Android Demo - Random Sync")
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getAvailableIDs().toString())
                    put(CalendarContract.Events.DTSTART, beginTime.timeInMillis)
                    put(CalendarContract.Events.DTEND, endTime.timeInMillis)
                }

                val uri: Uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)
                subscriber.onSuccess(uri)
            } catch (e: Exception) {
                android.util.Log.e("No gucci", "Didn't add event $e")
                subscriber.onError(e)
            }
        }
    }

    // Returns a list of all accounts from the query
    fun getAllAccounts(contentResolver: ContentResolver): ArrayList<CalendarAccount> {

        val cursor = queryCalendarAccounts(contentResolver)

        val accountList: ArrayList<CalendarAccount> = ArrayList()

        while (cursor.moveToNext()) {

            val nextAccount = CalendarAccount(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3))
            accountList.add(nextAccount)
        }

        return accountList
    }

    // Returns a list of accounts that are owned by the user
    fun getOwnedAccounts(contentResolver: ContentResolver): ArrayList<CalendarAccount> {

        val cursor = queryCalendarAccounts(contentResolver)

        val accountList: ArrayList<CalendarAccount> = ArrayList()

        while (cursor.moveToNext()) {

            val nextAccount = CalendarAccount(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3))

            if (nextAccount.accountName == nextAccount.ownerName) {
                accountList.add(nextAccount)
            }
        }

        return accountList

    }

    // Adds an event to the calendar selected by the user
    @SuppressLint("MissingPermission")
    fun addCalendarEvent(calendarId: String, contentResolver: ContentResolver): Single<Uri> {

        val beginTime = Calendar.getInstance()
        beginTime.set(2018, 9, 24, 12, 30)

        val endTime = Calendar.getInstance()
        endTime.set(2018, 9, 24, 13, 30)

        return Single.create { subscriber ->
            try {
                val contentValues = ContentValues().apply {
                    put(CalendarContract.Events.CALENDAR_ID, calendarId)
                    put(CalendarContract.Events.TITLE, "Android Demo - Selected Sync")
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getAvailableIDs().toString())
                    put(CalendarContract.Events.DTSTART, beginTime.timeInMillis)
                    put(CalendarContract.Events.DTEND, endTime.timeInMillis)
                }

                val uri: Uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)
                subscriber.onSuccess(uri)
            } catch (e: Exception) {
                android.util.Log.e("No gucci", "Didn't add event $e")
                subscriber.onError(e)
            }
        }
    }