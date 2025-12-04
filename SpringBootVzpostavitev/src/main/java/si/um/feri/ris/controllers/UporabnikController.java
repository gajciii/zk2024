package si.um.feri.ris.controllers;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import si.um.feri.ris.models.Oskodovanec;
import si.um.feri.ris.models.Uporabnik;
import si.um.feri.ris.models.Donacija;
import si.um.feri.ris.EmailSender.EmailSendingService;
import si.um.feri.ris.repository.PregledDonacij;
import si.um.feri.ris.repository.UporabnikRepository;
import si.um.feri.ris.PdfGenerator.PdfController;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/uporabniki")
public class UporabnikController {

    @Autowired
    private PregledDonacij donacijaDao;

    @Autowired
    private UporabnikRepository uporabnikDao;

    @Autowired
    private EmailSendingService emailSendingService;

    @GetMapping("/donacije")
    public Iterable<Donacija> seznamDonacij() {
        return donacijaDao.findAll();
    }

    @GetMapping("/uporabniki")
    public Iterable<Uporabnik> seznamUporabnikov() {
        return uporabnikDao.findAll();
    }

    @GetMapping("/uporabnik/{id}")
    public ResponseEntity<Uporabnik> uporabnikID(@PathVariable Long id) {
        Optional<Uporabnik> uporabnik = uporabnikDao.findUporabnikById(id);
        if (uporabnik.isPresent()) {
            return ResponseEntity.ok(uporabnik.get());
        } else {
            return ResponseEntity.notFound().build();

        }
    }

    @GetMapping("/TopDonatorji")
    public List<Uporabnik> pridobiTopDonatorje() {
        return uporabnikDao.pridobiUporabnikeZVisokimiDonacijami();
    }


//    @PostMapping("/dodajDonacijo")
//    public Donacija dodajDonacijo(Uporabnik uporabnik, Donacija donacija) {
//        return uporabnik.dodajDonacijo(donacija);
//    }



    @PostMapping("/dodajDonacijoUporabniku/{uporabnikId}")
    public ResponseEntity<byte[]> dodajDonacijoUporabniku(@PathVariable Long uporabnikId, @RequestBody Donacija novaDonacija) {
        try {
            Optional<Uporabnik> najdenUporabnik = uporabnikDao.findById(uporabnikId);
            if (najdenUporabnik.isPresent()) {
                Uporabnik uporabnik = najdenUporabnik.get();

                Donacija novaDonacijaEntiteta = new Donacija();
                novaDonacijaEntiteta.setZnesekDonacije(novaDonacija.getZnesekDonacije());
                novaDonacijaEntiteta = donacijaDao.save(novaDonacijaEntiteta);

                uporabnik.dodajDonacijo(novaDonacijaEntiteta);
                uporabnikDao.save(uporabnik);

                byte[] pdfBytes = generateDonationPdf(uporabnik, novaDonacijaEntiteta);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "RacunDonacije.pdf");
                headers.setContentLength(pdfBytes.length);

                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    private byte[] generateDonationPdf(Uporabnik uporabnik, Donacija donacija) throws IOException, DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        try{
            Path path = Paths.get(ClassLoader.getSystemResource("aux1.png").toURI());
            Image img = Image.getInstance(path.toAbsolutePath().toString());
            img.setAbsolutePosition(150, 400);
            img.scaleAbsolute(300, 250);
            document.add(img);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk("Racun donacije\n\n", font);
        document.add(chunk);

        document.add(new Paragraph("Uporabnik: " + uporabnik.getIme() + " " + uporabnik.getPriimek()));
        document.add(new Paragraph("Znesek donacije: " + donacija.getZnesekDonacije() + "EUR"));

        document.close();

        return byteArrayOutputStream.toByteArray();
    }
    

    @DeleteMapping("/uporabniki/{ime}")
    public ResponseEntity<String> odstraniUporabnikePoImenu(@PathVariable String ime) {
        List<Uporabnik> uporabniki = uporabnikDao.findByIme(ime);
        if (!uporabniki.isEmpty()) {
            uporabnikDao.deleteAll(uporabniki);
            return ResponseEntity.ok("Uporabniki z imenom " + ime + " uspešno odstranjeni.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/{id}")
    public ResponseEntity<Uporabnik> urediPodatkeUporabnika(@PathVariable Long id, @RequestBody Uporabnik posodobljenUporabnik) {
        Uporabnik obstojecUporabnik = uporabnikDao.findById(id).orElse(null);

        if (obstojecUporabnik != null) {
            if (posodobljenUporabnik.getIme() != null) {
                obstojecUporabnik.setIme(posodobljenUporabnik.getIme());
            }
            if (posodobljenUporabnik.getPriimek() != null) {
                obstojecUporabnik.setPriimek(posodobljenUporabnik.getPriimek());
            }
            if (posodobljenUporabnik.getUporabniskoIme() != null) {
                obstojecUporabnik.setUporabniskoIme(posodobljenUporabnik.getUporabniskoIme());
            }
            if (posodobljenUporabnik.getGeslo() != null) {
                obstojecUporabnik.setGeslo(posodobljenUporabnik.getGeslo());
            }
            if (posodobljenUporabnik.getNaslov() != null) {
                obstojecUporabnik.setNaslov(posodobljenUporabnik.getNaslov());
            }
            if (posodobljenUporabnik.getEmail() != null) {
                obstojecUporabnik.setEmail(posodobljenUporabnik.getEmail());
            }

            Uporabnik novUporabnik = uporabnikDao.save(obstojecUporabnik);
            return ResponseEntity.ok(novUporabnik);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/registracija")
    public ResponseEntity<String> registracijaUporabnika(@RequestBody Uporabnik novUporabnik) {
        try {
            List<Uporabnik> obstojecUporabnik = uporabnikDao.findByUporabniskoIme(novUporabnik.getUporabniskoIme());
            if (obstojecUporabnik.isEmpty()) {
                uporabnikDao.save(novUporabnik);
                //emailSendingService.testemail();
                emailSendingService.sendRegistrationEmail(novUporabnik); // Send registration email
                return ResponseEntity.ok(novUporabnik.getUporabniskoIme() + " uspešno registriran");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Uporabnik s tem uporabniškim imenom že obstaja " + novUporabnik.getUporabniskoIme());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Napaka pri registraciji uporabnika: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Uporabnik> prijavaUporabnika(@RequestBody Uporabnik prijavljenUporabnik) {
        try {
            if (prijavljenUporabnik.getUporabniskoIme() == null || prijavljenUporabnik.getGeslo() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            List<Uporabnik> uporabnik = uporabnikDao.findByUporabniskoIme(prijavljenUporabnik.getUporabniskoIme());

            if (uporabnik.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else if (!uporabnik.get(0).getGeslo().equals(prijavljenUporabnik.getGeslo())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            } else {
                // Return the user object
                return ResponseEntity.ok(uporabnik.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("veckratniDonatorji")
    public List<Uporabnik> pridobiVeckratneDonatorje() {
        return uporabnikDao.najdiUporabnikeKiSoDonirali();
    }


    @GetMapping("/DonatorjiNad10")
    public List<Uporabnik> pridobiDonatorjeZnesekVecjiOd10() {
        return uporabnikDao.findDonatorjiZnesekVecjiOd10();
    }

    @GetMapping("/DonatorjiNad100")
    public List<Uporabnik> pridobiDonatorjeZnesekVecjiOd100() {
        return uporabnikDao.findDonatorjiZnesekVecjiOd100();
    }

    @GetMapping("/DonatorjiNad1000")
    public List<Uporabnik> pridobiDonatorjeZnesekVecjiOd1000() {
        return uporabnikDao.findDonatorjiZnesekVecjiOd1000();
    }

    @GetMapping("/DonatorjiNad10000")
    public List<Uporabnik> pridobiDonatorjeZnesekVecjiOd10000() {
        return uporabnikDao.findDonatorjiZnesekVecjiOd10000();
    }

    @GetMapping("/oskodvanciVBlizini/{uporabnikId}")
    public ResponseEntity<List<Oskodovanec>> getOskodovanciBrezDonacijeInLokacijaPodobnaNaslovu(@PathVariable Long uporabnikId) {
        Optional<Uporabnik> uporabnikOpt = uporabnikDao.findById(uporabnikId);
        if (uporabnikOpt.isPresent()) {
            Uporabnik uporabnik = uporabnikOpt.get();

            String naslovUporabnika = uporabnik.getNaslov();

            List<Oskodovanec> oskodovanci = uporabnikDao.findOskodovanciBrezDonacijeInPodobnaLokacijaNesrece(naslovUporabnika);

            return ResponseEntity.ok(oskodovanci);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}