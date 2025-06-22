package ru.fefu.activitiesfefu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import kotlin.math.abs
import ru.fefu.activitiesfefu.databinding.FragmentActivityDetailsBinding

class ActivityDetailsFragment : Fragment() {
    private var _binding: FragmentActivityDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivityDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityId = arguments?.getInt("activityId") ?: -1
        android.util.Log.d("ActivityDetails", "activityId = $activityId")
        if (activityId == -1) return

        val db = (requireActivity().application as ActivityApplication).database
        viewLifecycleOwner.lifecycleScope.launch {
            val activity = db.activityDao().getActivityById(activityId)
            activity?.let {
                binding.detailsDistance.text = String.format("%.2f км", it.distance)
                binding.detailsDuration.text = formatDuration(it.durationMillis)
                binding.toolbar.title = it.type.displayName
                // Получаем пользователя по userId активности
                val user = db.userDao().getUserById(activity.userId)
                if (user != null) {
                    binding.userText.text = "@${user.name}"
                    binding.userText.visibility = View.VISIBLE
                } else {
                    binding.userText.visibility = View.GONE
                }
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.detailsStartTime.text = "Старт: ${timeFormat.format(activity.startDate)}"
                binding.detailsEndTime.text = "Финиш: ${timeFormat.format(activity.endDate)}"
                binding.detailsTimeAgo.text = getHoursAgoText(activity.startDate)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    // Добавьте вспомогательную функцию для форматирования времени:
    private fun formatDuration(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
        return when {
            hours > 0 -> String.format("%d ч %d мин", hours, minutes)
            else -> String.format("%d мин", minutes)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun getHoursAgoText(startDate: Date): String {
        val now = Date()
        val diffMillis = abs(now.time - startDate.time)
        val hours = diffMillis / (1000 * 60 * 60)
        return if (hours == 0L) {
            "меньше часа назад"
        } else {
            "$hours ${getHourWord(hours)} назад"
        }
    }
    override fun onResume() {
        super.onResume()
        Log.d("FAB_DEBUG", "onResume NewActivityFragment/ActivityDetailsFragment")
        (activity as? MainActivity)?.binding?.fabNewActivity?.hide()
    }

    fun getHourWord(hours: Long): String {
        return when {
            hours % 10 == 1L && hours % 100 != 11L -> "час"
            hours % 10 in 2..4 && (hours % 100 !in 12..14) -> "часа"
            else -> "часов"
        }
    }
} 