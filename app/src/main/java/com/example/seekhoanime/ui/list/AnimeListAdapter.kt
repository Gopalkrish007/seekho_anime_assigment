package com.example.seekhoanime.ui.list

import com.example.seekhoanime.domain.model.AnimeSummary
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.seekhoanime.databinding.ItemAnimeBinding


class AnimeListAdapter(
    private val onClick: (AnimeSummary) -> Unit
) : RecyclerView.Adapter<AnimeListAdapter.VH>() {

    private val items = mutableListOf<AnimeSummary>()

    fun submit(list: List<AnimeSummary>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemAnimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AnimeSummary) {
            binding.txtTitle.text = item.title
            binding.txtEpisodes.text = item.episodesText
            binding.txtRating.text = item.ratingText
            binding.imgPoster.load(item.imageUrl)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
}
