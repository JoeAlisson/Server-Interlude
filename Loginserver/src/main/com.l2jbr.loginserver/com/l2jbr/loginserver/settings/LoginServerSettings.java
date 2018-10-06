package com.l2jbr.loginserver.settings;

import com.l2jbr.commons.configuration.Settings;
import com.l2jbr.commons.configuration.SettingsFile;

import static com.l2jbr.commons.configuration.Configurator.getSettings;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNullElseGet;

public class LoginServerSettings implements Settings {

    private SettingsFile settings;
    private static LoginServerSettings instance;

    @Override
    public void load(SettingsFile settingsFile) {
        settings = requireNonNullElseGet(settingsFile, SettingsFile::new);
    }

    public static String gameServerListenHost() {
        return getInstance().settings.getString("LoginHostname", "*");
    }

    public static int gameServerListenPort() {
        return  getInstance().settings.getInteger("LoginPort", 9013);
    }

    public static boolean isFloodProtectionEnabled() {
        return getInstance().settings.getBoolean("EnableFloodProtection", true);
    }

    public static int floodFastConnectionLimit() {
        return getInstance().settings.getInteger("FastConnectionLimit", 15);
    }

    public static int floodNormalConnectionTime() {
        return getInstance().settings.getInteger("NormalConnectionTime", 700);
    }

    public static int floodFastConnectionTime() {
        return getInstance().settings.getInteger("FastConnectionTime", 350);
    }

    public static int maxConnectionPerIP() {
        return getInstance().settings.getInteger("MaxConnectionPerIP", 50);
    }

    public static boolean acceptNewGameServerEnabled() {
        return  getInstance().settings.getBoolean("AcceptNewGameServer", false);
    }

    private static LoginServerSettings getInstance() {
        if(isNull(instance)) {
            instance = getSettings(LoginServerSettings.class);
        }
        return instance;
    }
}
