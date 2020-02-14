package com.moulinette.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.moulinette.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstandFunctions {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static Matcher matcher;
    public static Pattern pattern = Pattern.compile( EMAIL_PATTERN );

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 1);
    }

    public static boolean validateEmail(String email) {
        matcher = EMAIL_ADDRESS_PATTERN.matcher( email );
        return matcher.matches();
    }

    public static void replaceFragment(FragmentActivity ac, Fragment fragment) {
        FragmentTransaction transaction = ac.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_up,
                R.animator.slide_down,
                R.animator.slide_up,
                R.animator.slide_down);
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void addFragment(FragmentActivity ac, Fragment fragment) {
        String backStateName =  fragment.getClass().getName();
        FragmentTransaction transaction = ac.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_up,
                R.animator.slide_down,
                R.animator.slide_up,
                R.animator.slide_down);
        transaction.add(R.id.frame_container, fragment);
        transaction.addToBackStack(backStateName);
        transaction.commit();
    }

    public static void removeFragment(FragmentActivity ac, Fragment fragment){
        FragmentManager fragmentManager = ac.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
