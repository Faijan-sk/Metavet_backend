package com.example.demo.Dto;

public class GroomerServiceResDto {
	
	 private String uid;
	    private String serviceName;
	    private String groomerKyc;
	    private Integer durationMinutes;
	    private Double price;
	    private String description;
	    private Boolean isActive;
		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
		}
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		public String getGroomerKyc() {
			return groomerKyc;
		}
		public void setGroomerKyc(String groomerKyc) {
			this.groomerKyc = groomerKyc;
		}
		public Integer getDurationMinutes() {
			return durationMinutes;
		}
		public void setDurationMinutes(Integer durationMinutes) {
			this.durationMinutes = durationMinutes;
		}
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Boolean getIsActive() {
			return isActive;
		}
		public void setIsActive(Boolean isActive) {
			this.isActive = isActive;
		}

	    
	    

}
