package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountyRepository extends JpaRepository<County, String> {

    @Query(value = "SELECT c.zipCode, c.city, UPPER(c.county), c.caseloadCode from County c")

    Optional<County> findByZipCode(BigInteger truncatedZip);

    List<County> findByCounty(String countyName);
}