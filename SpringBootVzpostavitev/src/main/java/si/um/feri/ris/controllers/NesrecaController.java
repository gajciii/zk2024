package si.um.feri.ris.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import si.um.feri.ris.models.Nesreca;
import si.um.feri.ris.repository.ListNesrec;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/nesrece")
public class NesrecaController {

    @Autowired
    private ListNesrec nesrecaDAO;
    @GetMapping("/nesrece")
    public Iterable<Nesreca> vrniNesrece(){
        return nesrecaDAO.findAll();
    }

    @GetMapping("nesreca/{id}")
    public Nesreca vrniNesreco(@PathVariable Long id){
        return nesrecaDAO.findById(id).orElse(null);
    }

    @PostMapping("/dodajNesreco")
    public Nesreca dodajNesreco(@RequestBody Nesreca nesreca){
        return nesrecaDAO.save(nesreca);
    }

    @GetMapping("/nesreceVecKotTriOskodovanci")
    public ResponseEntity<List<Nesreca>> getNesreceWithThreeOrMoreOskodovanci() {
        List<Nesreca> NesrecaTriAliVec = nesrecaDAO.findNesrecaPoskodovaniTriAliVec();
        return ResponseEntity.ok(NesrecaTriAliVec);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Nesreca> vrniNesrecoPoId(@PathVariable Long id) {
        try {
            Nesreca nesreca = nesrecaDAO.findNesrecaById(id).get(0);

            return ResponseEntity.ok(nesreca);
        } catch (Exception e) {
            System.out.println(e + "\n napaka pri pridobivanju Id-ja");;
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/odstraniNesreco/{id}")
    public ResponseEntity<Nesreca> odstraniNesreco(@PathVariable Long id){
        try{
            Nesreca nesreca = nesrecaDAO.findById(id).orElse(null);
            if(nesreca != null){
                nesrecaDAO.delete(nesreca);
                return ResponseEntity.ok(nesreca);
            }
            else{
                return ResponseEntity.notFound().build();
            }
        }catch (Exception e){
            System.out.println(e + "\nNapaka pri brisanju nesrece");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/urediNesreco/{id}")
    public ResponseEntity<Nesreca> urediNesreco(@PathVariable Long id, @RequestBody Nesreca posodobljenaNesreca){
        Nesreca obstojecaNesreca = nesrecaDAO.findById(id).orElse(null);
        try {
            if(obstojecaNesreca != null){
                obstojecaNesreca.setDatum(posodobljenaNesreca.getDatum());
                obstojecaNesreca.setLokacija(posodobljenaNesreca.getLokacija());
                obstojecaNesreca.setOpis(posodobljenaNesreca.getOpis());
                nesrecaDAO.save(obstojecaNesreca);
                return ResponseEntity.ok(obstojecaNesreca);
            }
            else{
                return ResponseEntity.notFound().build();
            }
        }
        catch (Exception e){
            System.out.println(e + "\nNapaka pri urejanju nesrece");
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/krajDatum")
    public Iterable<Nesreca> vrniNesrecoPoKrajDatumBeseda() {
        LocalDate datumOd = LocalDate.of(2023, 1, 1);
        LocalDate datumDo = LocalDate.of(2024, 1, 1);
        String kraj = "Slovenija";
        String beseda = "prometna nesreča";

        return nesrecaDAO.findNesreceByDatumKrajBeseda(
                Date.from(datumOd.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(datumDo.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                kraj,
                beseda
        );
    }


}