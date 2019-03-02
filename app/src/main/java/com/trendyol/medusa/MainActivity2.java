package com.trendyol.medusa;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.trendyol.medusalib.navigator.MultipleStackNavigator;
import com.trendyol.medusalib.navigator.Navigator;
import com.trendyol.medusalib.navigator.NavigatorConfiguration;
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class MainActivity2 extends AppCompatActivity implements Navigator.NavigatorListener {

    private BottomNavigationView navigation;

    MultipleStackNavigator multipleStackNavigator;

    private Fragment firstTabFragment = FragmentGenerator.generateNewFragment();
    private Fragment secondTabFragment = FragmentGenerator.generateNewFragment();
    private Fragment thirdTabFragment = FragmentGenerator.generateNewFragment();

    private List<Fragment> rootFragmentList = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    multipleStackNavigator.switchTab(0);
                    return true;
                case R.id.navigation_dashboard:
                    multipleStackNavigator.switchTab(1);
                    return true;
                case R.id.navigation_notifications:
                    multipleStackNavigator.switchTab(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navigation);

        rootFragmentList.add(firstTabFragment);
        rootFragmentList.add(secondTabFragment);
        rootFragmentList.add(thirdTabFragment);

        multipleStackNavigator = new MultipleStackNavigator(
                getSupportFragmentManager(),
                R.id.fragmentContainer,
                rootFragmentList,
                this,
                new NavigatorConfiguration(1, true, NavigatorTransaction.SHOW_HIDE));

        final SwitchCompat restartRootFragmentCheckBox = findViewById(R.id.restartSwitch);
        findViewById(R.id.resetCurrentTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleStackNavigator.resetCurrentTab(restartRootFragmentCheckBox.isChecked());
            }
        });
        findViewById(R.id.resetXTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleStackNavigator.reset(1, restartRootFragmentCheckBox.isChecked());
            }
        });

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleStackNavigator.reset();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (multipleStackNavigator.canGoBack()) {
            multipleStackNavigator.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTabChanged(int tabIndex) {
        switch (tabIndex) {
            case 0:
                navigation.setSelectedItemId(R.id.navigation_home);
                break;
            case 1:
                navigation.setSelectedItemId(R.id.navigation_dashboard);
                break;
            case 2:
                navigation.setSelectedItemId(R.id.navigation_notifications);
                break;
        }
    }
}
