package org.manwhore.displayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.inject.Inject;
import org.manwhore.displayer.base.BaseActivity;
import org.manwhore.displayer.filter.Filter;
import org.manwhore.displayer.logging.BreadCrumbs;
import org.manwhore.displayer.util.Constants;
import org.manwhore.displayer.util.IPersonResolver;
import org.manwhore.displayer.util.PersonResolver;

/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 21.3.2011
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public class WelcomeActivity extends BaseActivity {

		@Inject
		private IPersonResolver personResolver;

    private Button btnConversations, btnCreateProfile, btnPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);


        btnConversations = (Button) findViewById(R.id.welcomeBtnConversations);
        btnPreferences = (Button) findViewById(R.id.welcomeBtnPreferences);
        btnCreateProfile = (Button) findViewById(R.id.welcomeBtnRegister);

        btnPreferences.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                Intent settingsActivity = new Intent();
                settingsActivity.setClassName(Constants.PACKAGE, Constants.ACTIVITY_SETTINGS);                               
                startActivity(settingsActivity);
            }
        });

        btnConversations.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent intent = new Intent();
				intent.setClassName(Constants.PACKAGE, Constants.ACTIVITY_CONVERSATION_LIST);
				intent.putExtra(Constants.INTENT_KEY_FILTER, Filter.getDefaultFilter());
				startActivity(intent);
            }
        });

        btnCreateProfile.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent createProfileActivity = new Intent();
                createProfileActivity.setClassName(Constants.PACKAGE, Constants.ACTIVITY_CREATE_PROFILE);
                startActivity(createProfileActivity);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //persone resolver cache preloading disabled
        //((PersonResolver) personResolver).init();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String user = prefs.getString(Constants.SETTINGS_USER, "");
        String pass = prefs.getString(Constants.SETTINGS_PASS, "");

        if (user.equals("") || pass.equals("")) {            
            showNoProfile();
        }
        else {            
            showNormal();
        }

    }

    protected void showNoProfile()

    {
        btnConversations.setVisibility(View.GONE);

        TextView status = (TextView) findViewById(R.id.welcomeLabelProfileStatus);
        status.setText(R.string.ui_label_welcome_profile_undefined);

        findViewById(R.id.welcomeLabelProfileQuestion).setVisibility(View.VISIBLE);
        findViewById(R.id.welcomeLabelCreateProfile).setVisibility(View.VISIBLE);


        btnPreferences.setText(R.string.ui_btn_welcome_configure_here);
        btnCreateProfile.setVisibility(View.VISIBLE);


    }

    protected void showNormal()
    {
        btnCreateProfile.setVisibility(View.GONE);
        findViewById(R.id.welcomeLabelProfileQuestion).setVisibility(View.GONE);
        findViewById(R.id.welcomeLabelCreateProfile).setVisibility(View.GONE);

        TextView status = (TextView) findViewById(R.id.welcomeLabelProfileStatus);
        status.setText(R.string.ui_label_welcome_profile_set);

        btnConversations.setVisibility(View.VISIBLE);
        btnPreferences.setText(R.string.ui_btn_welcome_preferences);
    }

}