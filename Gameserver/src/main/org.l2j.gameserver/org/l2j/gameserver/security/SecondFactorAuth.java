package org.l2j.gameserver.security;

import org.l2j.gameserver.model.entity.database.AccountInfo;
import org.l2j.gameserver.model.entity.database.repository.AccountInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getRepository;
import static org.l2j.commons.util.Util.hash;
import static org.l2j.commons.util.Util.isNotEmpty;

public class SecondFactorAuth {

    private static final Logger logger = LoggerFactory.getLogger(SecondFactorAuth.class);
    private static final Pattern pattern = Pattern.compile("\\d{6,8}");
    private boolean authed;
    private AccountInfo accountInfo;

    public SecondFactorAuth(String account) {
        initInfo(account);
    }

    private void initInfo(String account) {
        var optionalInfo = getRepository(AccountInfoRepository.class).findById(account);
        optionalInfo.ifPresent(accountInfo1 -> accountInfo = accountInfo1);
    }

    public boolean isAuthed() {
        return authed;
    }

    public boolean hasPassword() {
        return nonNull(accountInfo) && isNotEmpty(accountInfo.getPassword());
    }

    public boolean save(String account, String password) {
        if(validate(password)) {
            return false;
        }

        try {
            return createAccountInfo(account, password);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getLocalizedMessage(), e);
            return false;
        }
    }

    private boolean createAccountInfo(String account, String password) throws  NoSuchAlgorithmException {
        accountInfo = new AccountInfo(account, hash(password));
        getRepository(AccountInfoRepository.class).save(accountInfo);
        return accountInfo.isPersisted();
    }

    public boolean changePassword(String password, String newPassword) {
        try {
            if(validate(newPassword) || !accountInfo.getPassword().equals(hash(password))) {
                return false;
            }
            accountInfo.setPassword(hash(newPassword));
            getRepository(AccountInfoRepository.class).save(accountInfo);
            return true;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getLocalizedMessage(), e);
            return false;
        }
    }

    private boolean validate(String password) {
        return pattern.matcher(password).matches();
    }
}
