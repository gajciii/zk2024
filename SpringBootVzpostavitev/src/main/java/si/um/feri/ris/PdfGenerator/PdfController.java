package si.um.feri.ris.PdfGenerator;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfController {

    @GetMapping("/generate-pdf")
    public void generatePdf(HttpServletResponse response) throws IOException, DocumentException, URISyntaxException {

       // response.setContentType("application/pdf");
       // response.setHeader("Content-Disposition", "inline; filename=example.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("iPdfGeneriran.pdf"));

        document.open();
        //Vstavljanje slike
        Path imagePath = Paths.get(ClassLoader.getSystemResource("Java_logo.png").toURI());
        Image img = Image.getInstance(((Path) imagePath).toAbsolutePath().toString());
        document.add(img);

        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk("Hello World", font);
        document.add(chunk);
        document.close();


    }


}
