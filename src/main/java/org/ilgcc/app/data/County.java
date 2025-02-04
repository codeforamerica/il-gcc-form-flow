package org.ilgcc.app.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "zip_codes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class County implements Serializable {

    @Id
    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "city")
    private String city;

    @Column(name = "county")
    private String county;

    @Column(name = "fips_county_code")
    private Integer fipsCountyCode;

    @Column(name = "dpa_count_code")
    private Integer dpaCountCode;

    @Column(name = "caseload_code")
    private String caseloadCode;

}
