package si.um.feri.ris.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import si.um.feri.ris.models.Oskodovanec;

import java.util.List;

public interface PregledOskodovancev extends CrudRepository<Oskodovanec, Long> {


	@Query("select o from Oskodovanec o join o.nesrece n where n.id = :nesrecaId")
	List<Oskodovanec> findOskodovanciByNesrecaId(long nesrecaId);

	@Query("SELECT o FROM Oskodovanec o WHERE o.mocnejePoskodovan = true AND o.imaDruzino = false")
	List<Oskodovanec> findMocnejePoskodovanBrezDruzine();

		@Query("SELECT o FROM Oskodovanec o " +
				"WHERE (o.ime LIKE 'a%' OR o.ime LIKE 'b%' OR o.ime LIKE 'm%') " +
				"AND (o.priimek LIKE 'c%' OR o.priimek LIKE 'd%' OR o.priimek LIKE 'e%' OR o.priimek LIKE 'v%') " +
				"AND o.imaDruzino = true " +
				"AND o.mocnejePoskodovan = true")
		List<Oskodovanec> findOskodovanciByVse();

	@Query("SELECT DISTINCT o FROM Oskodovanec o JOIN o.donacije d GROUP BY o HAVING COUNT(d) >= 5")
	List<Oskodovanec> najdiOskodovanceKiSoPrejeliDonacijo();




}