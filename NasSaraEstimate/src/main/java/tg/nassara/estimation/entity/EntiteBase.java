package tg.nassara.estimation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/** Socle commun : identifiant, horodatage et verrouillage optimiste. */
@MappedSuperclass
public abstract class EntiteBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dateCreation;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant dateModification;

    @Version
    private Long version;

    public Long getId() { return id; }
    public Instant getDateCreation() { return dateCreation; }
    public Instant getDateModification() { return dateModification; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object autre) {
        if (this == autre) return true;
        if (!(autre instanceof EntiteBase that)) return false;
        return id != null
                && id.equals(that.id)
                && Hibernate.getClass(this) == Hibernate.getClass(that);
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}