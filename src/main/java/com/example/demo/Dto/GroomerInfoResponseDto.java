package com.example.demo.Dto;

import java.time.LocalTime;

public class GroomerInfoResponseDto {

	 private String groomerUid;
	    private String groomerName;
	    private String email;
	    private String phone;
	    private ScheduleDTO scheduleForDay;
	    
	    public static class ScheduleDTO {
	        private Integer dayOfWeek;
	        private String dayName;
	        private LocalTime startTime;
	        private LocalTime endTime;

	        public Integer getDayOfWeek() {
	            return dayOfWeek;
	        }

	        public void setDayOfWeek(Integer dayOfWeek) {
	            this.dayOfWeek = dayOfWeek;
	        }

	        public String getDayName() {
	            return dayName;
	        }

	        public void setDayName(String dayName) {
	            this.dayName = dayName;
	        }

	        public LocalTime getStartTime() {
	            return startTime;
	        }

	        public void setStartTime(LocalTime startTime) {
	            this.startTime = startTime;
	        }

	        public LocalTime getEndTime() {
	            return endTime;
	        }

	        public void setEndTime(LocalTime endTime) {
	            this.endTime = endTime;
	        }
	    }

		public String getGroomerUid() {
			return groomerUid;
		}

		public void setGroomerUid(String groomerUid) {
			this.groomerUid = groomerUid;
		}

		public String getGroomerName() {
			return groomerName;
		}

		public void setGroomerName(String groomerName) {
			this.groomerName = groomerName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public ScheduleDTO getScheduleForDay() {
			return scheduleForDay;
		}

		public void setScheduleForDay(ScheduleDTO scheduleForDay) {
			this.scheduleForDay = scheduleForDay;
		}
	    
	    
}
