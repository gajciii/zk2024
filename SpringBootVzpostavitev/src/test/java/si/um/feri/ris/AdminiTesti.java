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


    @Test
    @Transactional
    void testPrijavaAdmina() { // uspešna prijava administratorja

        Administrator admin = new Administrator();
        admin.setUporabniskoIme("adminTest");
        admin.setGeslo("adminGeslo");

        administratorDAO.save(admin);

        ResponseEntity<String> response = administratorController.prijavaAdmina(admin); //testira prijavo admina

        assertEquals(HttpStatus.OK, response.getStatusCode()); //status mora biti OK
        assertEquals(String.valueOf(admin.getId()), response.getBody());
    }



    @Test
    public void testDodajNesreco() { // dodajanje nesreče

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

}

