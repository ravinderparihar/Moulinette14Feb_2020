package com.moulinette.utilities;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import com.moulinette.R;
import com.moulinette.activities.History;
import com.moulinette.activities.QuizSection;
import com.moulinette.activities.RaffleDrawSection;
import com.moulinette.activities.Settings;
import com.moulinette.activities.VoteSection;
import com.moulinette.fragments.Home;
import com.moulinette.activities.Profile;
import com.moulinette.adapters.LefeMenuAdapter;
import com.moulinette.models.leftmenu.DataModel_;

import org.json.JSONException;

import java.util.ArrayList;

import static com.moulinette.utilities.ConstandFunctions.replaceFragment;


public class LeftMenu {

    FragmentActivity ac;
    ArrayList<DataModel_> dataModels=new ArrayList<>();
    public  static DrawerLayout drawer;
    TinyDB tinyDb;


   public void init(FragmentActivity activity, final DrawerLayout drawerLayout, ListView mDrawerList, ImageView menu_nav){
        ac=activity;

       tinyDb = new TinyDB(ac);
       drawer=drawerLayout;

       DataModel_ data=new DataModel_();
       data.setName(ac.getString(R.string.home));
       data.setImage(R.drawable.ic_home);
       dataModels.add(data);

       data=new DataModel_();
       data.setName(ac.getString(R.string.profile));
       data.setImage(R.drawable.ic_user);
       dataModels.add(data);

       data=new DataModel_();
       data.setName(ac.getString(R.string.my_refrels));
       data.setImage(R.drawable.ic_gift);
       dataModels.add(data);

       data=new DataModel_();
       data.setName(ac.getString(R.string.my_quizs));
       data.setImage(R.drawable.ic_exam);
       dataModels.add(data);

       data=new DataModel_();
       data.setName(ac.getString(R.string.my_votes));
       data.setImage(R.drawable.ic_vote);
       dataModels.add(data);

       data=new DataModel_();
       data.setName(ac.getString(R.string.history));
       data.setImage(R.drawable.ic_history);
       dataModels.add(data);

       data=new DataModel_();
       data.setName(ac.getString(R.string.settings));
       data.setImage(R.drawable.ic_settings);
       dataModels.add(data);

       data=new DataModel_();
       data.setName(ac.getString(R.string.logout));
       data.setImage(R.drawable.ic_log_out);
       dataModels.add(data);

        LefeMenuAdapter adapter = new LefeMenuAdapter(activity, R.layout.menu_item_row, dataModels);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new Home_ActivityDrawerItemClickListener());
//      drawerLayout.setDrawerListener(menu_nav);

       menu_nav.setOnClickListener(new View.OnClickListener() {
           @Override
                   public void onClick(View view) {
               if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                   drawerLayout.closeDrawer(Gravity.LEFT);
               }else{
                   drawerLayout.openDrawer(Gravity.LEFT);
               }
           }
       });
    }

    private class Home_ActivityDrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    private void selectItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new Home();
                replaceFragment(ac, fragment);
                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case 1:
                Intent intent = new Intent(ac, Profile.class);
                ac.startActivity(intent);
                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                drawer.closeDrawer(Gravity.LEFT);
                break;

            case 2:
                intent = new Intent(ac, RaffleDrawSection.class);
                ac.startActivity(intent);
                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case 3:
                intent = new Intent(ac, QuizSection.class);
                ac.startActivity(intent);
                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case 4:
                intent = new Intent(ac, VoteSection.class);
                ac.startActivity(intent);
                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                drawer.closeDrawer(Gravity.LEFT);
                break;
            case 5:
                intent = new Intent(ac, History.class);
                ac.startActivity(intent);
                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                drawer.closeDrawer(Gravity.LEFT);
                break;

                case 6:
                intent = new Intent(ac, Settings.class);
                ac.startActivity(intent);
                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                drawer.closeDrawer(Gravity.LEFT);
                break;
                case 7:
                    try {
                        LogoutPopup.logout_Popup(ac);
                    } catch (JSONException e) {}
                    drawer.closeDrawer(Gravity.LEFT);
                break;

            default:
                break;

        }
    }

}
