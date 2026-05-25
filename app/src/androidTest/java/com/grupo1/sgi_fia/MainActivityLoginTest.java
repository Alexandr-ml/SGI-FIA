package com.grupo1.sgi_fia;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityLoginTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void credencialesInvalidasMantienenLogin() {
        onView(withId(R.id.edTxtUsuario)).perform(replaceText("Administracion FIA"));
        onView(withId(R.id.edTxtContrasena)).perform(replaceText("incorrecta"));
        closeSoftKeyboard();

        onView(withId(R.id.btnIniciarSesion)).perform(click());

        onView(withText("Acceso administrativo")).check(matches(isDisplayed()));
        onView(withId(R.id.btnIniciarSesion)).check(matches(isDisplayed()));
    }

    @Test
    public void credencialesValidasAbrenMenu() {
        onView(withId(R.id.edTxtUsuario)).perform(replaceText("Administracion FIA"));
        onView(withId(R.id.edTxtContrasena)).perform(replaceText("FIA20268"));
        closeSoftKeyboard();

        onView(withId(R.id.btnIniciarSesion)).perform(click());

        onView(withText("Acciones")).check(matches(isDisplayed()));
    }
}
