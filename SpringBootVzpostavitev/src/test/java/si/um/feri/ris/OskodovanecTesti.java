package si.um.feri.ris;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import si.um.feri.ris.controllers.OskodovanecController;
import si.um.feri.ris.models.Donacija;
import si.um.feri.ris.models.Oskodovanec;
import si.um.feri.ris.repository.AdministratorRepository;
import si.um.feri.ris.repository.ListNesrec;
import si.um.feri.ris.repository.PregledDonacij;
import si.um.feri.ris.repository.PregledOskodovancev;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OskodovanecTesti {

    @Autowired
    private OskodovanecController oskodovanecController;

    @Autowired
    private PregledOskodovancev oskodovanecDAO;

    @Autowired
    private PregledDonacij donacijaDAO;

    @Autowired
    private ListNesrec nesrecaDAO;

    @Autowired
    private AdministratorRepository administratorDAO;

    @BeforeEach
    public void init() {
        oskodovanecDAO.deleteAll();
        donacijaDAO.deleteAll();
        nesrecaDAO.deleteAll();
        administratorDAO.deleteAll();
    }

    // Testira dodajanje novega oškodovanca
    @Test
    @Transactional
    public void testDodajOskodovanca() {
        Oskodovanec novOskodovanec = new Oskodovanec();
        novOskodovanec.setIme("Janez");
        novOskodovanec.setPriimek("Novak");
        novOskodovanec.setImaDruzino(true);
        novOskodovanec.setMocnejePoskodovan(false);
        
        Oskodovanec shranjenOskodovanec = oskodovanecController.dodajOskodovanca(novOskodovanec);
        
        assertNotNull(shranjenOskodovanec);
        assertNotNull(shranjenOskodovanec.getId());
        assertEquals("Janez", shranjenOskodovanec.getIme());
        assertEquals("Novak", shranjenOskodovanec.getPriimek());
    }

    // Testira dodajanje donacije oškodovancu
    @Test
    @Transactional
    public void testDodajDonacijoOskodovancu() {
        Oskodovanec oskodovanec = new Oskodovanec();
        oskodovanec.setIme("Janez");
        oskodovanec.setPriimek("Novak");
        oskodovanec = oskodovanecDAO.save(oskodovanec);
        
        Donacija donacija = new Donacija();
        donacija.setZnesekDonacije(500.0);
        
        ResponseEntity<byte[]> response = oskodovanecController.dodajDonacijoOskodovancu(oskodovanec.getId(), donacija);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        Oskodovanec posodobljenOskodovanec = oskodovanecDAO.findById(oskodovanec.getId()).orElse(null);
        assertNotNull(posodobljenOskodovanec);
        
        long donacijeCount = donacijaDAO.count();
        assertTrue(donacijeCount > 0);
    }

    // Testira dodajanje donacije neobstoječemu oškodovancu
    @Test
    @Transactional
    public void testDodajDonacijoOskodovancuNeObstaja() {
        Donacija donacija = new Donacija();
        donacija.setZnesekDonacije(500.0);
        
        ResponseEntity<byte[]> response = oskodovanecController.dodajDonacijoOskodovancu(999L, donacija);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Testira brisanje oškodovanca
    @Test
    @Transactional
    public void testIzbrisiOskodovanca() {
        Oskodovanec oskodovanec = new Oskodovanec();
        oskodovanec.setIme("Janez");
        oskodovanec.setPriimek("Novak");
        oskodovanec = oskodovanecDAO.save(oskodovanec);
        Long oskodovanecId = oskodovanec.getId();
        
        ResponseEntity<Object> response = oskodovanecController.izbrisiOskodovanca(oskodovanecId);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(oskodovanecDAO.findById(oskodovanecId).isPresent());
    }

    // Testira urejanje oškodovanca
    @Test
    @Transactional
    public void testUrediOskodovanca() {
        Oskodovanec oskodovanec = new Oskodovanec();
        oskodovanec.setIme("Janez");
        oskodovanec.setPriimek("Novak");
        oskodovanec = oskodovanecDAO.save(oskodovanec);
        
        Oskodovanec posodobljenOskodovanec = new Oskodovanec();
        posodobljenOskodovanec.setIme("Miha");
        posodobljenOskodovanec.setPriimek("Horvat");
        
        ResponseEntity<Oskodovanec> response = oskodovanecController.urediOskodovanca(oskodovanec.getId(), posodobljenOskodovanec);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Miha", response.getBody().getIme());
        assertEquals("Horvat", response.getBody().getPriimek());
    }
}

