package si.um.feri.ris.models;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

@Entity
public class Donacija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(mappedBy = "donacije", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Oskodovanec> oskodovanci;

    @ManyToMany(mappedBy = "donacije", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Uporabnik> uporabniki;

    private double znesekDonacije;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Oskodovanec> getOskodovanci() {
        return oskodovanci;
    }

    public void setOskodovanci(List<Oskodovanec> oskodovanci) {
        this.oskodovanci = oskodovanci;
    }

    public List<Uporabnik> getUporabniki() {
        return uporabniki;
    }

    public void setUporabniki(List<Uporabnik> uporabniki) {
        this.uporabniki = uporabniki;
    }

    public double getZnesekDonacije() {
        return znesekDonacije;
    }

    public void setZnesekDonacije(double znesekDonacije) {
        this.znesekDonacije = znesekDonacije;
    }

    @Override
    public String toString() {
        return "Donacija{" +
                "id=" + id +
                ", znesekDonacije=" + znesekDonacije +
                '}';
    }
}