package fr.ifremer.nemo.ws.common;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SQLUtil {

    public static <T> T doWithConnection(DataSource dataSource, Function<Connection, T> fn) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                return fn.apply(connection);
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> queryForList(DataSource dataSource, String sql, RowMapper<T> rowMapper, Object... params) {
        return doWithConnection(dataSource, (connection) -> {
            try {
                PreparedStatement pStmt = connection.prepareStatement(sql);
                int i = 1;
                for (Object param : params) {
                    pStmt.setObject(i++, param);
                }
                List<T> resultList = new ArrayList<>();
                ResultSet rs = pStmt.executeQuery();
                while (rs.next()) {
                    resultList.add(rowMapper.mapRow(rs));
                }
                rs.close();
                pStmt.close();
                return resultList;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String queryForString(DataSource dataSource, String sql, Object... params) {
        List<String> list = queryForList(dataSource, sql, (rs) -> rs.getString(1), params);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
