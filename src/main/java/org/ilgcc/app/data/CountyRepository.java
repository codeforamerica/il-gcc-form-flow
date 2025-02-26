package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountyRepository extends JpaRepository<County, String> {

    @Query("SELECT c FROM County c WHERE UPPER(c.county) = UPPER(:countyName)")
    List<County> findByCounty(@Param("countyName") String countyName);

    Optional<County> findByZipCode(BigInteger truncatedZip);


}