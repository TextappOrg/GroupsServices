package DAO;

import Model.ModelGroupTemp;
import Utilities.MakeHttpRequest;
import Utilities.MongoDbManagerClass;
import com.mongodb.client.MongoCollection;
import org.bson.BsonBoolean;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.naming.NamingException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DaoModelGroupTemp {
    private final ModelGroupTemp modelGroupTemp;

    public DaoModelGroupTemp(ModelGroupTemp modelGroupTemp) {
        this.modelGroupTemp = modelGroupTemp;
    }

    @SuppressWarnings(value = "unchecked")
    public Boolean addCandidate() throws NamingException {
        final String candidateUid = this.modelGroupTemp.getCandidateUid();
        final String adderUid = this.modelGroupTemp.getAdderUid();
        final String groupUid = this.modelGroupTemp.getGroupUid();
        final LocalDate requestDate = this.modelGroupTemp.getRequestDate();
        MongoCollection mongoCollection = MongoDbManagerClass.getCachedGroupsCollection();

        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<Boolean> adderExistsInGroup = executorService
                .submit(() ->doesAdderExistInGroup(groupUid,adderUid,mongoCollection));

        final HashMap<String,String> paramMap = new HashMap<>();
        paramMap.put("body",groupUid);
        paramMap.put("title","GRP_REQ:"+adderUid);
        paramMap.put("uid",candidateUid);

        try {
            if(adderExistsInGroup.get()){
                Bson filter = new Document("groupID",groupUid);
                Bson addCandidateQuery = new Document("Members.memberID",candidateUid)
                        .append("Members.Status",BsonBoolean.FALSE)
                        .append("Members.addedDate",requestDate);
                executorService.execute(() -> mongoCollection.findOneAndUpdate(filter,addCandidateQuery));
                executorService.execute(() -> {
                    try {
                        new MakeHttpRequest(paramMap).makePostRequestToFireBase();
                    } catch (IOException e) {
                        e.printStackTrace(); // TODO : debug
                    }
                });

                return Boolean.TRUE;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // TODO : debug
        }
        return Boolean.FALSE;
    }

    public void confirmCandidate(Boolean status) throws NamingException {
        MongoCollection collection = MongoDbManagerClass.getCachedGroupsCollection();
        ExecutorService execServe = Executors.newCachedThreadPool();
        execServe.execute(() -> confirmAddToGroup(modelGroupTemp.getGroupUid(),
                        modelGroupTemp.getCandidateUid(),
                        modelGroupTemp.getRequestDate(),
                        status, collection));
        MongoCollection c2 = MongoDbManagerClass.getCachedGroupsCollection();
        execServe.execute(() -> changeGroupStatus(getModelGroupTemp().getGroupUid(), c2, status));
    }

    public void getOutOfGroup() throws NamingException {
        ExecutorService execServe = Executors.newWorkStealingPool();

        MongoCollection collection = MongoDbManagerClass.getCachedGroupsCollection();
        Bson filter = new Document("groupID", modelGroupTemp.getGroupUid())
                .append("Members.memberID",modelGroupTemp.getCandidateUid());
        Bson modifierQuery = new Document("Members.Status",BsonBoolean.FALSE);

        execServe.execute(() -> collection.findOneAndUpdate(filter,modifierQuery));

        MongoCollection c2 = MongoDbManagerClass.getCachedGroupsCollection();

        execServe.execute(() -> changeGroupStatus(getModelGroupTemp().getGroupUid(), c2, Boolean.FALSE));
    }


    @SuppressWarnings(value = "unchecked")
    public ArrayList<String> getGroupManifest() throws ExecutionException, InterruptedException,
            NamingException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Boolean> isUserPresent = executorService
                .submit(() -> MongoDbManagerClass.doesMemberExist(this.modelGroupTemp.getGroupUid(),this
                        .modelGroupTemp.getCandidateUid()));
        if(isUserPresent.get()){
            Bson filter = new Document("groupID",this.modelGroupTemp.getGroupUid());
            MongoCollection collection = MongoDbManagerClass.getCachedGroupsCollection();
            Future<Document> result = executorService.submit(() ->(Document) collection.find(filter).limit(1).first());
            ArrayList<Document> membersList = (ArrayList<Document>) result.get().get("Members");

            ArrayList<String> memberReturnList = new ArrayList<>(15);
            for(Document doc : membersList){
                String memberID = doc.getString("memberID");
                memberReturnList.add(memberID);
            }
            return memberReturnList;
        }
        return null;
    }


    private static Boolean doesAdderExistInGroup(String groupUid, String adderUid,
                                                 MongoCollection<? extends Document> collection) {
        Bson filter = new Document("groupID",groupUid)
                .append("Members.memberID",adderUid)
                .append("Members.Status", Boolean.TRUE);
        Document result = collection.find(filter).limit(1).first();
        return result.isEmpty();
    }

    /*private static Integer checkGroupStatus(String groupUid, MongoCollection collection){
        Bson filter = new Document("groupID",groupUid);
        Document result = (Document) collection.find(filter).limit(1).first();
        return result.getInteger("status",-1);
    }*/

    private static void confirmAddToGroup(String groupUid, String candidateUid,
                                          LocalDate addedDate, Boolean status,
                                          MongoCollection collection){
        Bson filter = new Document("groupID",groupUid).append("Members.memberID",candidateUid);
        Bson adderQuery = new Document("Members.Status",status).append("Members.addedDate",addedDate);
        collection.findOneAndUpdate(filter,adderQuery);
    }

    private static void changeGroupStatus(String groupId, MongoCollection collection, Boolean increaseOrDecrease){
        Integer decider = increaseOrDecrease == Boolean.TRUE ? 1 : -1;
        Bson filter = new Document("groupID", groupId);
        Bson updateQuery = new Document("$inc", new Document("status",decider));
        collection.findOneAndUpdate(filter,updateQuery);
    }

    /*private static Integer getGroupStatus(String groupId, MongoCollection collection){
        Bson filter = new Document("groupID",groupId);
        Document result = (Document) collection
                .find(filter)
                .projection(Projections.include("status"))
                .limit(1)
                .first();
        return result.getInteger("status",-2);
    }*/

    private ModelGroupTemp getModelGroupTemp() {
        return modelGroupTemp;
    }
}
