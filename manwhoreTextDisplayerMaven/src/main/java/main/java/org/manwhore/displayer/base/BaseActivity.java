/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.crittercism.app.Crittercism;
import org.manwhore.displayer.logging.BreadCrumbs;
import org.manwhore.displayer.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.activity.RoboActivity;

/**
 *
 * @author sigi
 */
public class BaseActivity extends RoboActivity {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseActivity.class);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
                BreadCrumbs.initBreadCrumbs(this);
                
                boolean serviceDisabled = false;
		Crittercism.init(getApplicationContext(), "4f034cc0b0931537460002d5", serviceDisabled);  
                
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String user = prefs.getString(Constants.SETTINGS_USER, "");
                
                if (user != null && !"".equals(user.trim())) {
                    Crittercism.setUsername(user);
                }
		LOGGER.debug("onCreate - activity {}", this.getComponentName());
	}

	@Override
	protected void onPause() {
		super.onPause();
		LOGGER.debug("onPause - activity {}", this.getComponentName());
	}

	@Override
	protected void onResume() {
		super.onResume();
                BreadCrumbs.initBreadCrumbs(this);
		LOGGER.debug("onResume - activity {}", this.getComponentName());
	}

}
