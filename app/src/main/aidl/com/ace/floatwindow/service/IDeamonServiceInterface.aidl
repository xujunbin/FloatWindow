// DeamonServiceInterface.aidl
package com.ace.floatwindow.service;

// Declare any non-default types here with import statements

interface IDeamonServiceInterface {
    void startTrafficFloatWindow();
    void stopTrafficFloatWindow();
    void onViewTypeChanged(boolean overlayStatusBar);
    void onConfigurationChanged();
}
