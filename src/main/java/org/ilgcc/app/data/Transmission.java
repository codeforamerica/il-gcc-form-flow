package org.ilgcc.app.data;

import static jakarta.persistence.TemporalType.TIMESTAMP;
import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.ilgcc.app.utils.enums.TransmissionStatus;
import org.ilgcc.app.utils.enums.TransmissionType;
import org.springframework.stereotype.Component;


@Entity
@Table(name = "transmissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
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
    private TransmissionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransmissionType type;

    @Type(JsonType.class)
    @Column(name = "errors", columnDefinition = "json")
    private Map<Integer, String> errors;
    
    @Column(name = "attempts")
    private int attempts = 1;

    public Transmission(Submission submissionId, UserFile userFileId, Date timeSent, TransmissionStatus status,
            TransmissionType type, Map<Integer, String> errors) {
        this.submissionId = submissionId;
        this.userFileId = userFileId;
        this.timeSent = timeSent;
        this.status = status;
        this.type = type;
        this.errors = errors;
    }
}
