package org.ilgcc.app.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "jobrunr_jobs")
@Getter
@Immutable
public class JobrunrJob implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "updatedat")
    private OffsetDateTime updatedAt;

    @Column(name = "recurringjobid")
    private String recurringJobId;

    @Column(name = "jobsignature")
    private String jobSignature;

    @Column(name = "state")
    private String state;
}
