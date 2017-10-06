package io.stringx.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import io.stringx.StringX;
import io.stringx.StringXProxyActivity;
import io.stringx.UnsupportedLanguageException;
import io.stringx.client.R;

public class StringXPreference extends android.preference.CheckBoxPreference {

    public StringXPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setKey(StringX.KEY_ENABLED);
        final StringX stringX = StringX.get(getContext());
        if (stringX != null) {
            setDefaultValue(stringX.isEnabled());
        }
        setIcon(R.mipmap.sx_logo);
        setTitle(R.string.sX_preference_title);
        setSummary(R.string.sX_preference_summary);
        try {
            setEnabled(isStringXAvailable(stringX));
        } catch (UnsupportedLanguageException e) {
            setEnabled(false);
        }
        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                if (stringX != null) {
                    Boolean isOptedIn = (Boolean) o;
                    setValue(isOptedIn);
                    stringX.invalidate();
                    stringX.setEnabled(isOptedIn);
                    if (isOptedIn) {
                        StringXProxyActivity.start(getContext());
                    } else {
                        stringX.restart();
                    }
                }
                return true;
            }
        });

    }

    private boolean isStringXAvailable(StringX stringX) throws UnsupportedLanguageException {
        return stringX != null && !stringX.getOptions().getSupportedLanguages().contains(StringX.getDeviceLanguage());
    }

    private void setValue(Boolean isOptedIn) {
        getEditor().putBoolean(getKey(), isOptedIn).apply();
    }

}