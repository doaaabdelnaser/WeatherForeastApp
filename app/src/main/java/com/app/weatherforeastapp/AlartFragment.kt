package com.app.weatherforeastapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherforeastapp.databinding.FragmentAlartBinding
import com.example.weatherapp.data.local.entities.AlertsEntity

class AlartFragment : Fragment() {
    private lateinit var alertsViewModel: AlertsViewModel
    private lateinit var binding:FragmentAlartBinding
    lateinit var alertsEntity: AlertsEntity
    lateinit var alertsAdapter: AlertsAdapter
    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         ContextUtils.setLocale(requireActivity(), Setting.lang)
     }*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        alertsViewModel =
            ViewModelProvider(this).get(AlertsViewModel::class.java)
        binding= FragmentAlartBinding.inflate(inflater,container,false)
        alertsViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            AlertsViewModel::class.java)
        alertsAdapter= AlertsAdapter(arrayListOf(),alertsViewModel,requireActivity().applicationContext)
        getAlarmData(alertsViewModel)


        initUI()

        binding.btnFab.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_nav_alart_to_nav_add_alart)
        }
        return binding.root
    }

    private fun initUI() {
        binding.rvAlerts.apply {
            adapter=alertsAdapter
        }
    }

    private fun getAlarmData(alertsViewModel: AlertsViewModel) {
        alertsViewModel.getAlertsList().observe(viewLifecycleOwner){
            alertsAdapter.changeData(it)
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                alertsAdapter.removeFromAdapter(viewHolder)
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvAlerts)
    }
}