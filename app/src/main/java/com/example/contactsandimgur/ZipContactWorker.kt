package com.example.contactsandimgur

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipContactWorker(ctx:Context,params:WorkerParameters):Worker(ctx,params) {
    override fun doWork(): Result {
        return try {
            val context = applicationContext
            val sourcePath = "${context.filesDir.path}/Contacts.csv"
            val toLocation = "${context.filesDir.path}/Contacts.zip"
            val BUFFER = 2048
            val sourceFile = File(sourcePath)
            var origin: BufferedInputStream? = null
            val dest = FileOutputStream(toLocation)
            val out = ZipOutputStream(
                BufferedOutputStream(
                    dest
                )
            )
            val data = ByteArray(BUFFER)
            val fi = FileInputStream(sourcePath)
            origin = BufferedInputStream(fi, BUFFER)
            val entry =
                ZipEntry(sourcePath)
            entry.time = sourceFile.lastModified()
            out.putNextEntry(entry)
            var count: Int
            while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                out.write(data, 0, count)
            }
            out.close()
            Log.d("Archived","Archiving contacts Failed")
            Result.success()
        } catch (t: Throwable) {
            Log.d("Archived","Archiving contacts Failed $t")
            Result.failure()
        }
    }
}