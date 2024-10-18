package si.um.feri.ris.controllers;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import si.um.feri.ris.models.Donacija;
import si.um.feri.ris.models.Oskodovanec;
import si.um.feri.ris.repository.PregledDonacij;
import si.um.feri.ris.repository.PregledOskodovancev;

import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/oskodovanci")
public class OskodovanecController {

    @Autowired
    private PregledOskodovancev oskodovanecDao;

    @Autowired
    private PregledDonacij donacijaDao;

    @GetMapping("/oskodovanci")
    public Iterable<Oskodovanec> vrniOskodovance() {
        return oskodovanecDao.findAll();
    }

    @GetMapping("/oskodovanci/{id}")
    public Oskodovanec vrniOskodovanca(long id) {
        return oskodovanecDao.findById(id).get();
    }

    @PostMapping
    public Oskodovanec dodajOskodovanca(Oskodovanec oskodovanec) {

        return oskodovanecDao.save(oskodovanec);
    }
    @Transactional
    @PostMapping("/dodajDonacijoOskodovancu/{oskodovanecId}")
    public ResponseEntity<byte[]> dodajDonacijoOskodovancu(@PathVariable Long oskodovanecId, @RequestBody Donacija novaDonacija) {
        try {
            Optional<Oskodovanec> najdenUporabnik = oskodovanecDao.findById(oskodovanecId);
            if (najdenUporabnik.isPresent()) {
                Oskodovanec oskodovanec = najdenUporabnik.get();

                Donacija novaDonacijaEntiteta = new Donacija();
                novaDonacijaEntiteta.setZnesekDonacije(novaDonacija.getZnesekDonacije());
                novaDonacijaEntiteta = donacijaDao.save(novaDonacijaEntiteta);

                oskodovanec.dodajDonacijo(novaDonacijaEntiteta);
                oskodovanecDao.save(oskodovanec);
                return ResponseEntity.status(HttpStatus.CREATED).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/izbrisiOskodovanca/{id}")
    public ResponseEntity<Object> izbrisiOskodovanca(@PathVariable long id){
        try {
            Oskodovanec oskodovanec = oskodovanecDao.findById(id).orElse(null);
            if (oskodovanec != null) {
                oskodovanecDao.delete(oskodovanec);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Napaka pri brisanju podatkov: " + e.getMessage());
        }
    }

    @PutMapping("/urediOskodovanca/{id}")
    public ResponseEntity<Oskodovanec> urediOskodovanca(@PathVariable long id, @RequestBody Oskodovanec posodobljenOskodovanec){
        Oskodovanec obstojecOskodovanec = oskodovanecDao.findById(id).orElse(null);

        try {
            if(obstojecOskodovanec != null){
                if(posodobljenOskodovanec.getIme() != null){

                    obstojecOskodovanec.setIme(posodobljenOskodovanec.getIme());
                }
                if(posodobljenOskodovanec.getPriimek() != null){

                    obstojecOskodovanec.setPriimek(posodobljenOskodovanec.getPriimek());
                }
                if(!String.valueOf(posodobljenOskodovanec.isImaDruzino()).equals("None")){

                    obstojecOskodovanec.setImaDruzino(posodobljenOskodovanec.isImaDruzino());
                }
                if(!String.valueOf(posodobljenOskodovanec.isMocnejePoskodovan()).equals("None")){

                    obstojecOskodovanec.setMocnejePoskodovan(posodobljenOskodovanec.isMocnejePoskodovan());
                }
                oskodovanecDao.save(obstojecOskodovanec);
                return ResponseEntity.ok(obstojecOskodovanec);
            }
            else{
                return ResponseEntity.notFound().build();
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obstojecOskodovanec);
        }
    }

    @GetMapping("/mocnejePoskodovanBrezDruzine")
    public Iterable<Oskodovanec> findMocnejePoskodovanBrezDruzine() {
        return oskodovanecDao.findMocnejePoskodovanBrezDruzine();
    }

    @GetMapping("/mocnejePoskodovanZDruzino")
    public Iterable<Oskodovanec> findVse() {
        return oskodovanecDao.findOskodovanciByVse();
    }

//    vsaj 5 donacij:
    @GetMapping("/prejeteDonacije")
    public Iterable<Oskodovanec> prejeliDonacijo() {
        return oskodovanecDao.najdiOskodovanceKiSoPrejeliDonacijo();
    }

}