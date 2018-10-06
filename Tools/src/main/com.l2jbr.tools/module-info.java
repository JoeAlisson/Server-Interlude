module com.l2jbr.tools {
    requires java.sql;
    // TODO remove gameserver and loginserver dependency
    requires com.l2jbr.gameserver;
    requires com.l2jbr.loginserver;
    requires com.l2jbr.commons;

    requires spring.data.commons;
}