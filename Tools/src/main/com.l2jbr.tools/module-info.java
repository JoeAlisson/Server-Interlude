module com.l2jbr.tools {
    requires java.sql;
    // TODO remove gameserver and authserver dependency
    requires com.l2jbr.gameserver;
    requires org.l2j.authserver;
    requires com.l2jbr.commons;

    requires spring.data.commons;
}