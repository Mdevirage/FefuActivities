package ru.fefu.activitiesfefu

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.fefu.activitiesfefu.databinding.FragmentNewActivityBinding
import ru.fefu.activitiesfefu.data.ActivityDao
import ru.fefu.activitiesfefu.data.ActivityEntity
import ru.fefu.activitiesfefu.data.ActivityType
import java.util.Date
import kotlin.random.Random
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewActivityFragment : Fragment() {
    private var _binding: FragmentNewActivityBinding? = null
    private val binding get() = _binding!!

    private lateinit var activityTypeAdapter: ActivityTypeAdapter
    private var selectedActivityType: ActivityType? = null
    private lateinit var activityDao: ActivityDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityDao = (requireActivity().application as ActivityApplication).database.activityDao()

        val activityTypes = ActivityType.values().map { it.displayName }
        // В NewActivityFragment, где создаётся адаптер:
        activityTypeAdapter = ActivityTypeAdapter(activityTypes) { selectedTypeString ->
            selectedActivityType = ActivityType.values().find { it.displayName == selectedTypeString }
            // Здесь нужно добавить:
            val position = activityTypes.indexOf(selectedTypeString)
            activityTypeAdapter.setSelected(position)
        }

        binding.activityTypeRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.activityTypeRecyclerView.adapter = activityTypeAdapter

        // Обработка клика по кнопке "Начать"
        binding.startButton.setOnClickListener {
            selectedActivityType?.let { type ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val random = Random(System.currentTimeMillis())
                    val startDate = Date()
                    val durationMillis = random.nextLong(600000, 3600000) // 10 mins - 1 hour
                    val endDate = Date(startDate.time + durationMillis)
                    val distance = random.nextFloat() * 10 // Random distance up to 10 km

                    // Получаем userId из SharedPreferences
                    val userId = requireContext()
                        .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .getInt("user_id", -1)

                    if (userId == -1) {
                        // Важно! Toast нужно показывать на главном потоке
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }


                    val newActivity = ActivityEntity(
                        id = 0, // Room will auto-generate id
                        userId = userId,
                        type = type,
                        startDate = startDate,
                        endDate = endDate,
                        distance = distance,
                        durationMillis = durationMillis,
                        coordinates = emptyList() // For now, empty list
                    )
                    activityDao.insertActivity(newActivity)
                }

                val fragment = ActiveActivityFragment()
                fragment.arguments = Bundle().apply {
                    putString("activityType", type.displayName)
                }
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .hide(this)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            } ?: run {
                // TODO: Показать сообщение, что нужно выбрать тип активности
            }
        }
    }
    override fun onResume() {
        super.onResume()
        Log.d("FAB_DEBUG", "onResume NewActivityFragment")
        (activity as? MainActivity)?.binding?.fabNewActivity?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 