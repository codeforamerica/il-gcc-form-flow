package org.ilgcc.app.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.stereotype.Component;


@Entity
@Table(name = "providers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class Provider implements Serializable {

    @Id
    @Column(name = "provider_id")
    private BigInteger providerId;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "dba_name")
    private String dbaName;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "status")
    private String status;

    @Column(name = "date_of_last_approval")
    private OffsetDateTime dateOfLastApproval;

    @JoinColumn(name = "resource_org_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private ResourceOrganization resourceOrganization;

    @Override
    public String toString() {
        return "Provider{" +
                "providerId=" + providerId +
                ", type='" + type + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", status='" + status + '\'' +
                ", dateOfLastApproval=" + dateOfLastApproval +
                ", resourceOrganization=" + resourceOrganization +
                '}';
    }
}
