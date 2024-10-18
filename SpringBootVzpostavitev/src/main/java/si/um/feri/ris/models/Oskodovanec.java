package si.um.feri.ris.models;
import jakarta.persistence.*;
import si.um.feri.ris.repository.PregledOskodovancev;

import java.util.List;

@Entity
public class Oskodovanec {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToMany
	@JoinTable(
			name = "Oskodovanec_Nesreca",
			joinColumns = @JoinColumn(name = "oskodovanec_id"),
			inverseJoinColumns = @JoinColumn(name = "nesreca_id")
	)
	List<Nesreca> nesrece;



	@ManyToMany
	@JoinTable(
			name = "Oskodovanec_Donacija",
			joinColumns = @JoinColumn(name = "oskodovanec_id"),
			inverseJoinColumns = @JoinColumn(name = "donacija_id")
	)
	List<Donacija> donacije;

	@ManyToOne
	@JoinColumn(name = "administrator_id")
	private Administrator administrator;

	private String ime;
	private String priimek;

	private boolean imaDruzino;
	private boolean mocnejePoskodovan;

	public boolean isImaDruzino() {
		return imaDruzino;
	}

	public void setImaDruzino(boolean imaDruzino) {
		this.imaDruzino = imaDruzino;
	}

	public boolean isMocnejePoskodovan() {
		return mocnejePoskodovan;
	}

	public void setMocnejePoskodovan(boolean mocnejePoskodovan) {
		this.mocnejePoskodovan = mocnejePoskodovan;
	}



	public Oskodovanec() {
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPriimek() {
		return priimek;
	}

	public void setPriimek(String priimek) {
		this.priimek = priimek;
	}

	public void dodajDonacijo(Donacija donacija) {
	}
}