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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "POSTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Posts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pid;

	@NotBlank(message = "Title field must be required ! ! ")
	@Size(min = 5, max = 40, message = "min 5 and max 40 characters are allowed ! ! ")
	private String title;

	@NotBlank(message = "Content field must be required ! ! ")
	@Size(min = 20, max = 3000, message = "min 20 and max 3000 characters are allowed ! ! ")
	private String content;

	private String code;

	private String category;

	@NotNull
	private String pic;

	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime pdate;

	@ManyToOne
	@JoinColumn(name = "uid")
	private User user;

}
