package com.example.android.popularmovies.Utils.BottomNavigationUtils;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Implementation copy-pasted from
 * https://stackoverflow.com/questions/44777869/hide-show-bottomnavigationview-on-scroll
 *
 * Answer posted by Abhishek Singh;
 * Edited by Massimo Fazzolari
 */

public class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {

    private int height;

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, BottomNavigationView child, int layoutDirection) {
        height = child.getHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       BottomNavigationView child, @NonNull
                                               View directTargetChild, @NonNull View target,
                                       int axes, int type)
    {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                  @NonNull BottomNavigationView child,
                                  @NonNull View target, int dx, int dy,
                                  @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);

        if (dy < 0){
            showBottomNavigationView(child);
        } else if (dy > 0){
            hideBottomNavigationView(child);
        }

    }

    private void showBottomNavigationView(BottomNavigationView view){
        view.animate().translationY(0);
    }

    private void hideBottomNavigationView(BottomNavigationView view){
        view.animate().translationY(view.getHeight());
    }
}
