package gov.va.api.health.minimartmanager;

import java.io.FileInputStream;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

public class LatestResourceETLStatusLoader {
  final String sqlDriverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

  String resource;

  public LatestResourceETLStatusLoader(String resource) {
    this.resource = resource;
  }

  public void insertIntoETLTable() {
    try {
      Class.forName(sqlDriverClass);
      var properties = new Properties(System.getProperties());
      properties.load(new FileInputStream("sqlserver.properties"));
      var url = properties.getProperty("spring.datasource.url");
      var username = properties.getProperty("spring.datasource.username");
      var password = properties.getProperty("spring.datasource.password");
      var conn = DriverManager.getConnection(url, username, password);
      // Inserts value if nonexistent, updates it otherwise
      var sql =
          "IF EXISTS (SELECT * FROM [dq].[app].[Latest_Resource_ETL_Status] WHERE ResourceName = ?)\n"
              + "BEGIN\n"
              + "UPDATE [dq].[app].[Latest_Resource_ETL_Status] SET EndDateTimeUTC = ? WHERE ResourceName = ?\n"
              + "END\n"
              + "ELSE\n"
              + "INSERT INTO [dq].[app].[Latest_Resource_ETL_Status] VALUES (?, ?)";
      var statement = conn.prepareStatement(sql);
      Clock cl = Clock.systemUTC();
      Instant now = Instant.now(cl);
      Date dateNow = Date.from(now);
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String smalldatetime = formatter.format(dateNow);
      statement.setString(1, resource);
      statement.setString(2, smalldatetime);
      statement.setString(3, resource);
      statement.setString(4, resource);
      statement.setString(5, smalldatetime);
      var result = statement.executeUpdate();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }
}
