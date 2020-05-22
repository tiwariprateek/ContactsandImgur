package com.example.contactsandimgur

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.material.snackbar.Snackbar

class StoreContactWorker(context: Context, params:WorkerParameters):Worker(context,params) {
    override fun doWork(): Result {
        return try {
            val context=applicationContext
            val contacts = getContacts(context)
            storeFile(context,contacts)
            Log.d("CompressWork","Getting contact successful ${contacts}")
            Result.success()

        }
        catch (t:Throwable){
            Log.d("CompressWork","Getting contact failed $t")
            Result.failure()
        }

    }
}