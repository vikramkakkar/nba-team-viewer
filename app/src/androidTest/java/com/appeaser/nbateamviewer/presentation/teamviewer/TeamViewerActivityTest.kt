package com.appeaser.nbateamviewer.presentation.teamviewer

import android.content.Intent
import android.widget.ProgressBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.appeaser.nbateamviewer.InjectedActivityTestRule
import com.appeaser.nbateamviewer.R
import com.appeaser.nbateamviewer.domain.entity.Player
import com.appeaser.nbateamviewer.domain.entity.Team
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class TeamViewerActivityTest {

    @get:Rule
    val activityTestRule = InjectedActivityTestRule(TeamViewerActivity::class.java) {
        it.presenter = presenter
    }

    private val presenter: TeamViewerContract.Presenter = mock()
    private lateinit var screen: Screen
    private val teamId = 1

    @Before
    fun setup() {
        screen = Screen(activityTestRule)
    }

    @Test
    fun attachViewToPresenterOnLaunch() {
        screen.launch(teamId)

        verify(presenter).attachView(activityTestRule.activity, teamId)
    }

    @Test
    fun finishUpIfTeamIdNotPassed() {
        screen.launch(null)

        verify(presenter, never()).attachView(any(), any())
    }

    @Test
    fun showLoading() {
        screen.launch(teamId)

        screen.showLoading()

        screen.assertLoadingViewShown()
    }

    @Test
    fun hideLoading() {
        screen.launch(teamId)

        screen.showLoading()
        screen.hideLoading()

        screen.assertLoadingViewHidden()
    }

    @Test
    fun showTeam() {
        screen.launch(teamId)

        val team = createTeam()

        screen.showTeam(team)

        screen.assertTeamShown(team)
    }

    private fun createTeam(): Team {
        val players = mutableListOf<Player>()
        val repeatCount = Random.nextInt(20)
        repeat(repeatCount) {
            players.add(
                Player(it, it.toString(), it.toString(), it.toString(), it)
            )
        }
        return Team(teamId, "some team", 5, 8, players)
    }

    class Screen(private val rule: ActivityTestRule<TeamViewerActivity>) {

        private lateinit var view: TeamViewerContract.View

        fun launch(teamId: Int?) {
            rule.launchActivity(
                teamId?.let {
                    Intent().putExtra(TeamViewerActivity.KEY_EXTRA_TEAM_ID, it)
                }
            )
            view = rule.activity

            // progress bar's animated drawable causes issues with espresso - replace it with a static drawable
            rule.activity.findViewById<ProgressBar>(R.id.loading_view).apply {
                indeterminateDrawable = rule.activity.getDrawable(R.drawable.ic_sort)
            }
        }

        fun showLoading() {
            onMainThread { view.showLoading() }
        }

        fun assertLoadingViewShown() {
            onView(withId(R.id.loading_view)).check(matches(isDisplayed()))
            onView(withId(R.id.rv_player_list)).check(matches(not(isDisplayed())))
        }

        fun hideLoading() {
            onMainThread { view.hideLoading() }
        }

        fun assertLoadingViewHidden() {
            onView(withId(R.id.loading_view)).check(matches(not(isDisplayed())))
        }

        fun showTeam(team: Team) {
            onMainThread { view.showTeam(team) }
        }

        fun assertTeamShown(team: Team) {
            val winsText = rule.activity.getString(R.string.text_wins, team.wins)
            val lossesText = rule.activity.getString(R.string.text_losses, team.losses)

            // toolbar title
            onView(withText(team.fullName)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_wins)).check(matches(withText(winsText)))
            onView(withId(R.id.tv_losses)).check(matches(withText(lossesText)))

            team.players.forEachIndexed { index, player ->
                onView(withId(R.id.rv_player_list)).perform(
                    scrollToPosition<PlayerListAdapter.PlayerViewHolder>(index)
                )

                val playerNameText = rule.activity.getString(R.string.player_name, player.firstName, player.lastName)
                val playerPositionText = rule.activity.getString(R.string.player_position, player.position)
                val playerNumberText = rule.activity.getString(R.string.player_number, player.number)

                onView(withText(playerNameText)).check(matches(isDisplayed()))
                onView(withText(playerPositionText)).check(matches(isDisplayed()))
                onView(withText(playerNumberText)).check(matches(isDisplayed()))
            }
        }

        private fun onMainThread(block: () -> Unit) = rule.runOnUiThread(block)
    }
}