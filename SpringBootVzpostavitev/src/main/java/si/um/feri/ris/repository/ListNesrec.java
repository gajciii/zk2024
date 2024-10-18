package si.um.feri.ris.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import si.um.feri.ris.models.Nesreca;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public interface ListNesrec extends CrudRepository<Nesreca, Long> {


	@Query("select n from Nesreca n ")
	List<Nesreca> findNesrecaById(long id);

	@Query("SELECT new si.um.feri.ris.models.Nesreca(n.datum, n.opis, n.lokacija) " +
			"FROM Nesreca n " +
			"WHERE n.datum BETWEEN :startDate AND :endDate " +
			"AND n.lokacija = :location " +
			"AND n.opis LIKE CONCAT('%', :descriptionKeyword, '%')")
	List<Nesreca> findNesreceByDatumKrajBeseda(@Param("startDate") Date fromDate,
											   @Param("endDate") Date toDate,
											   @Param("location") String location,
											   @Param("descriptionKeyword") String descriptionKeyword);

	@Query("SELECT n FROM Nesreca n WHERE SIZE(n.oskodovanci) >= 3")
	List<Nesreca> findNesrecaPoskodovaniTriAliVec();
}
