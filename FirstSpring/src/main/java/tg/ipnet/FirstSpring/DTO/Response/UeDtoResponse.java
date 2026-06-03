package tg.ipnet.FirstSpring.DTO.Response;


public class UeDtoResponse {
	private Long Id;
	
	private String codeUe;
	
	private String intitule;
	
	private Integer creditECTS;
	
	private String typeUe;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getCodeUe() {
		return codeUe;
	}

	public void setCodeUe(String codeUe) {
		this.codeUe = codeUe;
	}

	public String getIntitule() {
		return intitule;
	}

	public void setIntitule(String intitule) {
		this.intitule = intitule;
	}

	public Integer getCreditECTS() {
		return creditECTS;
	}

	public void setCreditECTS(Integer creditECTS) {
		this.creditECTS = creditECTS;
	}

	public String getTypeUe() {
		return typeUe;
	}

	public void setTypeUe(String typeUe) {
		this.typeUe = typeUe;
	}
	
	
}
