package com.appify.scaneye.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appify.scaneye.AdapterScanItems
import com.appify.scaneye.MainViewModel
import com.appify.scaneye.R
import com.appify.scaneye.dataModels.DateTimeHelper
import com.appify.scaneye.dataModels.ScanHistoryItem

class HistoryFragment : Fragment() {
    private val myMainViewModel: MainViewModel by activityViewModels()
    private lateinit var layoutTodayHistory: LinearLayout
    private lateinit var layoutYesterdayHistory: LinearLayout
    private lateinit var layoutOlderHistory: LinearLayout
    private lateinit var layoutEmptyHistory: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpStatusBar(); return inflater.inflate(R.layout.fragment_history, container, false)
    }

    private fun setUpStatusBar() {
        val currentWindow =
            activity?.window; currentWindow?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        currentWindow?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        currentWindow?.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)

        if (currentWindow != null) {
            WindowCompat.setDecorFitsSystemWindows(currentWindow, false)
            WindowCompat.getInsetsController(currentWindow, currentWindow.decorView).apply {
                isAppearanceLightStatusBars = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutTodayHistory = view.findViewById(R.id.layout_todayHistory)
        layoutYesterdayHistory = view.findViewById(R.id.layout_yesterdayHistory)
        layoutOlderHistory = view.findViewById(R.id.layout_olderHistory)
        layoutEmptyHistory = view.findViewById(R.id.tv_emptyHistory)

        val todayScansAdapter = AdapterScanItems()
        val yesterdayScansAdapter = AdapterScanItems()
        val olderScansAdapter = AdapterScanItems()

        view.findViewById<ImageButton>(R.id.button_backButton).setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<RecyclerView>(R.id.rv_todayHistory).apply {
            adapter = todayScansAdapter; layoutManager = LinearLayoutManager(context)
        }

        view.findViewById<RecyclerView>(R.id.rv_yestHistory).apply {
            adapter = yesterdayScansAdapter; layoutManager = LinearLayoutManager(context)
        }

        view.findViewById<RecyclerView>(R.id.rv_olderHistory).apply {
            adapter = olderScansAdapter; layoutManager = LinearLayoutManager(context)
        }

        myMainViewModel.getScanHistory()?.observe(this.viewLifecycleOwner) {
            val todayHistoryList = mutableListOf<ScanHistoryItem>()
            val yesterdayHistoryList = mutableListOf<ScanHistoryItem>()
            val olderHistoryList = mutableListOf<ScanHistoryItem>()

            val todayDate = DateTimeHelper.getCurrentDate()
            for (eachScan in it) {
                when (DateTimeHelper.compareMyDates(eachScan.historyItemTime, todayDate)) {
                    0 -> {
                        todayHistoryList.add(eachScan); eachScan.scanDateKey = 0
                    }
                    -1 -> {
                        yesterdayHistoryList.add(eachScan); eachScan.scanDateKey = -1
                    }
                    -2 -> {
                        olderHistoryList.add(eachScan); eachScan.scanDateKey = -2
                    }
                }
            }

            var visibilityPattern: String = if (olderHistoryList.isEmpty()) "0"; else "1"
            visibilityPattern =
                "${if (yesterdayHistoryList.isEmpty()) "0"; else "1"}$visibilityPattern"
            visibilityPattern = "${if (todayHistoryList.isEmpty()) "0"; else "1"}$visibilityPattern"
            switchLayoutsVisibility(visibilityPattern)

            todayScansAdapter.submitListToDiffer(todayHistoryList.reversed())
            yesterdayScansAdapter.submitListToDiffer(yesterdayHistoryList.reversed())
            olderScansAdapter.submitListToDiffer(olderHistoryList.reversed())
        }
    }

    private fun switchLayoutsVisibility(visibilityPattern: String) {
        when (visibilityPattern) {
            "000" -> {
                layoutTodayHistory.visibility = View.GONE
                layoutYesterdayHistory.visibility = View.GONE
                layoutOlderHistory.visibility = View.GONE
                layoutEmptyHistory.visibility = View.VISIBLE
            }
            "001" -> {
                layoutTodayHistory.visibility = View.GONE
                layoutYesterdayHistory.visibility = View.GONE
                layoutEmptyHistory.visibility = View.GONE

                val newLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams.addRule(RelativeLayout.BELOW, R.id.button_backButton)
                layoutOlderHistory.layoutParams = newLayoutParams
                layoutOlderHistory.visibility = View.VISIBLE
            }
            "010" -> {
                layoutTodayHistory.visibility = View.GONE
                layoutOlderHistory.visibility = View.GONE
                layoutEmptyHistory.visibility = View.GONE

                val newLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams.addRule(RelativeLayout.BELOW, R.id.button_backButton)
                layoutTodayHistory.layoutParams = newLayoutParams
                layoutYesterdayHistory.visibility = View.VISIBLE
            }
            "011" -> {
                layoutTodayHistory.visibility = View.GONE
                layoutEmptyHistory.visibility = View.GONE

                val newLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams.addRule(RelativeLayout.BELOW, R.id.button_backButton)
                layoutYesterdayHistory.layoutParams = newLayoutParams
                layoutYesterdayHistory.visibility = View.VISIBLE

                val newLayoutParams2 = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams2.addRule(RelativeLayout.BELOW, R.id.layout_yesterdayHistory)
                layoutOlderHistory.layoutParams = newLayoutParams2
                layoutOlderHistory.visibility = View.VISIBLE
            }
            "100" -> {
                layoutYesterdayHistory.visibility = View.GONE
                layoutOlderHistory.visibility = View.GONE
                layoutEmptyHistory.visibility = View.GONE

                val newLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams.addRule(RelativeLayout.BELOW, R.id.button_backButton)
                layoutTodayHistory.layoutParams = newLayoutParams
                layoutTodayHistory.visibility = View.VISIBLE
            }
            "101" -> {
                layoutYesterdayHistory.visibility = View.GONE
                layoutEmptyHistory.visibility = View.GONE

                val newLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams.addRule(RelativeLayout.BELOW, R.id.button_backButton)
                layoutTodayHistory.layoutParams = newLayoutParams
                layoutTodayHistory.visibility = View.VISIBLE

                val newLayoutParams2 = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams2.addRule(RelativeLayout.BELOW, R.id.layout_todayHistory)
                layoutOlderHistory.layoutParams = newLayoutParams2
                layoutOlderHistory.visibility = View.VISIBLE
            }
            "110" -> {
                layoutOlderHistory.visibility = View.GONE
                layoutEmptyHistory.visibility = View.GONE

                val newLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams.addRule(RelativeLayout.BELOW, R.id.button_backButton)
                layoutTodayHistory.layoutParams = newLayoutParams
                layoutTodayHistory.visibility = View.VISIBLE

                val newLayoutParams2 = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams2.addRule(RelativeLayout.BELOW, R.id.layout_todayHistory)
                layoutYesterdayHistory.layoutParams = newLayoutParams2
                layoutYesterdayHistory.visibility = View.VISIBLE
            }
            "111" -> {
                layoutEmptyHistory.visibility = View.GONE

                val newLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams.addRule(RelativeLayout.BELOW, R.id.button_backButton)
                layoutTodayHistory.layoutParams = newLayoutParams
                layoutTodayHistory.visibility = View.VISIBLE

                val newLayoutParams2 = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams2.addRule(RelativeLayout.BELOW, R.id.layout_todayHistory)
                layoutYesterdayHistory.layoutParams = newLayoutParams2
                layoutYesterdayHistory.visibility = View.VISIBLE

                val newLayoutParams3 = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newLayoutParams3.addRule(RelativeLayout.BELOW, R.id.layout_yesterdayHistory)
                layoutOlderHistory.layoutParams = newLayoutParams3
                layoutOlderHistory.visibility = View.VISIBLE
            }
        }
    }
}