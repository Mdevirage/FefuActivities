package ru.fefu.activitiesfefu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.fefu.activitiesfefu.databinding.FragmentActiveActivityBinding

class ActiveActivityFragment : Fragment() {
    private var _binding: FragmentActiveActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем переданные данные
        arguments?.let { args ->
            val activityType = args.getString("activityType", "")
            val distance = args.getFloat("distance", 0f)
            val durationMillis = args.getLong("durationMillis", 0L)
            
            binding.activityType.text = activityType
            binding.activityDistance.text = String.format("%.1f км", distance)
            
            // Конвертируем миллисекунды в формат HH:MM:SS
            val hours = (durationMillis / (1000 * 60 * 60)).toInt()
            val minutes = ((durationMillis % (1000 * 60 * 60)) / (1000 * 60)).toInt()
            val seconds = ((durationMillis % (1000 * 60)) / 1000).toInt()
            binding.activityDuration.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        val finishButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.finish_button)
        finishButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ActivityFragment())
                .commit()
            // Если используете Navigation Component:
            // findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 