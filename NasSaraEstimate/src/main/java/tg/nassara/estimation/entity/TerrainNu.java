package tg.nassara.estimation.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import tg.nassara.estimation.enums.NatureBien;

@Entity
@DiscriminatorValue("TERRAIN")
public class TerrainNu extends Bien {

    @Override
    public NatureBien nature() {
        return NatureBien.TERRAIN;
    }
}