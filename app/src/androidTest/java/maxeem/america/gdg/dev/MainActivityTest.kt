package maxeem.america.gdg.dev

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import maxeem.america.gdg.MainActivity
import maxeem.america.gdg.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    /* Instantiate an IntentsTestRule object. */
    @get:Rule
    var intentsRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    @Test
    fun openGdgList() {
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.loading))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.gdg_search))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}