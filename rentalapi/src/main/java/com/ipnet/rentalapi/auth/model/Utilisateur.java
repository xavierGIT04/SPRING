package com.ipnet.rentalapi.auth.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ipnet.rentalapi.Gbails.models.ContratBail;
import com.ipnet.rentalapi.Gbails.models.Notification;
import com.ipnet.rentalapi.Glogement.models.Bien;
import com.ipnet.rentalapi.auth.ProfilEnum;
import com.ipnet.rentalapi.auth.RoleEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Utilisateur implements UserDetails{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private UUID uuid = UUID.randomUUID();
	
	private String nomComplet;
	
	private String telephone;
	private String codeProprietaire;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private ProfilEnum profil;
	
	@Enumerated(EnumType.STRING)
	private RoleEnum role;
	
	private String avatar;
	
	@OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
	private List<Bien> biens = new ArrayList<Bien>();
	
	@OneToMany(mappedBy = "locataire", cascade = CascadeType.ALL)
	private List<ContratBail> bails = new ArrayList<ContratBail>();
	
	@OneToMany(mappedBy = "locataire", cascade = CascadeType.ALL)
	private List<Notification> notifications = new ArrayList<Notification>();
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {		
		return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
	}

	@Override
	public @Nullable String getPassword() {
		return codeProprietaire;
	}
	
	public void setPassword(String code) {
		this.codeProprietaire = code;
	}

	@Override
	public String getUsername() {
		return telephone;
	}
	
	public void setUsername(String telephone) {
		this.telephone = telephone;
	}

	@Override
	public boolean isAccountNonExpired() {return true;}
	
	@Override
	public boolean isAccountNonLocked() {return true;}
	
	@Override
	public boolean isCredentialsNonExpired() {return true;}
	
	@Override
	public boolean isEnabled() {return true;}
	
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

	public String getNomComplet() {
		return nomComplet;
	}

	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}

	public ProfilEnum getProfil() {
		return profil;
	}

	public void setProfil(ProfilEnum profil) {
		this.profil = profil;
	}

	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
		this.role = role;
	}

	public List<Bien> getBiens() {
		return biens;
	}

	public void setBiens(List<Bien> biens) {
		this.biens = biens;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public List<ContratBail> getBails() {
		return bails;
	}

	public void setBails(List<ContratBail> bails) {
		this.bails = bails;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}
	
	

}
