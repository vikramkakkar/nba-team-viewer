package com.appeaser.nbateamviewer.presentation.teamviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.ViewCompat.setBackground
import androidx.recyclerview.widget.RecyclerView
import com.appeaser.nbateamviewer.R
import com.appeaser.nbateamviewer.domain.entity.Player
import kotlinx.android.synthetic.main.view_player.view.*

class PlayerListAdapter : RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder>() {

    private val playerList = mutableListOf<Player>()

    fun updatePlayerList(players: List<Player>) {
        playerList.clear()
        playerList.addAll(players)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_player, parent, false)
        )
    }

    override fun getItemCount() = playerList.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bindPlayer(playerList[position])
    }

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            setBackground(itemView.tv_player_number, PlayerNumberBg(getColor(view.context, R.color.colorPrimary)))
        }

        fun bindPlayer(player: Player) {
            itemView.tv_player_name.text = itemView.context.getString(
                R.string.player_name,
                player.firstName, player.lastName
            )
            itemView.tv_player_number.text = itemView.context.getString(R.string.player_number, player.number)
            itemView.tv_player_position.text = itemView.context.getString(R.string.player_position, player.position)
        }
    }
}