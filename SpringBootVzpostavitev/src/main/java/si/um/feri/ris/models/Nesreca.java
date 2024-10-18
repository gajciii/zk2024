package si.um.feri.ris.models;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity
public class Nesreca{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToMany(mappedBy = "nesrece", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Oskodovanec> oskodovanci;

	@ManyToOne
	@JoinColumn(name = "administrator_id")
	private Administrator administrator;
	private Date datum;
	private String opis;
	private String lokacija;

	public Nesreca(Date datum, String opis, String lokacija) {
		this.datum = datum;
		this.opis = opis;
		this.lokacija = lokacija;
	}

	public Nesreca() {

	}

	public Date getDatum() {
		return this.datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public String getLokacija() {
		return this.lokacija;
	}

	public void setLokacija(String lokacija) {
		this.lokacija = lokacija;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getOpis() {
		return opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public Administrator getAdministrator() {
		return administrator;
	}

	public void setAdministrator(Administrator administrator) {
		this.administrator = administrator;
	}
}