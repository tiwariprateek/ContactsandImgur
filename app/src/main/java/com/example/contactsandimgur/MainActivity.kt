package com.example.contactsandimgur

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MainActivity : AppCompatActivity() {
    val workManager=WorkManager.getInstance(application)
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS=1
    private var contactList=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS)

            }
        else {
            Log.d("MainActivity","Permission granted")
        }
        button.setOnClickListener {
            button.visibility=View.GONE
            var continuation=workManager.beginWith(OneTimeWorkRequest.from(StoreContactWorker::class.java))
            val zipContactWorker=OneTimeWorkRequest.Builder(ZipContactWorker::class.java).build()
            continuation=continuation.then(zipContactWorker)
            continuation.enqueue()
            Snackbar.make(it,"Operation Success", Snackbar.LENGTH_LONG).show()

//            zipFileAtPath()
//            getContacts(textView)
//            storeFile()

        }

    }
    fun getContacts(textView: TextView){
        val cursor=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,null,null,null)
        while (cursor?.moveToNext()!!){
            val name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val mobile=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            contactList.add("Contact name is : $name \n Contact phone number $mobile \n")
            textView.text=contactList.toString()

        }
    }
    fun storeFile(){
        val filename = "Contacts.csv"
        val archivedFile="Archived"
        val fileContents = contactList.toString()
        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
            ZipOutputStream(it)
        }
        Log.d("MainActivity","File stored in ${filesDir} ")
    }
    fun zipFileAtPath() {
        val sourcePath="${filesDir.path}/Contacts.csv"
        val toLocation="${filesDir.path}/Contacts.zip"
        val BUFFER = 2048
        val sourceFile = File(sourcePath)
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


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("MainActivity","Permission granted")

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.d("MainActivity","Permission Denied")
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


}
