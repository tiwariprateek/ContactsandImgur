package com.example.contactsandimgur

import android.app.Activity
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun getContacts(context: Context): ArrayList<String>{
    val contactList=ArrayList<String>()
    val cursor=context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,null,null,null)
    while (cursor?.moveToNext()!!){
        val name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
        val mobile=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
        contactList.add("Contact name is : $name \n Contact phone number $mobile \n")
    }
    return contactList
}
fun storeFile(context: Context,contactList:ArrayList<String>):Boolean{
    val filename = "Contacts.csv"
    val fileContents = contactList.toString()
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        return try {
            it.write(fileContents.toByteArray())
            Log.d("MainActivity","File stored in ${context.filesDir.absolutePath} ")
            true
        }
        catch (t:Throwable){
            Log.d("MainActivity","Failure in storing file !! ")
            false
        }
    }
    }
fun zipFileAtPath(context: Context,status:Boolean) {
    val sourcePath="${context.filesDir.path}/Contacts.csv"
    val toLocation="${context.filesDir.path}/Contacts.zip"
    val BUFFER = 2048
    val sourceFile = File(sourcePath)
    if (status) {
        try {
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
            entry.time = sourceFile.lastModified() // to keep modification time after unzipping
            out.putNextEntry(entry)
            var count: Int
            while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                out.write(data, 0, count)
            }
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    else
        Log.d("MainActivity","Contacts.csv doesn't exist")
}