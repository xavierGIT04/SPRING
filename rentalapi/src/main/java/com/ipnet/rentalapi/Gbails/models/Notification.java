package com.ipnet.rentalapi.Gbails.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ipnet.rentalapi.Gbails.Enums.TypeNotification;
import com.ipnet.rentalapi.auth.model.Utilisateur;


@Entity
@Table(name = "notifications")
public class Notification {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_notification", nullable = false)
    private TypeNotification typeNotification;

    @Column(name = "est_lu", nullable = false)
    private boolean estLu = false;

    // RELATIONS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locataire_id", nullable = false)
    private Utilisateur locataire; // Le locataire qui reçoit la notification

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getDateEnvoi() {
		return dateEnvoi;
	}

	public void setDateEnvoi(LocalDateTime dateEnvoi) {
		this.dateEnvoi = dateEnvoi;
	}

	public TypeNotification getTypeNotification() {
		return typeNotification;
	}

	public void setTypeNotification(TypeNotification typeNotification) {
		this.typeNotification = typeNotification;
	}

	public boolean isEstLu() {
		return estLu;
	}

	public void setEstLu(boolean estLu) {
		this.estLu = estLu;
	}

	public Utilisateur getLocataire() {
		return locataire;
	}

	public void setLocataire(Utilisateur locataire) {
		this.locataire = locataire;
	}

	public Notification() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}