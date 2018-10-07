package com.l2jbr.loginserver.status;

import com.l2jbr.commons.status.Status;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.l2jbr.commons.settings.TelnetSettings.telnetPassword;
import static com.l2jbr.commons.settings.TelnetSettings.telnetPort;

public class LoginStatus extends Status {

    private final List<LoginStatusThread> _loginStatus;

    public LoginStatus() throws IOException {
        super(telnetPort(), telnetPassword());
        _loginStatus = new ArrayList<>();
    }

    @Override
    protected void startStatusThread(Socket connection) throws IOException {
        LoginStatusThread lst = new LoginStatusThread(connection, _uptime);
        if (lst.isAlive()) {
            _loginStatus.add(lst);
        }
    }

    @Override
    public void sendMessageToTelnets(String msg) {
        List<LoginStatusThread> lsToRemove = new ArrayList<>();
        for (LoginStatusThread ls : _loginStatus) {
            if (ls.isInterrupted()) {
                lsToRemove.add(ls);
            } else {
                ls.printToTelnet(msg);
            }
        }
        lsToRemove.forEach(_loginStatus::remove);
    }
}
