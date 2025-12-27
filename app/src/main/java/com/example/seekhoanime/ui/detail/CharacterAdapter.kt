package com.example.seekhoanime.ui.detail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.seekhoanime.databinding.ItemCharacterBinding
import com.example.seekhoanime.domain.model.AnimeCharacter
import com.example.seekhoanime.ui.common.UiConfig


class CharacterAdapter : RecyclerView.Adapter<CharacterAdapter.VH>() {

    private val items = mutableListOf<AnimeCharacter>()

    fun submit(list: List<AnimeCharacter>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(private val b: ItemCharacterBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(item: AnimeCharacter) {
            b.txtName.text = item.name

            val initial = item.name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            b.txtInitial.text = initial

            val canShowImage = UiConfig.SHOW_PROFILE_IMAGES && !item.imageUrl.isNullOrBlank()

            b.imgProfile.isVisible = canShowImage
            b.txtInitial.isVisible = !canShowImage

            if (canShowImage) {
                b.imgProfile.load(item.imageUrl) {
                    crossfade(true)

                    // If image fails to load → fallback to initials
                    listener(
                        onError = { _, _ ->
                            b.imgProfile.isVisible = false
                            b.txtInitial.isVisible = true
                        }
                    )
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
}
