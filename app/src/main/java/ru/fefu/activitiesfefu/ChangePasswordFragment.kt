package ru.fefu.activitiesfefu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.fefu.activitiesfefu.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            // Возвращаемся назад по backstack
            requireActivity().onBackPressedDispatcher.onBackPressed()
            // Если используете Navigation Component:
            // findNavController().popBackStack()
        }

        binding.applyButton.setOnClickListener {
            val userId = requireContext()
                .getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                .getInt("user_id", -1)
            if (userId == -1) {
                android.widget.Toast.makeText(requireContext(), "Пользователь не найден", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val oldPassword = binding.EditOldPassword.text.toString()
            val newPassword = binding.EditNewPassword.text.toString()
            val repeatPassword = binding.EditRepeatPassword.text.toString()

            if (oldPassword.isBlank() || newPassword.isBlank() || repeatPassword.isBlank()) {
                android.widget.Toast.makeText(requireContext(), "Заполните все поля", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != repeatPassword) {
                android.widget.Toast.makeText(requireContext(), "Пароли не совпадают", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = (requireActivity().application as ActivityApplication).database

            viewLifecycleOwner.lifecycleScope.launch {
                val user = db.userDao().getUserById(userId)
                if (user == null) {
                    android.widget.Toast.makeText(requireContext(), "Пользователь не найден", android.widget.Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (user.password != oldPassword) {
                    android.widget.Toast.makeText(requireContext(), "Старый пароль неверен", android.widget.Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val updatedUser = user.copy(password = newPassword)
                db.userDao().update(updatedUser)
                android.widget.Toast.makeText(requireContext(), "Пароль изменён", android.widget.Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // вернуться назад
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 