package Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement
public class ModelGroupTemp {
    @XmlElement private final String adderUid;
    @XmlElement private final String candidateUid;
    @XmlElement private final String groupUid;
    @XmlElement private final LocalDate requestDate;

    public ModelGroupTemp(String adderUid, String candidateUid, String groupUid, LocalDate requestDate) {
        this.adderUid = adderUid;
        this.candidateUid = candidateUid;
        this.groupUid = groupUid;
        this.requestDate = requestDate;
    }

    public ModelGroupTemp(String candidateUid, String groupUid, LocalDate requestDate) {
        this.adderUid = "";
        this.candidateUid = candidateUid;
        this.groupUid = groupUid;
        this.requestDate = requestDate;
    }

    public ModelGroupTemp(String candidateUid, String groupUid) {
        this.candidateUid = candidateUid;
        this.groupUid = groupUid;
        this.adderUid = "";
        this.requestDate = LocalDate.now();
    }

    public String getAdderUid() {
        return adderUid;
    }

    public String getCandidateUid() {
        return candidateUid;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    @Override
    public String toString() {
        return  adderUid + " "  + candidateUid + " "  + groupUid + " "  + requestDate.toString() + " " ;
    }
}
