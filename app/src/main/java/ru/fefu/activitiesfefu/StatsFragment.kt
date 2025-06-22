package ru.fefu.activitiesfefu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.fefu.activitiesfefu.data.ActivityDao
import ru.fefu.activitiesfefu.data.ActivityType

class StatsFragment : BaseActivityListFragment() {

    private lateinit var activityDao: ActivityDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDao = (requireActivity().application as ActivityApplication).database.activityDao()
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = requireContext()
                .getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                .getInt("user_id", -1)
            if (userId == -1) return@launch

            val activities = activityDao.getActivitiesForUser(userId)
                val activityListItems = activities.map { activityEntity ->
                    ActivityListItem.Activity(
                        id = activityEntity.id,
                        type = activityEntity.type,
                        startDate = activityEntity.startDate,
                        endDate = activityEntity.endDate,
                    distance = String.format("%.2f км", activityEntity.distance),
                    duration = formatDuration(activityEntity.durationMillis)
                    )
                }
                updateActivityList(activityListItems)
            }
    }

    override fun updateActivityList(newActivities: List<ActivityListItem>) {
        val items = mutableListOf<ActivityListItem>()
        items.add(ActivityListItem.Section("Все активности"))
        items.addAll(newActivities.filterIsInstance<ActivityListItem.Activity>().sortedByDescending { it.startDate })

        activityListAdapter?.submitList(items)
    }
    private fun formatDuration(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
        return when {
            hours > 0 -> String.format("%d ч %d мин", hours, minutes)
            else -> String.format("%d мин", minutes)
        }
    }
}