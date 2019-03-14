package com.appeaser.nbateamviewer.presentation.teamlist

import android.widget.ProgressBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.appeaser.nbateamviewer.InjectedActivityTestRule
import com.appeaser.nbateamviewer.R
import com.appeaser.nbateamviewer.domain.entity.SortBy
import com.appeaser.nbateamviewer.domain.entity.Team
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.random.Random

class TeamListActivityTest {

    @get:Rule
    val activityTestRule = InjectedActivityTestRule(TeamListActivity::class.java) {
        it.presenter = presenter
    }

    private val presenter: TeamListContract.Presenter = mock()
    private lateinit var screen: Screen

    @Before
    fun setup() {
        screen = Screen(activityTestRule)
    }

    @Test
    fun attachViewToPresenterOnLaunch() {
        screen.launch()

        verify(presenter).attachView(activityTestRule.activity)
    }

    @Test
    fun showLoading() {
        screen.launch()

        screen.showLoading()

        screen.assertLoadingViewDisplayed()
    }

    @Test
    fun hideLoading() {
        screen.launch()

        screen.showLoading()
        screen.hideLoading()

        screen.assertLoadingViewHidden()
    }

    @Test
    fun showConnectionIssueErrorMessage() {
        screen.launch()

        screen.showConnectionIssueErrorMessage()

        screen.assertConnectionIssueErrorMessageShown()
    }

    @Test
    fun showHttpFailureErrorMessage() {
        screen.launch()

        screen.showHttpFailureErrorMessage()

        screen.assertHttpFailureErrorMessageShown()
    }

    @Test
    fun showGenericErrorMessage() {
        screen.launch()

        screen.showGenericErrorMessage()

        screen.assertGenericErrorMessageShown()
    }

    @Test
    fun showTeams() {
        screen.launch()

        val teams = createTeamList()

        screen.showTeams(teams)

        screen.assertTeamsShown(teams)
    }

    @Test
    fun sortAlphabeticallyAsc() {
        screen.launch()

        screen.chooseAlphabeticalAscSortingOption()

        verify(presenter).sortTeamListBy(SortBy.ALPHABETICAL_ASC)
    }

    @Test
    fun sortAlphabeticallyDesc() {
        screen.launch()

        screen.chooseAlphabeticalDescSortingOption()

        verify(presenter).sortTeamListBy(SortBy.ALPHABETICAL_DESC)
    }

    @Test
    fun sortByWinsAsc() {
        screen.launch()

        screen.chooseWinsAscSortingOption()

        verify(presenter).sortTeamListBy(SortBy.WINS_ASC)
    }

    @Test
    fun sortByWinsDesc() {
        screen.launch()

        screen.chooseWinsDescSortingOption()

        verify(presenter).sortTeamListBy(SortBy.WINS_DESC)
    }

    @Test
    fun sortByLossesAsc() {
        screen.launch()

        screen.chooseLossesAscSortingOption()

        verify(presenter).sortTeamListBy(SortBy.LOSSES_ASC)
    }

    @Test
    fun sortByLossesDesc() {
        screen.launch()

        screen.chooseLossesDescSortingOption()

        verify(presenter).sortTeamListBy(SortBy.LOSSES_DESC)
    }

    private fun createTeamList(): List<Team> {
        val teamList = mutableListOf<Team>()
        val repeatCount = Random.nextInt(20)
        repeat(repeatCount) {
            teamList.add(
                Team(it, UUID.randomUUID().toString(), it + 1, it + 2, listOf())
            )
        }
        return teamList
    }

    class Screen(private val rule: ActivityTestRule<TeamListActivity>) {

        private lateinit var view: TeamListContract.View

        fun launch() {
            rule.launchActivity(null)
            view = rule.activity

            // progress bar's animated drawable causes issues with espresso - replace it with a static drawable
            rule.activity.findViewById<ProgressBar>(R.id.loading_view).apply {
                indeterminateDrawable = rule.activity.getDrawable(R.drawable.ic_sort)
            }
        }

        fun showLoading() {
            onMainThread { view.showLoading() }
        }

        fun assertLoadingViewDisplayed() {
            onView(withId(R.id.loading_view)).check(matches(isDisplayed()))
            onView(withId(R.id.rv_team_list)).check(matches(not(isDisplayed())))
            onView(withId(R.id.ll_error_message)).check(matches(not(isDisplayed())))
        }

        fun hideLoading() {
            onMainThread { view.hideLoading() }
        }

        fun assertLoadingViewHidden() {
            onView(withId(R.id.loading_view)).check(matches(not(isDisplayed())))
        }

        fun showConnectionIssueErrorMessage() {
            onMainThread { view.showConnectionIssueErrorMessage() }
        }

        fun assertConnectionIssueErrorMessageShown() {
            onView(withId(R.id.ll_error_message)).check(matches(isDisplayed()))
            onView(withId(R.id.loading_view)).check(matches(not(isDisplayed())))
            onView(withId(R.id.rv_team_list)).check(matches(not(isDisplayed())))

            onView(withId(R.id.tv_error_message))
                .check(matches(withText(R.string.connection_issue_error_message)))
        }

        fun showHttpFailureErrorMessage() {
            onMainThread { view.showHttpFailureErrorMessage() }
        }

        fun assertHttpFailureErrorMessageShown() {
            onView(withId(R.id.ll_error_message)).check(matches(isDisplayed()))
            onView(withId(R.id.loading_view)).check(matches(not(isDisplayed())))
            onView(withId(R.id.rv_team_list)).check(matches(not(isDisplayed())))

            onView(withId(R.id.tv_error_message))
                .check(matches(withText(R.string.http_failure_error_message)))
        }

        fun showGenericErrorMessage() {
            onMainThread { view.showGenericErrorMessage() }
        }

        fun assertGenericErrorMessageShown() {
            onView(withId(R.id.ll_error_message)).check(matches(isDisplayed()))
            onView(withId(R.id.loading_view)).check(matches(not(isDisplayed())))
            onView(withId(R.id.rv_team_list)).check(matches(not(isDisplayed())))

            onView(withId(R.id.tv_error_message))
                .check(matches(withText(R.string.generic_error_message)))
        }

        fun showTeams(teamList: List<Team>) {
            onMainThread { view.showTeams(teamList) }
        }

        fun assertTeamsShown(teamList: List<Team>) {
            teamList.forEachIndexed { index, team ->
                onView(withId(R.id.rv_team_list))
                    .perform(scrollToPosition<TeamListAdapter.TeamViewHolder>(index))

                onView(withText(team.fullName)).check(matches(isDisplayed()))

                val winsText = rule.activity.getString(R.string.text_wins, team.wins)
                val lossesText = rule.activity.getString(R.string.text_losses, team.losses)
                onView(withText(winsText)).check(matches(isDisplayed()))
                onView(withText(lossesText)).check(matches(isDisplayed()))
            }
        }

        fun chooseAlphabeticalAscSortingOption() {
            onView(withId(R.id.sort)).perform(click())
            onView(withText(R.string.menu_title_sort_alphabetically)).perform(click())
            onView(withText(R.string.menu_title_sort_asc)).perform(click())
        }

        fun chooseAlphabeticalDescSortingOption() {
            onView(withId(R.id.sort)).perform(click())
            onView(withText(R.string.menu_title_sort_alphabetically)).perform(click())
            onView(withText(R.string.menu_title_sort_desc)).perform(click())
        }

        fun chooseWinsAscSortingOption() {
            onView(withId(R.id.sort)).perform(click())
            onView(withText(R.string.menu_title_sort_by_wins)).perform(click())
            onView(withText(R.string.menu_title_sort_asc)).perform(click())
        }

        fun chooseWinsDescSortingOption() {
            onView(withId(R.id.sort)).perform(click())
            onView(withText(R.string.menu_title_sort_by_wins)).perform(click())
            onView(withText(R.string.menu_title_sort_desc)).perform(click())
        }

        fun chooseLossesAscSortingOption() {
            onView(withId(R.id.sort)).perform(click())
            onView(withText(R.string.menu_title_sort_by_losses)).perform(click())
            onView(withText(R.string.menu_title_sort_asc)).perform(click())
        }

        fun chooseLossesDescSortingOption() {
            onView(withId(R.id.sort)).perform(click())
            onView(withText(R.string.menu_title_sort_by_losses)).perform(click())
            onView(withText(R.string.menu_title_sort_desc)).perform(click())
        }

        private fun onMainThread(block: () -> Unit) = rule.runOnUiThread(block)
    }
}