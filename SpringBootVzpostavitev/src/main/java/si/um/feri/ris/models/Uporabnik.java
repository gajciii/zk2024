package si.um.feri.ris.models;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import si.um.feri.ris.repository.ListNesrec;

import java.util.List;
@Entity
public class Uporabnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToMany
    @JoinTable(
            name = "Uporabnik_Donacija",
            joinColumns = @JoinColumn(name = "uporabnik_id"),
            inverseJoinColumns = @JoinColumn(name = "donacija_id")
    )
    List<Donacija> donacije;


    private String ime;
    private String uporabniskoIme;
    private String geslo;
    private String priimek;
    private String email;
    private String naslov;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uporabnik(String uporabniskoIme, String ime, String priimek) {
        this.uporabniskoIme = uporabniskoIme;
        this.ime = ime;
        this.priimek = priimek;
    }

    public Uporabnik() {

    }


    public void dodajDonacijo(Donacija donacija) {
        this.donacije.add(donacija);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }
}