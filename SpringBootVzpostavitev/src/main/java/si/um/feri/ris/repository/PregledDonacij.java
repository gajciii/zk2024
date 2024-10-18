package si.um.feri.ris.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import si.um.feri.ris.models.Donacija;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import si.um.feri.ris.models.Uporabnik;

import java.util.List;

public interface PregledDonacij extends CrudRepository<Donacija, Long> {

    @Query("SELECT u FROM Uporabnik u JOIN u.donacije d WHERE d.id = :donacijaId")
    List<Uporabnik> findUporabnikiByDonacijaId(@Param("donacijaId") long donacijaId);


}
