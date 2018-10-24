module org.l2j.gameserver {
    requires org.l2j.commons;
    requires org.l2j.mmocore;

    requires java.sql;
    requires java.desktop;
    requires org.slf4j;
    requires java.scripting;
    requires jython.standalone;
    requires spring.data.commons;
    requires spring.data.jdbc;
    requires spring.context;
    requires java.xml.bind;

    exports org.l2j.gameserver;
    exports org.l2j.gameserver.model.entity.database.repository;
    exports org.l2j.gameserver.model.entity.database;
    opens org.l2j.gameserver.model.entity.xml to java.xml.bind;
    opens org.l2j.gameserver.templates.xml.jaxb to java.xml.bind;
}