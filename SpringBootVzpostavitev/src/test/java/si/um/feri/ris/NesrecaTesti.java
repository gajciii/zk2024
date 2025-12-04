package si.um.feri.ris;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import si.um.feri.ris.controllers.NesrecaController;
import si.um.feri.ris.models.Administrator;
import si.um.feri.ris.models.Nesreca;
import si.um.feri.ris.models.Oskodovanec;
import si.um.feri.ris.repository.AdministratorRepository;
import si.um.feri.ris.repository.ListNesrec;
import si.um.feri.ris.repository.PregledOskodovancev;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NesrecaTesti {

    @Autowired
    private NesrecaController nesrecaController;

    @Autowired
    private ListNesrec nesrecaDAO;

    @Autowired
    private AdministratorRepository administratorDAO;

    @Autowired
    private PregledOskodovancev oskodovanecDAO;

    @BeforeEach
    public void init() {
        nesrecaDAO.deleteAll();
        administratorDAO.deleteAll();
        oskodovanecDAO.deleteAll();
    }

    @Test
    @Transactional
    public void testDodajNesreco() {
        Nesreca novaNesreca = new Nesreca(new Date(), "Testna nesreča", "Testna lokacija");
        
        Nesreca shranjenaNesreca = nesrecaController.dodajNesreco(novaNesreca);
        
        assertNotNull(shranjenaNesreca);
        assertNotNull(shranjenaNesreca.getId());
        assertEquals("Testna nesreča", shranjenaNesreca.getOpis());
        assertEquals("Testna lokacija", shranjenaNesreca.getLokacija());
    }

    @Test
    @Transactional
    public void testVrniNesrecoPoId() {
        Nesreca novaNesreca = new Nesreca(new Date(), "Testna nesreča", "Testna lokacija");
        novaNesreca = nesrecaDAO.save(novaNesreca);
        
        ResponseEntity<Nesreca> response = nesrecaController.vrniNesrecoPoId(novaNesreca.getId());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(novaNesreca.getId(), response.getBody().getId());
    }

    @Test
    @Transactional
    public void testVrniNesrecoPoIdNeObstaja() {
        ResponseEntity<Nesreca> response = nesrecaController.vrniNesrecoPoId(999L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Transactional
    public void testOdstraniNesreco() {
        Nesreca novaNesreca = new Nesreca(new Date(), "Testna nesreča", "Testna lokacija");
        novaNesreca = nesrecaDAO.save(novaNesreca);
        Long nesrecaId = novaNesreca.getId();
        
        ResponseEntity<Nesreca> response = nesrecaController.odstraniNesreco(nesrecaId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(nesrecaDAO.findById(nesrecaId).isPresent());
    }

    @Test
    @Transactional
    public void testUrediNesreco() {
        Nesreca novaNesreca = new Nesreca(new Date(), "Stari opis", "Stara lokacija");
        novaNesreca = nesrecaDAO.save(novaNesreca);
        
        Nesreca posodobljenaNesreca = new Nesreca();
        posodobljenaNesreca.setOpis("Novi opis");
        posodobljenaNesreca.setLokacija("Nova lokacija");
        
        ResponseEntity<Nesreca> response = nesrecaController.urediNesreco(novaNesreca.getId(), posodobljenaNesreca);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Novi opis", response.getBody().getOpis());
        assertEquals("Nova lokacija", response.getBody().getLokacija());
    }

    @Test
    @Transactional
    public void testNesreceVecKotTriOskodovanci() {
        Administrator admin = new Administrator();
        admin.setUporabniskoIme("adminTest");
        admin.setGeslo("geslo");
        admin = administratorDAO.save(admin);
        
        Nesreca nesreca = new Nesreca(new Date(), "Nesreča z več oškodovanci", "Lokacija");
        nesreca.setAdministrator(admin);
        nesreca = nesrecaDAO.save(nesreca);
        
        for (int i = 0; i < 4; i++) {
            Oskodovanec oskodovanec = new Oskodovanec();
            oskodovanec.setIme("Oškodovanec" + i);
            oskodovanec.setPriimek("Test");
            oskodovanecDAO.save(oskodovanec);
        }
        
        ResponseEntity<List<Nesreca>> response = nesrecaController.getNesreceWithThreeOrMoreOskodovanci();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}

