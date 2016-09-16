package com.ace.floatwindow.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ace.floatwindow.R;
import com.ace.floatwindow.data.EncryptPreference;
import com.ace.floatwindow.data.Preference;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by JunBin on 2015/8/13.
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;
    protected Context mContext;
    protected View mContentView;

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    protected View findViewById(final int id) {
        if (mContentView != null) {
            return mContentView.findViewById(id);
        }

        return null;
    }

    protected EncryptPreference getPreference() {
        return EncryptPreference.getInstance(mContext, Preference.DEFAULT_PREFERENCE_NAME);
    }

    public void goNextFragment(final Fragment nextFragment) {
        FragmentTransaction ft = ((FragmentActivity)mActivity).getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.add(R.id.fragment, nextFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(nextFragment.getClass().getName());
        ft.commit();
    }

    public abstract String getFragmentTitle();
}
