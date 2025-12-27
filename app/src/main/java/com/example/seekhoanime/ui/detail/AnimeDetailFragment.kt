package com.seekho.anime.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.seekhoanime.AppGraph
import com.example.seekhoanime.data.repository.AnimeRepositoryImpl
import com.example.seekhoanime.databinding.FragmentAnimeDetailBinding
import com.example.seekhoanime.ui.common.UiState
import com.example.seekhoanime.ui.common.ViewModelFactory
import com.example.seekhoanime.ui.detail.AnimeDetailViewModel
import com.example.seekhoanime.ui.detail.CharacterAdapter

import kotlinx.coroutines.launch


class AnimeDetailFragment : Fragment() {

    companion object {
        private const val ARG_ANIME_ID = "arg_anime_id"

        fun newInstance(animeId: Int) = AnimeDetailFragment().apply {
            arguments = Bundle().apply { putInt(ARG_ANIME_ID, animeId) }
        }
    }

    private var _binding: FragmentAnimeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: AnimeDetailViewModel
    private lateinit var castAdapter: CharacterAdapter

    private val animeId: Int by lazy {
        requireArguments().getInt(ARG_ANIME_ID, -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (animeId == -1) {
            parentFragmentManager.popBackStack()
            return
        }
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        var trailerInteracted = false

        binding.webTrailer.setOnTouchListener { _, event ->
            if (!trailerInteracted && event.action == MotionEvent.ACTION_DOWN) {
                trailerInteracted = true
                binding.btnWatchTrailer.isVisible = false
            }
            false
        }

        // ViewModel (uses AppGraph repo that is backed by Room)
        vm = ViewModelProvider(
            this,
            ViewModelFactory { AnimeDetailViewModel(AppGraph.repo) }
        )[AnimeDetailViewModel::class.java]

        // Cast adapter init (you missed this earlier)
        castAdapter = CharacterAdapter()
        binding.rvCast.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCast.adapter = castAdapter

        // Bind once: observe DB + listen to network changes
        vm.bind(animeId, AppGraph.networkMonitor.isOnline)

        // Retry: refresh both
        binding.btnRetry.setOnClickListener {
            vm.refresh(animeId)
            vm.refreshCast(animeId)
        }

        // WebView safe-ish baseline
        binding.webTrailer.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.domStorageEnabled = true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                vm.state.collect { state ->
                    // Loading: show spinner only if no cached detail yet
                    binding.progress.isVisible = state.loading && state.detail == null

                    // Error: show full error only if no cached detail yet
                    val showError = state.error != null && state.detail == null
                    binding.txtError.isVisible = showError
                    binding.btnRetry.isVisible = showError
                    binding.txtError.text = state.error ?: ""

                    val detail = state.detail
                    if (detail != null) {
                        binding.txtTitle.text = detail.title
                        binding.txtMeta.text =
                            "Episodes: ${detail.episodesText}   •   Rating: ${detail.ratingText}"
                        binding.txtGenres.text = "Genres: ${detail.genresText}"
                        binding.txtSynopsis.text = detail.synopsis


                        val youtubeId = detail.trailerYoutubeId
                        val embedUrl = detail.trailerEmbedUrl


                        val isOffline = state.isOffline

                        val trailerEmbed =
                            detail.trailerEmbedUrl
                                ?: detail.trailerYoutubeId?.let { "https://www.youtube-nocookie.com/embed/$it?rel=0&playsinline=1" }

                        val watchUrl = when {
                            !youtubeId.isNullOrBlank() -> "https://www.youtube.com/watch?v=$youtubeId"
                            !detail.trailerEmbedUrl.isNullOrBlank() -> {
                                // convert embed to watch
                                val idFromEmbed = detail.trailerEmbedUrl
                                    .substringAfter("/embed/", "")
                                    .substringBefore("?", "")
                                    .trim()
                                if (idFromEmbed.isNotBlank()) "https://www.youtube.com/watch?v=$idFromEmbed" else null
                            }

                            else -> null
                        }

                        val hasTrailer = !trailerEmbed.isNullOrBlank() || !watchUrl.isNullOrBlank()

                        // default: poster
                        binding.imgPoster.isVisible = true
                        binding.imgPoster.load(detail.posterUrl)

                        binding.webTrailer.isVisible = false

                        // WebView only when online + embed exists
                        val showWebView = hasTrailer && !isOffline && !trailerEmbed.isNullOrBlank()
                        if (showWebView) {
                            binding.webTrailer.isVisible = true
                            binding.imgPoster.isVisible = false

                            val html = """
        <html><body style="margin:0;padding:0;">
        <iframe width="100%" height="100%"
          src="$trailerEmbed"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen>
        </iframe>
        </body></html>
    """.trimIndent()

                            binding.webTrailer.loadDataWithBaseURL(
                                null,
                                html,
                                "text/html",
                                "utf-8",
                                null
                            )
                        }

                        // ✅ Button visible only if WebView is NOT showing
                        binding.btnWatchTrailer.isVisible = !isOffline && !watchUrl.isNullOrBlank()
                        binding.btnWatchTrailer.setOnClickListener {
                            watchUrl?.let { openYoutube(it) }
                        }


                    }

                    // Cast always from DB (can be empty)
                    castAdapter.submit(state.cast)

                }
            }
        }
    }

    private fun openYoutube(watchUrl: String) {
        val videoId = Uri.parse(watchUrl).getQueryParameter("v")

        // Try YouTube app deep link first
        if (!videoId.isNullOrBlank()) {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
            try {
                startActivity(appIntent)
                return
            } catch (_: Exception) {
                // fallback below
            }
        }

        // Fallback: open browser (or YouTube if installed)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(watchUrl)))
    }


    override fun onDestroyView() {
        binding.webTrailer.stopLoading()
        binding.webTrailer.loadUrl("about:blank")
        binding.rvCast.adapter = null
        _binding = null
        super.onDestroyView()
    }
}

