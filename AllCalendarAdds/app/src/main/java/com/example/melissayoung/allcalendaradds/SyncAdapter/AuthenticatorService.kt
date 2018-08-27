    package com.example.melissayoung.allcalendaradds.SyncAdapter

    import android.app.Service
    import android.content.Intent
    import android.os.IBinder


    class AuthenticatorService : Service() {

        // Instance field that stores the authenticator object
        private lateinit var stubAuthenticator : StubAuthenticator

        override fun onCreate() {
            stubAuthenticator = StubAuthenticator(baseContext)
        }

        override fun onBind(intent: Intent?): IBinder = stubAuthenticator.iBinder

    }