package com.example.contactsandimgur.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.contactsandimgur.R
import com.example.contactsandimgur.StoreContactWorker
import com.example.contactsandimgur.ZipContactWorker
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_contact.*

/**
 * A simple [Fragment] subclass.
 */
class ContactFragment : Fragment() {
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1
    private var contactList = ArrayList<String>()
    lateinit var workManager : WorkManager
    val workInfo: LiveData<List<WorkInfo>>
        get() = workManager.getWorkInfosByTagLiveData("ZIP_STATUS")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        workManager=WorkManager.getInstance(requireContext())
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                MY_PERMISSIONS_REQUEST_READ_CONTACTS
            )

        } else {
            Log.d("MainActivity", "Permission granted")
        }
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contact_button.setOnClickListener {
            contact_button.visibility = View.GONE
            var continuation =
                workManager.beginWith(OneTimeWorkRequest.from(StoreContactWorker::class.java))
            val zipContactWorker = OneTimeWorkRequest.Builder(ZipContactWorker::class.java)
                .addTag("ZIP_STATUS")
                .build()
            continuation = continuation.then(zipContactWorker)
            continuation.enqueue()
            workInfo.observe(viewLifecycleOwner, Observer { work ->
                if (work.isNullOrEmpty())
                    Log.d("MainActivity", "No matching work found")
                val workinfo = work[0]
                if (workinfo.state.isFinished) {
                    Snackbar.make(viewsnackbar, "Operation Success", Snackbar.LENGTH_LONG).show()
                } else
                    Snackbar.make(viewsnackbar, "Operation in progress", Snackbar.LENGTH_LONG).show()
            })
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("MainActivity","Permission granted")
                } else {
                    Log.d("MainActivity","Permission Denied")
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
