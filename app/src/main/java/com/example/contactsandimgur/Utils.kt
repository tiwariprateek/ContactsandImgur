package com.example.contactsandimgur

import android.content.Context
import android.provider.ContactsContract
import android.util.Log


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
