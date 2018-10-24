module org.l2j.commons {
    requires java.sql;
    requires java.naming;
    requires java.desktop;
    requires org.slf4j;
    requires com.zaxxer.hikari;
    requires spring.data.commons;
    requires spring.data.jdbc;
    requires spring.context;
    requires spring.jdbc;
    requires java.xml.bind;

    exports org.l2j.commons.util;
    exports org.l2j.commons.xml;
    exports org.l2j.commons.crypt;
    exports org.l2j.commons.status;
    exports org.l2j.commons.lib;
    exports org.l2j.commons;
    exports org.l2j.commons.database;
    exports org.l2j.commons.database.model;
    exports org.l2j.commons.database.annotation;
    exports org.l2j.commons.configuration;
    exports org.l2j.commons.settings;

}