/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.testroid.xposed.lenovomultiwinmod;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * This is a simple wrapper for {@link android.widget.LinearLayout} that implements the {@link android.widget.Checkable}
 * interface by keeping an internal 'checked' state flag.
 * <p>
 * This can be used as the root view for a custom list item layout for
 * {@link android.widget.AbsListView} elements with a
 * {@link android.widget.AbsListView#setChoiceMode(int) choiceMode} set.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private boolean mChecked = false;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean b) {
        if (b != mChecked) {
            mChecked = b;
            refreshDrawableState();
        }
    }

    public void toggle() {
        TextView textView = (TextView)findViewById(R.id.app_paackage);
        String test = textView.getText().toString();
        Log.v("checkLinLayout", "working: " +test);
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        TextView textView = (TextView)findViewById(R.id.app_paackage);
        String test = textView.getText().toString();
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
            //Log.v("checkLinLayout", "add: " +test);
            if (!AllAppsActivity.arrAppList.contains(test)) {
                AllAppsActivity.arrAppList.add(test);
            }
        } else {
            //Log.v("checkLinLayout", "del: " +test);
            while (AllAppsActivity.arrAppList.contains(test)) {
                AllAppsActivity.arrAppList.remove(test);
            }
        }
        if (AllAppsActivity.arrAppList.contains("")) {
        	AllAppsActivity.arrAppList.remove("");
        }
        if (AllAppsActivity.arrAppList.contains(null)) {
        	AllAppsActivity.arrAppList.remove(null);
        }
        return drawableState;
    }
}
