package com.github.caracal.jarvis.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.caracal.jarvis.databinding.FragmentShoppingBinding

/**
 * Placeholder fragment for the Shopping feature.
 *
 * Displays a "Coming Soon" message styled to the Jarvis / Iron Man theme.
 * Replace the body of this fragment with the real Shopping implementation
 * when the feature is ready.
 */
class ShoppingFragment : Fragment() {

    private var _binding: FragmentShoppingBinding? = null

    /** Non-null binding reference valid between [onCreateView] and [onDestroyView]. */
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

