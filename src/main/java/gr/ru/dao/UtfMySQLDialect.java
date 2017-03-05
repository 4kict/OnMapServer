package gr.ru.dao;

import org.hibernate.dialect.MySQLDialect;

public class UtfMySQLDialect extends MySQLDialect {
    @Override
    public String getTableTypeString() {
        return " DEFAULT CHARSET=utf8mb4";
    }
}
