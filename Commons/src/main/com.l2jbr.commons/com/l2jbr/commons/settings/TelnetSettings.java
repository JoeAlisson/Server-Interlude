package com.l2jbr.commons.settings;

import com.l2jbr.commons.configuration.Settings;
import com.l2jbr.commons.configuration.SettingsFile;

import java.util.List;

import static com.l2jbr.commons.configuration.Configurator.getSettings;

public class TelnetSettings implements Settings {

    private SettingsFile settings;

    @Override
    public void load(SettingsFile settingsFile) {
        settings = settingsFile;
    }

    public static boolean isTelnetEnabled() {
        return getInstance().settings.getBoolean("EnableTelnet", false);
    }

    public static int telnetPort() {
        return getInstance().settings.getInteger("StatusPort", 8929);
    }

    public static String telnetPassword() {
        return getInstance().settings.getString("StatusPW", "root");
    }

    public static List<String> telnetHostsAllowed() {
        return getInstance().settings.getStringList("ListOfHosts", "127.0.0.1,localhost", ",");
    }

    private static TelnetSettings getInstance() {
        return getSettings(TelnetSettings.class);
    }
}
