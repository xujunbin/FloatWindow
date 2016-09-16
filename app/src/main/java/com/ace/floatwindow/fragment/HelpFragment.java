package com.ace.floatwindow.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ace.floatwindow.R;

/**
 * Created by JunBin on 2015/8/18.
 */
public class HelpFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_can_not_show, container, false);
        initViews();
        return mContentView;
    }

    private void initViews() {
        final String newLine = "\n";
        final StringBuilder builder = new StringBuilder();

        builder.append(getString(R.string.can_not_show_xiaomi_1));
        builder.append(newLine);
        builder.append(getString(R.string.can_not_show_xiaomi_2));
        builder.append(newLine);
        builder.append(getString(R.string.can_not_show_xiaomi_3));
        builder.append(newLine);
        builder.append(getString(R.string.can_not_show_xiaomi_4));
        builder.append(newLine);
        builder.append(getString(R.string.can_not_show_xiaomi_5));
        builder.append(newLine);
        builder.append(getString(R.string.can_not_show_xiaomi_6));
        builder.append(newLine);
        builder.append(getString(R.string.can_not_show_xiaomi_7));
        final TextView xiaomi = (TextView)findViewById(R.id.can_not_show_tv_xiaomi);
        xiaomi.setText(builder.toString());

        builder.setLength(0);
        builder.append(getString(R.string.can_not_show_huawei_1));
        builder.append(newLine);
        builder.append(getString(R.string.can_not_show_huawei_2));
        builder.append(newLine);
        builder.append(getString(R.string.can_not_show_huawei_3));
        builder.append(newLine);
        builder.append(getString(R.string.can_not_show_huawei_4));
        final TextView huawei = (TextView)findViewById(R.id.can_not_show_tv_huawei);
        huawei.setText(builder.toString());
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.main_can_not_show);
    }
}
