/*
 * Copyright (C) 2021-2022 The PixelDust Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pixeldust.settings.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.util.pixeldust.PixeldustUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.pixeldust.support.preference.SecureSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class MiscExtensions extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String LOCATION_DEVICE_CONFIG = "location_indicators_enabled";
    private static final String LOCATION_INDICATOR = "enable_location_privacy_indicator";

    private SecureSettingSwitchPreference mLocationIndicator;
    private SwitchPreference mCombinedStatusbarSignalIcons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.misc_extensions);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();

        mLocationIndicator = (SecureSettingSwitchPreference) findPreference(LOCATION_INDICATOR);
        boolean locIndicator = DeviceConfig.getBoolean(DeviceConfig.NAMESPACE_PRIVACY,
                LOCATION_DEVICE_CONFIG, false);
        mLocationIndicator.setDefaultValue(locIndicator);
        mLocationIndicator.setChecked(Settings.Secure.getInt(resolver,
                LOCATION_INDICATOR, locIndicator ? 1 : 0) == 1);

        mCombinedStatusbarSignalIcons = (SwitchPreference) findPreference(
                Settings.System.COMBINED_STATUS_BAR_SIGNAL_ICONS);
        mCombinedStatusbarSignalIcons.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.PIXELDUST;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mCombinedStatusbarSignalIcons) {
            PixeldustUtils.showSystemUiRestartDialog(getContext());
            return true;
        }
        return false;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider() {
            @Override
            public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                    boolean enabled) {
                final ArrayList<SearchIndexableResource> result = new ArrayList<>();
                final SearchIndexableResource sir = new SearchIndexableResource(context);
                sir.xmlResId = R.xml.misc_extensions;
                result.add(sir);
                return result;
            }

            @Override
            public List<String> getNonIndexableKeys(Context context) {
                final List<String> keys = super.getNonIndexableKeys(context);
                return keys;
            }
    };
}
