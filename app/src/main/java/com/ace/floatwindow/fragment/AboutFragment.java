package com.ace.floatwindow.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ace.floatwindow.R;

/**
 * Created by JunBin on 2015/8/19.
 */
public class AboutFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_about, container, false);
        initViews();
        return mContentView;
    }

    private void initViews() {
        String version = "";
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            version = getString(R.string.about_version, info.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.about_tv_version)).setText(version);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.main_about);
    }
}
