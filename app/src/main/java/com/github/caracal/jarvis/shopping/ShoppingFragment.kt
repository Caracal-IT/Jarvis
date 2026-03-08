package com.github.caracal.jarvis.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.github.caracal.jarvis.databinding.ShoppingFragmentBinding
import com.github.caracal.jarvis.shopping.list.ShoppingListFragment
import com.github.caracal.jarvis.shopping.replenish.ReplenishListFragment
import com.github.caracal.jarvis.shopping.ui.ShoppingViewModel
import com.github.caracal.jarvis.shopping.ui.ShoppingViewModelFactory

/**
 * Root fragment for the Shopping feature.
 *
 * Hosts a two-tab layout containing [ShoppingListFragment] and [ReplenishListFragment].
 * The shared [ShoppingViewModel] is owned here and accessed by child fragments to avoid
 * duplicate ViewModel instantiation.
 */
class ShoppingFragment : Fragment() {

    private var _binding: ShoppingFragmentBinding? = null
    private val binding get() = _binding!!

    /** Shared ViewModel accessed by child fragments via [requireParentFragment]. */
    internal val viewModel: ShoppingViewModel by viewModels {
        ShoppingViewModelFactory(requireContext())
    }

    private lateinit var shoppingListFragment: ShoppingListFragment
    private lateinit var replenishListFragment: ReplenishListFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShoppingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragments()
        setupTabs()
        showShoppingListTab()
    }

    private fun setupFragments() {
        shoppingListFragment = (childFragmentManager.findFragmentByTag(TAG_SHOPPING_LIST)
            as? ShoppingListFragment) ?: ShoppingListFragment()

        replenishListFragment = (childFragmentManager.findFragmentByTag(TAG_REPLENISH)
            as? ReplenishListFragment) ?: ReplenishListFragment()

        childFragmentManager.commit {
            if (!shoppingListFragment.isAdded) {
                add(binding.shoppingTabContent.id, shoppingListFragment, TAG_SHOPPING_LIST)
            }
            if (!replenishListFragment.isAdded) {
                add(binding.shoppingTabContent.id, replenishListFragment, TAG_REPLENISH)
            }
        }
    }

    private fun setupTabs() {
        binding.btnTabShoppingList.setOnClickListener { showShoppingListTab() }
        binding.btnTabReplenish.setOnClickListener { showReplenishTab() }
    }

    private fun showShoppingListTab() {
        childFragmentManager.commit {
            show(shoppingListFragment)
            hide(replenishListFragment)
        }
        binding.btnTabShoppingList.isSelected = true
        binding.btnTabReplenish.isSelected = false
    }

    private fun showReplenishTab() {
        childFragmentManager.commit {
            show(replenishListFragment)
            hide(shoppingListFragment)
        }
        binding.btnTabShoppingList.isSelected = false
        binding.btnTabReplenish.isSelected = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG_SHOPPING_LIST = "shopping_list"
        private const val TAG_REPLENISH = "replenish"
    }
}
