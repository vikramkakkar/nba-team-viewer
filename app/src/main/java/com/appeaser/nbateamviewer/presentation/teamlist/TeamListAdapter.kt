package com.appeaser.nbateamviewer.presentation.teamlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.appeaser.nbateamviewer.R
import com.appeaser.nbateamviewer.domain.entity.Team
import com.appeaser.nbateamviewer.presentation.teamlist.TeamListAdapter.TeamViewHolder
import kotlinx.android.synthetic.main.view_team.view.*

class TeamListAdapter(private val itemClickAction: (Team) -> Unit) : Adapter<TeamViewHolder>() {

    // callback used by [AsyncListDiffer] to check whether items have changed position
    private val diffItemCallback = object : DiffUtil.ItemCallback<Team>() {
        override fun areItemsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem.id == newItem.id
        }
    }

    // used to avoid the call to [notifyDataSetChanged()] by diffing the
    // existing & updated data list, and notifying [Adapter] of the changes.
    private val differ = AsyncListDiffer(this, diffItemCallback)


    fun updateTeamList(teams: List<Team>) {
        differ.submitList(teams)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        return TeamViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_team, parent, false)
        )
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = differ.currentList[position]
        holder.bindTeam(team, itemClickAction)
    }

    class TeamViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTeam(team: Team, itemClickAction: (Team) -> Unit) {
            itemView.tv_team_name.text = team.fullName
            itemView.tv_wins.text = itemView.context.getString(R.string.text_wins, team.wins)
            itemView.tv_losses.text = itemView.context.getString(R.string.text_losses, team.losses)

            itemView.setOnClickListener { itemClickAction(team) }
        }
    }
}