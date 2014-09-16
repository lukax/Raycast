package com.raycast.controller.base;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.raycast.R;
import com.raycast.controller.SettingsActivity;

/**
 * Created by Lucas on 15/09/2014.
 */
public class RaycastBaseActivity extends Activity {
    private static final int RESULT_SETTINGS = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
