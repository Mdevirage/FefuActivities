package ru.fefu.activitiesfefu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.fefu.activitiesfefu.databinding.FragmentHistoryBinding

abstract class BaseActivityListFragment : Fragment() {
    protected var _binding: FragmentHistoryBinding? = null
    protected val binding get() = _binding!!

    protected var activityListAdapter: ActivityListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    protected fun setupRecyclerView() {
        activityListAdapter = ActivityListAdapter() { activity ->
            navigateToActivityDetails(activity)
        }
        binding.activityRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.activityRecyclerView.adapter = activityListAdapter
    }

    protected fun navigateToActivityDetails(activity: ActivityListItem.Activity) {
        val fragment = ActivityDetailsFragment()
        fragment.arguments = Bundle().apply {
            putInt("activityId", activity.id)
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    abstract fun updateActivityList(items: List<ActivityListItem>)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 