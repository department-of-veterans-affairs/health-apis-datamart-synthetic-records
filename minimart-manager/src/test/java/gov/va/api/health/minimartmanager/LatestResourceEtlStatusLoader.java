package gov.va.api.health.minimartmanager;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

@RequiredArgsConstructor(staticName = "create")
public class LatestResourceEtlStatusLoader {

    private final ThreadLocal<EntityManager> LOCAL_ENTITY_MANAGER = new ThreadLocal<>();

    private boolean checkExists(String resource, EntityManager entityManager){
        Query nativeQuery = entityManager.createNativeQuery("SELECT r FROM [app].[Latest_Resource_ETL_Status] WHERE r.ResourceName = ?")
                .setParameter(1, resource);
        if (nativeQuery.getResultList().size() > 0){
            return true;
        }
        return false;
    }

    public void insertIntoEtlTable(String resource, EntityManager entityManager) throws IOException{
        try (FileInputStream inputStream = new FileInputStream("sqlserver.properties")) {
//            var properties = new Properties(System.getProperties());
//            properties.load(inputStream);
//            Class.forName(properties.getProperty("spring.datasource.driver-class-name"));
//            var url = properties.getProperty("spring.datasource.url");
//            var username = properties.getProperty("spring.datasource.username");
//            var password = properties.getProperty("spring.datasource.password");
            boolean exists = checkExists(resource, entityManager);
            Instant.now();
            if(exists){

            }



        } catch (IOException e){
            throw e;
        }
    }

}
