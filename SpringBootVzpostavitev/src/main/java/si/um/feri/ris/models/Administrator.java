package si.um.feri.ris.models;
import jakarta.persistence.*;
import si.um.feri.ris.models.Nesreca;
import si.um.feri.ris.models.Oskodovanec;
import java.util.List;
@Entity
public class Administrator {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String uporabniskoIme;
	private String geslo;

	@OneToMany(mappedBy = "administrator")
	private List<Nesreca> nesrece;

	@OneToMany(mappedBy = "administrator")
	private List<Oskodovanec> oskodovanci;

	public String getUporabniskoIme() {
		return uporabniskoIme;
	}

	public void setUporabniskoIme(String uporabniskoIme) {
		this.uporabniskoIme = uporabniskoIme;
	}

	public String getGeslo() {
		return geslo;
	}

	public void setGeslo(String geslo) {
		this.geslo = geslo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Administrator{" +
				"id=" + id +
				", uporabniskoIme='" + uporabniskoIme + '\'' +
				", geslo='" + geslo + '\'' +
				'}';
	}
}