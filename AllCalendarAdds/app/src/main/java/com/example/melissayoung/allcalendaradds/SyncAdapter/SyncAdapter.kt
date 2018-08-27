    package com.example.melissayoung.allcalendaradds.SyncAdapter

    import android.accounts.Account
    import android.content.AbstractThreadedSyncAdapter
    import android.content.ContentProviderClient
    import android.content.Context
    import android.content.SyncResult
    import android.os.Bundle

    class SyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {

        val contentResolver = context.contentResolver

        override fun onPerformSync(account: Account?, extras: Bundle?, authority: String?, provider: ContentProviderClient?, syncResult: SyncResult?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }