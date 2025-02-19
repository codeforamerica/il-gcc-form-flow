package org.ilgcc.app.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigInteger;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "resource_organizations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class ResourceOrganization {
    @Id
    @Column(name = "resource_org_id")
    private BigInteger resourceOrgId;

    @Column(name = "caseload_code")
    private String caseloadCode;

    @Column(name = "name")
    private String name;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "sda")
    private Short sda;

    @OneToMany(mappedBy = "resourceOrganization", fetch =FetchType.LAZY)
    private Set<Provider> providers;

    @Override
    public String toString() {
        return "ResourceOrganization{" +
                "resourceOrgId=" + resourceOrgId +
                ", caseloadCode='" + caseloadCode + '\'' +
                ", name='" + name + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", sda=" + sda +
                '}';
    }
}
