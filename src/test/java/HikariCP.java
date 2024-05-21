import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCP {
    private final HikariDataSource dataSource;

    public HikariCP() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mariadb://localhost:3306/test");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("8JKzDDUUzNMES4dNex3XXXe7FuDAroZ");
        this.dataSource = new HikariDataSource(hikariConfig);
        System.out.println("Created HikariCP");
    }

    public HikariDataSource dataSource() {
        return dataSource;
    }

    public void close() {
        System.out.println("Closing HikariCP");
        dataSource.close();
    }
}
