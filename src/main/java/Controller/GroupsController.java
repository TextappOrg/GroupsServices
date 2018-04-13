package Controller;

import DAO.DaoModelGroupCreator;
import DAO.DaoModelGroupTemp;
import Model.ModelGroupCreator;
import Model.ModelGroupTemp;
import Utilities.HandymanClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.naming.NamingException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@ApplicationPath("/Groups")
public class GroupsController {
    private DaoModelGroupTemp groupTemp;

    @POST
    @Path("/createGroup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createGroup(@FormParam("creatorUID") final String creatorUID, @FormParam("groupName") final String
            groupName, @FormParam("createdDate") final String createdDate){
        final String groupUUID = HandymanClass.makeUUID(creatorUID+groupName);
        final LocalDate dateOfCreation = LocalDate.parse(createdDate,DateTimeFormatter.ISO_DATE);
        ModelGroupCreator modelGroupCreator = new ModelGroupCreator(creatorUID,groupUUID,groupName,0,dateOfCreation);
        DaoModelGroupCreator groupCreator = new DaoModelGroupCreator(modelGroupCreator);
        try {
            return groupCreator.createGroup()
                    ? Response.ok().build()
                    : Response.noContent().entity("group already present").build();
        } catch (NamingException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/addCandidate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addCandidate(@FormParam("candidateUid") final String candidateUid,
                             @FormParam("adderUid") final String adderUid,
                             @FormParam("groupUid") final String groupUid,
                             @FormParam("addedDate") final String addedDate){
        final LocalDate dateOfAdd = LocalDate.parse(addedDate,DateTimeFormatter.ISO_DATE);
        ModelGroupTemp modelGroupTemp = new ModelGroupTemp(adderUid,candidateUid,groupUid,dateOfAdd);
        groupTemp = new DaoModelGroupTemp(modelGroupTemp);
        try {
            return groupTemp.addCandidate()
                    ? Response.ok("request sent").build()
                    : Response.noContent().entity("not sent").build();
        } catch (NamingException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/confirmCandidate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void confirmCandidate(@NotNull @FormParam("candidateUid") final String candidateUid,
                                 @NotNull @FormParam("GroupUid") final String groupUid,
                                 @NotNull @FormParam("addedDate") final String addedDate,
                                 @NotNull @FormParam("status") final String status){
        final Boolean stat = status.equalsIgnoreCase("Y") ? Boolean.TRUE : Boolean.FALSE;
        final LocalDate dateOfAdd = LocalDate.parse(addedDate,DateTimeFormatter.ISO_DATE);
        final ModelGroupTemp modelGroupTemp = new ModelGroupTemp(candidateUid,groupUid,dateOfAdd);
        groupTemp = new DaoModelGroupTemp(modelGroupTemp);
        try {
            groupTemp.confirmCandidate(stat);
        } catch (NamingException e) {
            e.printStackTrace(); // TODO : debug
        }
    }

    @POST
    @Path("/OUT")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void getOutOfGroup(@NotNull @FormParam("candidateUid") final String candidateUid,
                              @NotNull @FormParam("groupUid") final String groupUid){
        final ModelGroupTemp modelGroupTemp = new ModelGroupTemp(candidateUid,groupUid);
        groupTemp = new DaoModelGroupTemp(modelGroupTemp);
        try {
            groupTemp.getOutOfGroup();
        } catch (NamingException e) {
            e.printStackTrace(); // TODO : debug
        }
    }

    @POST
    @Path("/GetGroupManifest")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publishGroupManifest(@NotNull @FormParam("groupUid") final String groupUid,
                                         @NotNull @FormParam("candidateUid") final String candidateUid){
        final ModelGroupTemp modelGroupTemp = new ModelGroupTemp(candidateUid,groupUid);
        groupTemp = new DaoModelGroupTemp(modelGroupTemp);
        try {
            ArrayList<String> returnList = groupTemp.getGroupManifest();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(returnList);
            return Response.ok(json,MediaType.APPLICATION_JSON).build();
        } catch (ExecutionException | InterruptedException | NamingException | JsonProcessingException e) {
            e.printStackTrace(); // TODO : debug
            return Response.serverError().build();
        } catch (NullPointerException e){
            e.printStackTrace(); // TODO : debug
            return Response.noContent().build();
        }
    }
}
