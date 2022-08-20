package com.example.nycschools

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.nycschools.databinding.ActivityMainBinding
import com.example.nycschools.di.NYCApplication
import com.example.nycschools.model.UIState
import com.example.nycschools.model.local.SchoolSatEntity
import com.example.nycschools.view.SchoolListDisplay
import com.example.nycschools.viewmodel.NYCViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

private const val TAG = "MainActivity"

class MainActivity: AppCompatActivity() {

    @Inject lateinit var viewModel: NYCViewModel
    private lateinit var blinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        blinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(blinding.root)
        NYCApplication.component.inject( this )

        viewModel.schoolSat.observe(this) {
            // todo create the fragment SchoolListDisplay (layout, recyclerview, adapter/viewholder)
            // todo in the fragment instance pass the Parcelable.
            // todo supportFragmentManager.beginTransaction()
            // todo modify the ActivityMainBinding define the container.

            when(it){
                is UIState.Response -> initFragment(it.success)
                is UIState.Error -> showError(it.errorMessage)
                is UIState.Loading -> showLoading(it.isLoading)
                is UIState.Empty -> refreshData()
            }
        }
    }

    private fun refreshData() {
        TODO()
    }

    private fun showLoading(loading: Boolean) {
        blinding.progressBar.visibility = if (loading) {
             View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun showError(errorMessage: String) {
        Snackbar.make(
            blinding.content,
            errorMessage,
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Dismiss"){
            Toast.makeText(this@MainActivity,
                "Dismissing Toast",
                Toast.LENGTH_SHORT.show(),
        }.show()
    }

    private fun initFragment(data: List<SchoolSatEntity>) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SchoolListDisplay.newInstance(data))
            .commit()
    }
}