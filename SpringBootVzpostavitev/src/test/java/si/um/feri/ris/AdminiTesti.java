package si.um.feri.ris;
import static org.junit.jupiter.api.Assertions.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import si.um.feri.ris.controllers.AdministratorController;
import si.um.feri.ris.models.*;
import si.um.feri.ris.repository.AdministratorRepository;
import si.um.feri.ris.repository.ListNesrec;
import si.um.feri.ris.repository.PregledOskodovancev;
import si.um.feri.ris.repository.UporabnikRepository;

import java.util.*;


@SpringBootTest
public class AdminiTesti {

    @Autowired
    private UporabnikRepository uporabnikDAO;

    @Autowired
    private PregledOskodovancev oskodovancevDAO;

    @Autowired
    private ListNesrec nesrecaDAO;

    @Autowired
    private AdministratorRepository administratorDAO;

    @Autowired
    private AdministratorController administratorController;

    @BeforeEach
    public void init() {
        uporabnikDAO.deleteAll();
        oskodovancevDAO.deleteAll();
        administratorDAO.deleteAll();
        nesrecaDAO.deleteAll();

    }


    // Testira uspešno prijavo administratorja
    @Test
    @Transactional
    void testPrijavaAdmina() {

        Administrator admin = new Administrator();
        admin.setUporabniskoIme("adminTest");
        admin.setGeslo("adminGeslo");

        administratorDAO.save(admin);

        ResponseEntity<String> response = administratorController.prijavaAdmina(admin); //testira prijavo admina

        assertEquals(HttpStatus.OK, response.getStatusCode()); //status mora biti OK
        assertEquals(String.valueOf(admin.getId()), response.getBody());
    }



    // Testira dodajanje nesreče s strani administratorja
    @Test
    public void testDodajNesreco() {

        Administrator admin = new Administrator();
        admin.setUporabniskoIme("adminTest");
        admin.setGeslo("adminGeslo");
        administratorDAO.save(admin);

        Nesreca novaNesreca = new Nesreca(new Date(), "Testna nesreča", "Koroška cesta 46");
        novaNesreca.setAdministrator(admin);

        ResponseEntity<Nesreca> response = administratorController.dodajNesreco(novaNesreca);//test dodajanja nesreče

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Testna nesreča", response.getBody().getOpis());
        assertEquals("Koroška cesta 46", response.getBody().getLokacija());

        //preverim da je nesreča shranjena v bazo
        Nesreca shranjenaNesreca = nesrecaDAO.findById(response.getBody().getId()).orElse(null);
        assertNotNull(shranjenaNesreca);
        assertEquals("Testna nesreča", shranjenaNesreca.getOpis());
        assertEquals("Koroška cesta 46", shranjenaNesreca.getLokacija());

        nesrecaDAO.deleteAll();
        administratorDAO.deleteAll();
    }

    // Testira neuspešno prijavo administratorja z napačnim geslom
    @Test
    @Transactional
    void testNeuspesnaPrijavaAdmina() {
        Administrator admin = new Administrator();
        admin.setUporabniskoIme("adminTest");
        admin.setGeslo("adminGeslo");
        administratorDAO.save(admin);

        Administrator napačenAdmin = new Administrator();
        napačenAdmin.setUporabniskoIme("adminTest");
        napačenAdmin.setGeslo("napačnoGeslo");

        ResponseEntity<String> response = administratorController.prijavaAdmina(napačenAdmin);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Testira dodajanje novega administratorja
    @Test
    @Transactional
    void testDodajAdministratorja() {
        Administrator novAdmin = new Administrator();
        novAdmin.setUporabniskoIme("noviAdmin");
        novAdmin.setGeslo("geslo123");

        ResponseEntity<String> response = administratorController.dodajAdministratorja(novAdmin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("uspešno dodan"));
    }

    // Testira registracijo novega administratorja
    @Test
    @Transactional
    void testRegistracijaAdmina() {
        Administrator novAdmin = new Administrator();
        novAdmin.setUporabniskoIme("noviAdmin");
        novAdmin.setGeslo("geslo123");

        ResponseEntity<String> response = administratorController.regisracijaAdmina(novAdmin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("uspešno registriran"));
    }

    // Testira dodajanje oškodovanca s strani administratorja
    @Test
    @Transactional
    void testDodajOskodovanca() {
        Oskodovanec novOskodovanec = new Oskodovanec();
        novOskodovanec.setIme("Janez");
        novOskodovanec.setPriimek("Novak");

        ResponseEntity<Oskodovanec> response = administratorController.dodajOskodovanca(novOskodovanec);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Janez", response.getBody().getIme());
    }

    // Testira odstranjevanje oškodovanca s strani administratorja
    @Test
    @Transactional
    void testOdstraniOskodovanca() {
        Oskodovanec oskodovanec = new Oskodovanec();
        oskodovanec.setIme("Janez");
        oskodovanec.setPriimek("Novak");
        oskodovanec = oskodovancevDAO.save(oskodovanec);

        ResponseEntity<String> response = administratorController.odstraniOskodovanca(oskodovanec.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("uspešno odstranjen"));
    }

}

