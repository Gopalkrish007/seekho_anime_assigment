package com.example.seekhoanime.ui.list


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seekhoanime.AppGraph
import com.example.seekhoanime.R
import com.example.seekhoanime.databinding.FragmentAnimeListBinding
import com.example.seekhoanime.ui.common.ViewModelFactory
import com.seekho.anime.ui.detail.AnimeDetailFragment
import kotlinx.coroutines.launch


class AnimeListFragment : Fragment() {

    companion object {
        fun newInstance() = AnimeListFragment()
    }

    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: AnimeListViewModel
    private lateinit var adapter: AnimeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(
            this,
            ViewModelFactory { AnimeListViewModel(AppGraph.repo) }
        )[AnimeListViewModel::class.java]

        // Bind once (DB observe + network changes)
        vm.bind(AppGraph.networkMonitor.isOnline)

        adapter = AnimeListAdapter { anime ->
            parentFragmentManager.beginTransaction()
                .replace(
                    (requireActivity().findViewById<View>(R.id.fragmentContainer)).id,
                    AnimeDetailFragment.newInstance(anime.id)
                )
                .addToBackStack("detail")
                .commit()
        }

        binding.rvAnime.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnime.adapter = adapter

        binding.btnRetry.setOnClickListener { vm.refresh() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                vm.state.collect { state ->
                    // Loading indicator
                    binding.progress.isVisible = state.loading && state.items.isEmpty()

                    // Error view (only show if no cached list)
                    val showError = state.error != null && state.items.isEmpty()
                    binding.txtError.isVisible = showError
                    binding.btnRetry.isVisible = showError
                    binding.txtError.text = state.error ?: ""

                    // Always show cached list if available
                    adapter.submit(state.items)

                    // Optional: if you added txtOffline in XML
                    binding.txtOffline.isVisible = state.isOffline
                }
            }
        }

    }

    override fun onDestroyView() {
        binding.rvAnime.adapter = null
        _binding = null
        super.onDestroyView()
    }
}

