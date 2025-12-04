package si.um.feri.ris;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import si.um.feri.ris.controllers.DonacijaController;
import si.um.feri.ris.models.Donacija;
import si.um.feri.ris.repository.PregledDonacij;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DonacijaTesti {

    @Autowired
    private DonacijaController donacijaController;

    @Autowired
    private PregledDonacij donacijaDAO;

    @BeforeEach
    public void init() {
        donacijaDAO.deleteAll();
    }

    // Testira dodajanje nove donacije
    @Test
    @Transactional
    public void testDodajDonacijo() {
        Donacija novaDonacija = new Donacija();
        novaDonacija.setZnesekDonacije(100.0);
        
        ResponseEntity<String> response = donacijaController.dodajDonacijo(novaDonacija);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("uspešno dodana"));
        assertTrue(response.getBody().contains("100.0"));
    }

    // Testira prikaz donacije po ID-ju
    @Test
    @Transactional
    public void testPrikaziDonacijo() {
        Donacija donacija = new Donacija();
        donacija.setZnesekDonacije(200.0);
        donacija = donacijaDAO.save(donacija);
        
        Donacija najdenaDonacija = donacijaController.prikaziDonacijo(donacija.getId());
        
        assertNotNull(najdenaDonacija);
        assertEquals(donacija.getId(), najdenaDonacija.getId());
        assertEquals(200.0, najdenaDonacija.getZnesekDonacije());
    }

    // Testira brisanje donacije
    @Test
    @Transactional
    public void testIzbrisiDonacijo() {
        Donacija donacija = new Donacija();
        donacija.setZnesekDonacije(300.0);
        donacija = donacijaDAO.save(donacija);
        Long donacijaId = donacija.getId();
        
        ResponseEntity<Donacija> response = donacijaController.izbrisiDonacijo(donacijaId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(donacijaDAO.findById(donacijaId).isPresent());
    }

    // Testira brisanje neobstoječe donacije
    @Test
    @Transactional
    public void testIzbrisiDonacijoNeObstaja() {
        ResponseEntity<Donacija> response = donacijaController.izbrisiDonacijo(999L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Testira urejanje donacije
    @Test
    @Transactional
    public void testUrediDonacijo() {
        Donacija donacija = new Donacija();
        donacija.setZnesekDonacije(100.0);
        donacija = donacijaDAO.save(donacija);
        
        Donacija posodobljenaDonacija = new Donacija();
        posodobljenaDonacija.setZnesekDonacije(500.0);
        
        ResponseEntity<Donacija> response = donacijaController.urediDonacijo(donacija.getId(), posodobljenaDonacija);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500.0, response.getBody().getZnesekDonacije());
    }

    // Testira prikaz neobstoječe donacije
    @Test
    @Transactional
    public void testPrikaziDonacijoNeObstaja() {
        Donacija donacija = donacijaController.prikaziDonacijo(999L);
        
        assertNull(donacija);
    }

    // Testira pridobivanje vseh donacij
    @Test
    @Transactional
    public void testPrikaziVseDonacije() {
        Donacija donacija1 = new Donacija();
        donacija1.setZnesekDonacije(100.0);
        donacijaDAO.save(donacija1);
        
        Donacija donacija2 = new Donacija();
        donacija2.setZnesekDonacije(200.0);
        donacijaDAO.save(donacija2);
        
        Iterable<Donacija> vseDonacije = donacijaController.prikaziDonacijo();
        
        assertNotNull(vseDonacije);
        int count = 0;
        for (@SuppressWarnings("unused") Donacija d : vseDonacije) {
            count++;
        }
        assertEquals(2, count);
    }
}

