package DAO;

import Model.ModelGroupCreator;
import Utilities.MongoDbManagerClass;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.naming.NamingException;
import java.time.LocalDate;
import java.util.ArrayList;

public class DaoModelGroupCreator {

    private final ModelGroupCreator groupCreator;

    public DaoModelGroupCreator(ModelGroupCreator groupCreator) {
        this.groupCreator = groupCreator;
    }

    @SuppressWarnings(value = "unchecked")
    public Boolean createGroup() throws NamingException {
        MongoCollection mongoCollection = MongoDbManagerClass.getCachedGroupsCollection();
        if(!MongoDbManagerClass.doesGroupExist(this.groupCreator.getGroupUid())){
            ArrayList<Document> arrayList = new ArrayList<>(1);
            arrayList.add(new Document("memberID",this.groupCreator.getCreatorUid())
                    .append("addedDate",LocalDate.now())
                    .append("Status",Boolean.TRUE));
            Bson insertQuery = new org.bson.Document("groupName",this.groupCreator.getGroupName())
                    .append("groupID",this.groupCreator.getGroupUid())
                    .append("status",this.groupCreator.getStatus())
                    .append("admin",this.groupCreator.getCreatorUid())
                    .append("createdDate",this.groupCreator.getCreatedDate())
                    .append("Members",arrayList);
            mongoCollection.insertOne(insertQuery);
            return true;
        }
        return false;
    }
}