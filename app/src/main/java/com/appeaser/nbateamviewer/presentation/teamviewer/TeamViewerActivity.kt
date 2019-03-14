package com.appeaser.nbateamviewer.presentation.teamviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appeaser.nbateamviewer.R
import com.appeaser.nbateamviewer.domain.entity.Team
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.act_team_viewer.*
import javax.inject.Inject

class TeamViewerActivity : AppCompatActivity(), TeamViewerContract.View {

    @Inject
    lateinit var presenter: TeamViewerContract.Presenter

    private val playerListAdapter = PlayerListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.act_team_viewer)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        rv_player_list.apply {
            layoutManager = LinearLayoutManager(
                this@TeamViewerActivity,
                RecyclerView.VERTICAL, false
            )
            addItemDecoration(DividerItemDecoration(this@TeamViewerActivity, LinearLayout.VERTICAL))
            adapter = playerListAdapter
        }

        val teamId = intent.extras?.getInt(KEY_EXTRA_TEAM_ID)

        if (teamId == null) {
            Log.i(TAG, "Team ID not found in extras, finishing up")
            finishUpWithError()
            return
        }

        presenter.attachView(this, teamId)
    }

    override fun showLoading() {
        rv_player_list.visibility = View.GONE
        loading_view.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading_view.visibility = View.GONE
    }

    override fun showTeam(team: Team) {
        rv_player_list.visibility = View.VISIBLE

        supportActionBar?.title = team.fullName
        tv_wins.text = getString(R.string.text_wins, team.wins)
        tv_losses.text = getString(R.string.text_losses, team.losses)

        playerListAdapter.updatePlayerList(team.players)
    }

    override fun finishUpWithError() {
        Toast.makeText(this, R.string.team_not_found_error_message, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    companion object {
        private const val TAG = "[TeamViewerActivity]"

        @VisibleForTesting
        const val KEY_EXTRA_TEAM_ID = "key.extra.team.id"

        fun launch(context: Context, teamId: Int): Intent {
            return Intent(context, TeamViewerActivity::class.java).apply {
                putExtra(KEY_EXTRA_TEAM_ID, teamId)
            }
        }
    }
}