package Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement
public class ModelGroupCreator {
    @XmlElement private final String creatorUid;
    @XmlElement private final String groupUid;
    @XmlElement private final String groupName;
    @XmlElement private final Integer status;
    @XmlElement private final LocalDate createdDate;

    public ModelGroupCreator(String creatorUid, String groupUid, String groupName, Integer status, LocalDate createdDate) {
        this.creatorUid = creatorUid;
        this.groupUid = groupUid;
        this.groupName = groupName;
        this.status = status;
        this.createdDate = createdDate;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public String getGroupName() {
        return groupName;
    }

    public Integer getStatus() {
        return status;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return  creatorUid + " " + groupUid + " " + groupName + " " + status + " " + createdDate.toString();
    }
}
