package com.appeaser.nbateamviewer.presentation.teamlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appeaser.nbateamviewer.R
import com.appeaser.nbateamviewer.domain.entity.SortBy
import com.appeaser.nbateamviewer.domain.entity.Team
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.act_team_list.*
import javax.inject.Inject

class TeamListActivity : AppCompatActivity(), TeamListContract.View {

    @Inject
    lateinit var presenter: TeamListContract.Presenter

    private val teamListAdapter = TeamListAdapter { team ->
        // TODO: launch team viewer act
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.act_team_list)
        setSupportActionBar(toolbar)

        rv_team_list.apply {
            layoutManager = LinearLayoutManager(this@TeamListActivity, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(this@TeamListActivity, LinearLayout.VERTICAL))
            itemAnimator = DefaultItemAnimator().apply {
                moveDuration = ANIMATION_DURATION
                addDuration = ANIMATION_DURATION
                removeDuration = ANIMATION_DURATION
            }
            adapter = teamListAdapter
        }

        presenter.attachView(this, savedInstanceState?.getSerializable(KEY_SORT_BY) as? SortBy)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_team_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val sortBy = when (item?.itemId) {
            R.id.sort_alphabetically_asc -> SortBy.ALPHABETICAL_ASC
            R.id.sort_alphabetically_desc -> SortBy.ALPHABETICAL_DESC
            R.id.sort_by_wins_asc -> SortBy.WINS_ASC
            R.id.sort_by_wins_desc -> SortBy.WINS_DESC
            R.id.sort_by_losses_asc -> SortBy.LOSSES_ASC
            R.id.sort_by_losses_desc -> SortBy.LOSSES_DESC
            else -> null
        }

        sortBy?.run {
            presenter.sortTeamListBy(this)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showLoading() {
        rv_team_list.visibility = View.GONE
        ll_error_message.visibility = View.GONE
        loading_view.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading_view.visibility = View.GONE
    }

    override fun showTeams(teams: List<Team>) {
        loading_view.visibility = View.GONE
        ll_error_message.visibility = View.GONE
        rv_team_list.visibility = View.VISIBLE

        teamListAdapter.updateTeamList(teams)
    }

    override fun showConnectionIssueErrorMessage() {
        showErrorMessage(R.string.connection_issue_error_message)
    }

    override fun showHttpFailureErrorMessage() {
        showErrorMessage(R.string.http_failure_error_message)
    }

    override fun showGenericErrorMessage() {
        showErrorMessage(R.string.generic_error_message)
    }

    private fun showErrorMessage(@StringRes errorMessageId: Int) {
        loading_view.visibility = View.GONE
        rv_team_list.visibility = View.GONE

        tv_error_message.text = getString(errorMessageId)
        ll_error_message.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_SORT_BY, presenter.getCurrentSortByPreference())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    companion object {
        private const val ANIMATION_DURATION = 450L

        private const val KEY_SORT_BY = "key.sort.by"
    }
}