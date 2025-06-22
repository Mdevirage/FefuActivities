package ru.fefu.activitiesfefu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.fefu.activitiesfefu.databinding.FragmentProfileEditBinding

class ProfileEditFragment : Fragment() {
    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val CHANGE_PASSWORD_TAG = "CHANGE_PASSWORD_TAG"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d("FAB_DEBUG", "onResume ProfileEditFragment")
        (activity as? MainActivity)?.binding?.fabNewActivity?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = requireContext()
            .getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            .getInt("user_id", -1)

        if (userId == -1) {
            // Если пользователь не найден, можно выйти или показать ошибку
            android.widget.Toast.makeText(requireContext(), "Пользователь не найден", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        // Получаем доступ к базе данных
        val db = (requireActivity().application as ActivityApplication).database

        // Загружаем пользователя из базы и отображаем данные
        viewLifecycleOwner.lifecycleScope.launch {
            val user = db.userDao().getUserById(userId)
            user?.let {
                binding.editLogin.setText(it.email)
                binding.editNickname.setText(it.name)
            }
        }

        binding.toolbar.findViewById<TextView>(R.id.save_button).setOnClickListener {
            val userId = requireContext()
                .getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                .getInt("user_id", -1)
            if (userId == -1) {
                android.widget.Toast.makeText(requireContext(), "Пользователь не найден", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newEmail = binding.editLogin.text.toString().trim()
            val newName = binding.editNickname.text.toString().trim()

            // Проверяем, что поля не пустые
            if (newEmail.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Логин не может быть пустым", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newName.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Никнейм не может быть пустым", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = (requireActivity().application as ActivityApplication).database

            viewLifecycleOwner.lifecycleScope.launch {
                val user = db.userDao().getUserById(userId)
                if (user != null) {
                    // Проверяем уникальность логина (исключая текущего пользователя)
                    val emailExists = db.userDao().isEmailExists(newEmail, userId)
                    if (emailExists > 0) {
                        android.widget.Toast.makeText(requireContext(), "Такой логин уже используется", android.widget.Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Проверяем уникальность никнейма (исключая текущего пользователя)
                    val nicknameExists = db.userDao().isNicknameExists(newName, userId)
                    if (nicknameExists > 0) {
                        android.widget.Toast.makeText(requireContext(), "Такой никнейм уже используется", android.widget.Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val updatedUser = user.copy(
                        email = newEmail,
                        name = newName
                        // пароль и пол не меняем здесь
                    )
                    db.userDao().update(updatedUser)
                    android.widget.Toast.makeText(requireContext(), "Профиль сохранён", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.changePasswordButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                replace(R.id.fragment_container, ChangePasswordFragment(), CHANGE_PASSWORD_TAG)
                addToBackStack(null)
                commit()
            }
        }

        binding.logoutButton.setOnClickListener {
            // Очищаем userId из SharedPreferences
            requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                .edit().remove("user_id").apply()

            // Переходим на WelcomeActivity (или LoginActivity)
            val intent = android.content.Intent(requireContext(), WelcomeActivity::class.java)
            // Если хотите, чтобы пользователь не мог вернуться назад по back, очищаем back stack:
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 