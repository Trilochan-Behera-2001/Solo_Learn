package com.solo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COMMENTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cid;
	@NotBlank(message = "About field must be required ! ! ")
	@Size(min = 20, max = 3000, message = "min 20 and max 3000 characters are allowed ! ! ")
	private String answer;
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime cdate;
	@ManyToOne
	@JoinColumn(name = "pid")
	private Posts posts;
	@ManyToOne
	@JoinColumn(name = "uid")
	private User user;

}
