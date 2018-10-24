module org.l2j.tools {
    requires java.sql;
    // TODO remove gameserver and authserver dependency
    requires org.l2j.gameserver;
    requires org.l2j.authserver;
    requires org.l2j.commons;

    requires spring.data.commons;
}