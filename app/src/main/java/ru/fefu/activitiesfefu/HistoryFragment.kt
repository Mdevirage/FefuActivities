package ru.fefu.activitiesfefu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ru.fefu.activitiesfefu.data.ActivityType
import java.util.Date
import kotlinx.coroutines.launch
import ru.fefu.activitiesfefu.data.ActivityDao
import ru.fefu.activitiesfefu.data.ActivityEntity
import ru.fefu.activitiesfefu.data.ActivityUserDao

class HistoryFragment : BaseActivityListFragment() {

    private lateinit var userDao: ActivityUserDao
    private lateinit var activityDao: ActivityDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = (requireActivity().application as ActivityApplication).database
        userDao = db.userDao()
        activityDao = db.activityDao()
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            val db = (requireActivity().application as ActivityApplication).database
            val userDao = db.userDao()
            val activityDao = db.activityDao()

            val currentUserId = requireContext()
                .getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                .getInt("user_id", -1)
            if (currentUserId == -1) return@launch

            val users = userDao.getAllUsers().filter { it.id != currentUserId }
            val allActivities = mutableListOf<Triple<String, ActivityEntity, Int>>() // (userName, activity, userId)
            for (user in users) {
                val activities = activityDao.getActivitiesForUser(user.id)
                for (activity in activities) {
                    allActivities.add(Triple(user.name, activity, user.id))
                }
            }

            val dateFormat = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
            val activitiesByDate = allActivities.groupBy { dateFormat.format(it.second.startDate) }

            val items = mutableListOf<ActivityListItem>()
            for ((date, activities) in activitiesByDate.toSortedMap(compareByDescending { dateFormat.parse(it) })) {
                items.add(ActivityListItem.Section(date))
                val activitiesByUser = activities.groupBy { it.third to it.first } // (userId, userName)
                for ((userPair, userActivities) in activitiesByUser) {
                    items.add(ActivityListItem.Section(userPair.second)) // userName
                    for ((_, activity, _) in userActivities.sortedByDescending { it.second.startDate }) {
                        items.add(
                            ActivityListItem.Activity(
                                id = activity.id,
                                type = activity.type,
                                startDate = activity.startDate,
                                endDate = activity.endDate,
                                distance = String.format("%.2f км", activity.distance),
                                duration = formatDuration(activity.durationMillis)
                            )
                        )
                    }
                }
            }
            updateActivityList(items)
        }
    }

    private fun formatDuration(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
        return when {
            hours > 0 -> String.format("%d ч %d мин", hours, minutes)
            else -> String.format("%d мин", minutes)
        }
    }

    override fun updateActivityList(items: List<ActivityListItem>) {
        // Это может быть изменено позже для отображения данных пользователей
        activityListAdapter?.submitList(items)
    }
}