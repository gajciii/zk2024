package si.um.feri.ris.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import si.um.feri.ris.models.Oskodovanec;
import si.um.feri.ris.models.Uporabnik;

import java.util.List;
import java.util.Optional;

public interface UporabnikRepository extends JpaRepository<Uporabnik, Long> {
    List<Uporabnik> findByIme(String ime);

    @Query("SELECT u FROM Uporabnik u")
    Optional<Uporabnik> findUporabnikById(long id);

    @Query("SELECT u FROM Uporabnik u " +
            "JOIN u.donacije d " +
            "GROUP BY u.id " +
            "HAVING SUM(d.znesekDonacije) > 500 AND COUNT(DISTINCT d) >= 3")
    List<Uporabnik> pridobiUporabnikeZVisokimiDonacijami();


//hihi

//    @Query("SELECT u FROM Uporabnik u")
//    List<Uporabnik> findByUporabniskoIme(String uporabniskoIme);
    @Query("SELECT u FROM Uporabnik u WHERE LOWER(u.uporabniskoIme) = LOWER(:uporabniskoIme)")
    List<Uporabnik> findByUporabniskoIme(String uporabniskoIme);

    @Query("SELECT DISTINCT new si.um.feri.ris.models.Uporabnik(u.ime, u.priimek, u.uporabniskoIme) FROM Uporabnik u JOIN u.donacije d ")
    List<Uporabnik> najdiUporabnikeKiSoDonirali();

    @Query("SELECT u FROM Uporabnik u " +
            "JOIN u.donacije d " +
            "GROUP BY u.id " +
            "HAVING SUM(d.znesekDonacije) > 10")
    List<Uporabnik> findDonatorjiZnesekVecjiOd10();

    @Query("SELECT u FROM Uporabnik u " +
            "JOIN u.donacije d " +
            "GROUP BY u.id " +
            "HAVING SUM(d.znesekDonacije) > 100")
    List<Uporabnik> findDonatorjiZnesekVecjiOd100();

    @Query("SELECT u FROM Uporabnik u " +
            "JOIN u.donacije d " +
            "GROUP BY u.id " +
            "HAVING SUM(d.znesekDonacije) > 1000")
    List<Uporabnik> findDonatorjiZnesekVecjiOd1000();

    @Query("SELECT u FROM Uporabnik u " +
            "JOIN u.donacije d " +
            "GROUP BY u.id " +
            "HAVING SUM(d.znesekDonacije) > 10000")
    List<Uporabnik> findDonatorjiZnesekVecjiOd10000();

    @Query("SELECT o FROM Oskodovanec o WHERE o NOT IN (SELECT d.oskodovanci FROM o.donacije d) " +
            "AND EXISTS (SELECT n FROM o.nesrece n WHERE n.lokacija LIKE CONCAT('%', :naslovUporabnika, '%'))")
    List<Oskodovanec> findOskodovanciBrezDonacijeInPodobnaLokacijaNesrece(@Param("naslovUporabnika") String naslovUporabnika);



}
