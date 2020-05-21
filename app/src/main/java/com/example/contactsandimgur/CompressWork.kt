package com.example.contactsandimgur

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class CompressWork(context: Context,params:WorkerParameters):Worker(context,params) {
    override fun doWork(): Result {
        return try {
            val context=applicationContext
            val contacts = getContacts(context)
            storeFile(context,contacts)
            zipFileAtPath(context)
            Log.d("CompressWork","Getting contact successful ${contacts}")
            Result.success()
        }
        catch (t:Throwable){
            Log.d("CompressWork","Getting contact failed $t")
            Result.failure()
        }

    }
}