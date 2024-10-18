package si.um.feri.ris.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import si.um.feri.ris.models.*;
import si.um.feri.ris.repository.AdministratorRepository;
import si.um.feri.ris.repository.ListNesrec;
import si.um.feri.ris.repository.PregledDonacij;
import si.um.feri.ris.repository.PregledOskodovancev;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/administratorji")
public class AdministratorController {

    @Autowired
    private ListNesrec nesrecaDAO;
    @Autowired
    private PregledOskodovancev oskodovanciDAO;

    @Autowired
    private AdministratorRepository administratorDAO;

    @PostMapping
    public ResponseEntity<Nesreca> dodajNesreco(@RequestBody Nesreca nesreca) {
        if (nesreca.getAdministrator() != null) {
            long adminId = nesreca.getAdministrator().getId();
            Optional<Administrator> adminOptional = administratorDAO.findById(adminId);

            if (adminOptional.isPresent()) {
                nesreca.setAdministrator(adminOptional.get());
                nesrecaDAO.save(nesreca);
                return ResponseEntity.ok(nesreca);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @PostMapping("/dodajAdministratorja")
    public ResponseEntity<String> dodajAdministratorja(@RequestBody Administrator novAdmin) {
        try {
            System.out.println("Nov admin prejet: " + novAdmin);
            System.out.println("Novo uporabnisko ime: " + novAdmin.getUporabniskoIme());
            administratorDAO.save(novAdmin);
            return ResponseEntity.ok("Administrator uspešno dodan. " + novAdmin.getUporabniskoIme());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Napaka pri dodajanju administratorja: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> izbrisiNesreco(@PathVariable long id) {
        Optional<Nesreca> nesreca = nesrecaDAO.findById(id);
        if (nesreca.isPresent()) {
            nesrecaDAO.deleteById(id);
            return ResponseEntity.ok("Nesreča uspešno izbrisana");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/urediNesreco")
    public ResponseEntity<Nesreca> urediNesreco(@RequestBody Nesreca nesreca) {
        Optional<Nesreca> existingNesreca = nesrecaDAO.findById(nesreca.getId());

        if (existingNesreca.isPresent()) {
            existingNesreca.get().setDatum(nesreca.getDatum());
            existingNesreca.get().setLokacija(nesreca.getLokacija());
            existingNesreca.get().setOpis(nesreca.getOpis());
            nesrecaDAO.save(existingNesreca.get());

            return ResponseEntity.ok(existingNesreca.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/dodajOskodovanca")
    public ResponseEntity<Oskodovanec> dodajOskodovanca(@RequestBody Oskodovanec oskodovanec) {
        oskodovanciDAO.save(oskodovanec);
        return ResponseEntity.ok(oskodovanec);
    }

    @DeleteMapping("/odstraniOskodovanca/{id}")
    public ResponseEntity<String> odstraniOskodovanca(@PathVariable long id) {
        Optional<Oskodovanec> oskodovanec = oskodovanciDAO.findById(id);
        if (oskodovanec.isPresent()) {
            oskodovanciDAO.deleteById(id);
            return ResponseEntity.ok("Oškodovanec uspešno odstranjen");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Oškodovanec s podanim ID-jem ni bil najden");
        }
    }

    @PostMapping("/prijava")
    public ResponseEntity<String> prijavaAdmina(@RequestBody Administrator prijavljenAdmin){
        try {
            if (prijavljenAdmin.getUporabniskoIme() == null || prijavljenAdmin.getGeslo() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Uporabniško ime ali geslo manjka.");
            }

            List<Administrator> admin = administratorDAO.findByUporabniskoImeAdmin(prijavljenAdmin.getUporabniskoIme());

            if (admin.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrator s tem uporabniškim imenom ne obstaja");
            } else if (!admin.get(0).getGeslo().equals(prijavljenAdmin.getGeslo())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Napačno geslo");
            } else {
                Long adminId = admin.get(0).getId();
                return ResponseEntity.ok(adminId.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Napaka pri prijavi administratorja: " + e.getMessage());
        }
    }


    @PostMapping("/registracijaAdmina")
    public ResponseEntity<String> regisracijaAdmina(@RequestBody Administrator novAdmin) {
        try {
            List<Administrator> obstojecAdmin = administratorDAO.findByUporabniskoImeAdmin(novAdmin.getUporabniskoIme());
            if (obstojecAdmin.isEmpty()) {
                administratorDAO.save(novAdmin);
                return ResponseEntity.ok(novAdmin.getUporabniskoIme() + " uspešno registriran");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Administrator s tem uporabniškim imenom že obstaja " + novAdmin.getUporabniskoIme());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Napaka pri registraciji administratorja: " + e.getMessage());
        }
    }

}