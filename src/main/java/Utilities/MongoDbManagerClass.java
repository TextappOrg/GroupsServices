package Utilities;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import org.bson.conversions.Bson;
import org.bson.Document;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;

public class MongoDbManagerClass {

    public static MongoCollection getCachedGroupsCollection() throws NamingException {
        return getMongoDoc("RestServiceGroupMongoDbConnDetails","Groups_Cached_Collection_Name",
                "Groups_Database_Name","Friends_Connection_String");
    }

    private static MongoCollection getMongoDoc(String propertiesName, String collectionName, String dbName,
                                               String connectionString) throws NamingException {
        InitialContext context = new InitialContext();
        Properties properties = (Properties) context.lookup(propertiesName);
        String connection_string = properties.getProperty(connectionString);
        String collection_name = properties.getProperty(collectionName);
        String db_name = properties.getProperty(dbName);
        return new MongoClient(new MongoClientURI(connection_string))
                .getDatabase(db_name)
                .getCollection(collection_name);
    }
    
    @SuppressWarnings(value = "unchecked")
    public static Boolean doesGroupExist(String groupUid) throws NamingException {
        Bson filter = new Document("groupID",groupUid);
        MongoCollection collection = MongoDbManagerClass.getCachedGroupsCollection();
        return ((Document) collection.find(filter).limit(1).first()).isEmpty();
    }

    public static Boolean doesMemberExist(String groupUid, String memberUid) throws NamingException {
        Bson filter = new Document("groupID",groupUid)
                .append("Members.memberID",memberUid)
                .append("Member.Status",Boolean.TRUE);
        MongoCollection collection = getCachedGroupsCollection();
        return ((Document) collection.find(filter).limit(1).first()).isEmpty();
    }
}