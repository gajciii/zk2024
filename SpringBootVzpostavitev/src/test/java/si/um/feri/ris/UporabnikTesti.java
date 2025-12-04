package si.um.feri.ris;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import si.um.feri.ris.controllers.UporabnikController;
import si.um.feri.ris.models.Donacija;
import si.um.feri.ris.models.Uporabnik;
import si.um.feri.ris.repository.PregledDonacij;
import si.um.feri.ris.repository.UporabnikRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UporabnikTesti {

    @Autowired
    private UporabnikRepository uporabnikDao;
    @Autowired
    private PregledDonacij donacijaDao;
    @Autowired
    private UporabnikController uporabnikController;

    @BeforeEach
    public void init() {
        uporabnikDao.deleteAll();
        donacijaDao.deleteAll();
    }

    @Test
    @Transactional
    public void testNeuspesnaPrijavaUporabnika() {

        Uporabnik shranjeniUporabnik = new Uporabnik();
        shranjeniUporabnik.setUporabniskoIme("testUser");
        shranjeniUporabnik.setGeslo("pravilnoGeslo");
        uporabnikDao.save(shranjeniUporabnik);  // shranim uporabnika v bazo

        Uporabnik prijavljeniUporabnik = new Uporabnik();
        prijavljeniUporabnik.setUporabniskoIme("testUser");
        prijavljeniUporabnik.setGeslo("napačnoGeslo");  // prijava z napačnim geslom

        ResponseEntity<Uporabnik> response = uporabnikController.prijavaUporabnika(prijavljeniUporabnik);//neuspešna prijava-klic metode prijava ampak ima napačno geslo

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());  // status mora biti UNAUTHORIZED
        assertNull(response.getBody());  // telo odgovora mora biti null

        uporabnikDao.deleteAll();
    }

    @Test
    @Transactional //da so spremembe izvedene preden test preveri rezultate
    public void testPridobiVeckratneDonatorje() {

        Uporabnik uporabnik1 = new Uporabnik("uporabnisko_ime1", "ime1", "priimek1");
        uporabnikDao.save(uporabnik1);
        Uporabnik uporabnik2 = new Uporabnik("uporabnisko_ime2", "ime2", "priimek2");
        uporabnikDao.save(uporabnik2);

        Donacija donacija1 = new Donacija();
        donacija1.setZnesekDonacije(200); //donacija za uporabnika 1
        uporabnikController.dodajDonacijoUporabniku(uporabnik1.getId(), donacija1);

        Donacija donacija2 = new Donacija();
        donacija2.setZnesekDonacije(2000); //donacija za uporabnika 2
        uporabnikController.dodajDonacijoUporabniku(uporabnik2.getId(), donacija2);

        Donacija donacija3 = new Donacija();
        donacija3.setZnesekDonacije(300); //donacija spet za uporabnika 1
        uporabnikController.dodajDonacijoUporabniku(uporabnik1.getId(), donacija3);

        List<Uporabnik> veckratniDonatorji = uporabnikController.pridobiVeckratneDonatorje(); //klic metode za večkratne donatorje

        assertNotNull(veckratniDonatorji, "Seznam večkratnih donatorjev ne sme biti null");
        assertEquals(1, veckratniDonatorji.size(), "Mora biti samo en večkratni donator");

        Uporabnik firstDonor = veckratniDonatorji.get(0); //preverim da je prvi donator v seznamu dejansko bil returnan
        assertEquals(uporabnik1.getUporabniskoIme(), firstDonor.getUporabniskoIme(),"Prvi donator v seznamu mora biti uporabnik1");
    }


    @Test
    @Transactional
    public void testPridobiDonatorjeZnesekVecjiOd100() { //donator z zneskom večjim od 100

        Uporabnik uporabnik1 = new Uporabnik("uporabnisko_ime1", "ime1", "priimek1");
        uporabnikDao.save(uporabnik1);
        Uporabnik uporabnik2 = new Uporabnik("uporabnisko_ime2", "ime2", "priimek2");
        uporabnikDao.save(uporabnik2);

        //donacije uporabnika 1
        Donacija d1 = new Donacija();
        Donacija d2 = new Donacija();
        Donacija d3 = new Donacija();

        d1.setZnesekDonacije(50);
        d2.setZnesekDonacije(150);
        d3.setZnesekDonacije(200);

        uporabnikController.dodajDonacijoUporabniku(uporabnik1.getId(), d1);
        uporabnikController.dodajDonacijoUporabniku(uporabnik1.getId(), d2);
        uporabnikController.dodajDonacijoUporabniku(uporabnik1.getId(), d3);

        //donacije uporabnika 2
        Donacija d4 = new Donacija();
        d4.setZnesekDonacije(100);
        uporabnikController.dodajDonacijoUporabniku(uporabnik2.getId(), d4);

        List<Uporabnik> donatorjiNad100 = uporabnikController.pridobiDonatorjeZnesekVecjiOd100(); //klic metode za donatorje z zneskom večjim od 100

        //preverjanje rezultatov
        assertFalse(donatorjiNad100.isEmpty(), "Seznam donatorjev ne sme biti prazen");
        assertEquals(uporabnik1.getUporabniskoIme(), donatorjiNad100.get(0).getUporabniskoIme(), "Donator mora biti uporabnik1 - doniral več kot 100");
        assertEquals(1, donatorjiNad100.size(), "Število donatorjev nad 100 mora biti 1");
    }


}
