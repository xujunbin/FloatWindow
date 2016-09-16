package com.ace.floatwindow.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JunBin on 2015/8/6.
 */
public class EncryptPreference {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public static EncryptPreference getInstance(final Context context, final String name) {
        return new EncryptPreference(context, name);
    }

    private EncryptPreference(final Context context, final String name) {
        mPreferences = context.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
        mEditor = mPreferences.edit();
    }

    public EncryptPreference putString(final String key, final String value) {
        mEditor.putString(encrypt(key), encrypt(value));
        return this;
    }

    public String getString(final String key, final String defValue) {
        final String encrypted = mPreferences.getString(encrypt(key), null);
        if (encrypted != null) {
            return decrypt(encrypted);
        }
        return defValue;
    }

    public EncryptPreference putInt(final String key, final int value) {
        mEditor.putString(encrypt(key), encrypt(value));
        return this;
    }

    public int getInt(final String key, final int defValue) {
        final String encrypted = mPreferences.getString(encrypt(key), null);
        if (encrypted != null) {
            try {
                return Integer.parseInt(decrypt(encrypted));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defValue;
    }

    public EncryptPreference putLong(final String key, final long value) {
        mEditor.putString(encrypt(key), encrypt(value));
        return this;
    }

    public long getLong(final String key, final long defValue) {
        final String encrypted = mPreferences.getString(encrypt(key), null);
        if (encrypted != null) {
            try {
                return Long.parseLong(decrypt(encrypted));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defValue;
    }

    public EncryptPreference putFloat(final String key, final float value) {
        mEditor.putString(encrypt(key), encrypt(value));
        return this;
    }

    public float getFloat(final String key, final float defValue) {
        final String encrypted = mPreferences.getString(encrypt(key), null);
        if (encrypted != null) {
            try {
                return Float.parseFloat(decrypt(encrypted));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return defValue;
    }

    public EncryptPreference putBoolean(final String key, final boolean value) {
        mEditor.putString(encrypt(key), encrypt(value));
        return this;
    }

    public boolean getBoolean(final String key, final boolean defValue) {
        final String encrypted = mPreferences.getString(encrypt(key), null);
        if (encrypted != null) {
            try {
                return Boolean.parseBoolean(decrypt(encrypted));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defValue;
    }

    public boolean contains(final String key) {
        return mPreferences.contains(encrypt(key));
    }

    public EncryptPreference remove(final String key) {
        mEditor.remove(encrypt(key));
        return this;
    }

    public EncryptPreference clear() {
        mEditor.clear();
        return this;
    }

    public boolean commit() {
        return mEditor.commit();
    }

    private String encrypt(final Object key) {
        if (key == null) {
            return null;
        }

        return String.valueOf(key);
//        return EncryptUtils.encrypt(String.valueOf(key));
    }

    private String decrypt(final String key) {
        if (key == null) {
            return null;
        }

        return key;
//        return EncryptUtils.decrypt(key);
    }
}