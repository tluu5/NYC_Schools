package com.example.nycschools.view

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nycschools.R
import com.example.nycschools.databinding.SchoolListFragmentBinding
import com.example.nycschools.model.local.SchoolSatEntity
import com.example.nycschools.view.adapter.NYCAdapter

class SchoolListDisplay: Fragment() {

    companion object{
        const val KEY_LIST = "List_Sat_Entity"
        fun newInstance(data: List<SchoolSatEntity>) =
            SchoolListDisplay().apply {
                arguments = Bundle().apply {
                    val result = ArrayList<SchoolSatEntity>()
                    data.forEach { result.add(it) }
                    putParcelableArrayList(KEY_LIST, result)
                }
            }
    }

    private lateinit var binding: SchoolListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = SchoolListFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        arguments?.getParcelableArrayList<SchoolSatEntity>(KEY_LIST)?.let {
            initViews(it as List<SchoolSatEntity>)
        }

        return binding.root
    }

    private fun initViews(dataSet: List<SchoolSatEntity>) {
        binding.schoolList.layoutManager = GridLayoutManager(context, 2)
        binding.schoolList.adapter = NYCAdapter(dataSet,
            ::navigateDetails)
    }

    private fun navigateDetails(schoolSatEntity: SchoolSatEntity) {
        requireActivity().navigateDetails( schoolSatEntity)
    }

    private fun FragmentActivity.navigateDetails(schoolSatEntity: SchoolSatEntity){
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, SchoolDetails.newInance(schoolSatEntity))
//            .addToBackStack(null)
//            .commit()
    }
}

