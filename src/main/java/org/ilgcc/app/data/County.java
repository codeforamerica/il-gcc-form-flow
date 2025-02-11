package org.ilgcc.app.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "zip_codes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class County implements Serializable {

    @Id
    @Column(name = "zip_code")
    private BigInteger zipCode;

    @Column(name = "city")
    private String city;

    @Column(name = "county")
    private String county;

    @Column(name = "fips_county_code")
    private Integer fipsCountyCode;

    @Column(name = "dpa_county_code")
    private Integer dpaCountyCode;

    @Column(name = "caseload_code")
    private String caseloadCode;

}
