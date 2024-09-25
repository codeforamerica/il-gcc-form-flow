package org.ilgcc.app.db;

import static javax.persistence.TemporalType.TIMESTAMP;

import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.ilgcc.app.utils.enums.status;
import org.ilgcc.app.utils.enums.type;

@Entity
@Table(name = "transmissions")
@Setter
@Getter
public class Transmission {

    @Id
    @GeneratedValue
    private UUID transmissionId;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submissionId;

    @ManyToOne
    @JoinColumn(name = "user_file_id")
    private UserFile userFileId;

    @Temporal(TIMESTAMP)
    @Column(name = "time_sent")
    private Date timeSent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private type type;

    @Type(JsonType.class)
    @Column(name = "errors", columnDefinition = "json")
    private Map<Integer, String> errors;
    
    @Column(name = "attempts")
    private int attempts;

    public Transmission(Submission submissionId, UserFile userFileId, Date timeSent, status status,
            type type, Map<Integer, String> errors) {
        this.submissionId = submissionId;
        this.userFileId = userFileId;
        this.timeSent = timeSent;
        this.status = status;
        this.type = type;
        this.errors = errors;
    }

    public Transmission() {
        
    }
}
