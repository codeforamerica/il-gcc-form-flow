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
import org.hibernate.annotations.Type;
import org.ilgcc.app.utils.enums.TransmissionStatus;
import org.ilgcc.app.utils.enums.TransmissionType;

@Entity
@Table(name = "transmissions")
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
    @Column(name = "transmission_status")
    private TransmissionStatus transmissionStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission_type")
    private TransmissionType transmissionType;

    @Type(JsonType.class)
    @Column(name = "transmission_errors", columnDefinition = "json")
    private Map<TransmissionType, String> transmissionErrors;
}
