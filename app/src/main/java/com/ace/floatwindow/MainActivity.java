package com.ace.floatwindow;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ace.floatwindow.data.EncryptPreference;
import com.ace.floatwindow.data.Preference;
import com.ace.floatwindow.fragment.AboutFragment;
import com.ace.floatwindow.fragment.BaseFragment;
import com.ace.floatwindow.fragment.HelpFragment;
import com.ace.floatwindow.fragment.FloatWindowFragment;
import com.ace.floatwindow.service.DeamonService;
import com.ace.floatwindow.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.fragment.FeedbackFragment;
import com.umeng.fb.model.Conversation;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static String DRAWER_FLOAT_WINDOW = "float_window";
    private final static String DRAWER_CAN_NOT_SHOW = "can_not_show";
    private final static String DRAWER_CONTACT_US = "contact_us";
    private final static String DRAWER_ABOUT = "about";

    private final static int Exit_App = 0;

    private Handler mHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<String> mDrawerItemList;
    private String mClickedDrawerItem = null;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawerListItems();
        initViews();

        startService(new Intent(getApplicationContext(), DeamonService.class));

        UmengUpdateAgent.setUpdateCheckConfig(BuildConfig.DEBUG);
        UmengUpdateAgent.setRichNotification(true);
        UmengUpdateAgent.update(getApplicationContext());
    }

    private void initDrawerListItems() {
        mDrawerItemList = new ArrayList<>();
        mDrawerItemList.add(DRAWER_FLOAT_WINDOW);
        mDrawerItemList.add(DRAWER_CAN_NOT_SHOW);
        mDrawerItemList.add(DRAWER_CONTACT_US);
        mDrawerItemList.add(DRAWER_ABOUT);
    }

    private void initViews() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_dl_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();

                onDrawItemClicked(mClickedDrawerItem);
                mClickedDrawerItem = null;
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        final DrawerListAdapter mDrawerListAdapter = new DrawerListAdapter();
        final ListView drawerListView = (ListView)findViewById(R.id.drawer_lv_list);
        drawerListView.setAdapter(mDrawerListAdapter);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickedDrawerItem = mDrawerListAdapter.getItem(position);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final BaseFragment fragment = new FloatWindowFragment();
        final FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fragment, fragment, DRAWER_FLOAT_WINDOW);
        ft.commit();
        mCurrentFragment = fragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (!FloatWindowFragment.class.isInstance(mCurrentFragment)) {
            onDrawItemClicked(DRAWER_FLOAT_WINDOW);
        } else if (mHandler.hasMessages(Exit_App)) {
            super.onBackPressed();
            System.exit(0);
        } else {
            mHandler.sendEmptyMessageDelayed(Exit_App, 2000);
            ToastUtils.showShortToast(getApplicationContext(), R.string.main_press_again_exit_app);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void onDrawItemClicked(final String tag) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        final FragmentTransaction ft = fragmentManager.beginTransaction();
        if (fragment == null) {
            if (DRAWER_FLOAT_WINDOW.equals(tag)) {
                fragment = new FloatWindowFragment();
            } else if (DRAWER_CAN_NOT_SHOW.equals(tag)) {
                fragment = new HelpFragment();;
            } else if (DRAWER_CONTACT_US.equals(tag)) {
                final Context context = getApplicationContext();
                final EncryptPreference preference = EncryptPreference.getInstance(context, Preference.DEFAULT_PREFERENCE_NAME);
                String conversationId = preference.getString(Preference.FEEDBACK_CONVERSATION_ID, null);
                if (TextUtils.isEmpty(conversationId)) {
                    final Conversation conversation = Conversation.newInstance(context);
                    conversationId = conversation.getId();
                    preference.putString(Preference.FEEDBACK_CONVERSATION_ID, conversationId).commit();
                }
                fragment = FeedbackFragment.newInstance(conversationId);
            } else if (DRAWER_ABOUT.equals(tag)) {
                fragment = new AboutFragment();
            }

            if (fragment != null) {
                ft.add(R.id.fragment, fragment, tag);
                ft.commit();

                mCurrentFragment = fragment;
                onFragmentChanged(tag);
            }
        } else {
            if (!fragment.equals(mCurrentFragment)) {
                ft.hide(mCurrentFragment);
            }
            ft.show(fragment);
            ft.commit();

            mCurrentFragment = fragment;
            onFragmentChanged(tag);
        }
    }

    private void onFragmentChanged(final String tag) {
        String title = null;
        if (DRAWER_FLOAT_WINDOW.equals(tag)) {
            title = getString(R.string.main_float_window);
        } else if (DRAWER_CAN_NOT_SHOW.equals(tag)) {
            title = getString(R.string.main_can_not_show);
        } else if (DRAWER_CONTACT_US.equals(tag)) {
            title = getString(R.string.main_contact_us);
        } else if (DRAWER_ABOUT.equals(tag)) {
            title = getString(R.string.main_about);
        }

        if (!TextUtils.isEmpty(title)) {
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    private class DrawerListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getCount() {
            return mDrawerItemList.size();
        }

        @Override
        public String getItem(int position) {
            return mDrawerItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.item_drawer_list, parent, false);
            initItem(convertView, getItem(position));
            return convertView;
        }

        private void initItem(final View root, final String tag) {
            final TextView textView = (TextView)root.findViewById(R.id.drawer_tv_content);
            if (DRAWER_FLOAT_WINDOW.equals(tag)) {
                textView.setText(R.string.main_float_window);
            } else if (DRAWER_CAN_NOT_SHOW.equals(tag)) {
                textView.setText(R.string.main_can_not_show);
            } else if (DRAWER_CONTACT_US.equals(tag)) {
                textView.setText(R.string.main_contact_us);
            } else if (DRAWER_ABOUT.equals(tag)) {
                textView.setText(R.string.main_about);
            }
        }
    }
}
