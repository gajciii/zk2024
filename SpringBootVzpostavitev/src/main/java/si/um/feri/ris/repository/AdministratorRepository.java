package si.um.feri.ris.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import si.um.feri.ris.models.Administrator;
import si.um.feri.ris.models.Nesreca;
import si.um.feri.ris.models.Uporabnik;

import java.util.List;

public interface AdministratorRepository extends CrudRepository <Administrator, Long> {

    @Query("SELECT a FROM Administrator a WHERE LOWER(a.uporabniskoIme) = LOWER(:uporabniskoIme)")
    List<Administrator> findByUporabniskoImeAdmin(String uporabniskoIme);

    @Query("select a from Administrator a")
    List<Nesreca> findAdministratorById(long id);



}
