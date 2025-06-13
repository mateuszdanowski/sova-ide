package edu.mimuw.sovaide.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(ALWAYS)
@Table(name = "projects")
public class Project {
	@Id
	@UuidGenerator
	@Column(name = "id", unique = true, updatable = false)
	private String id;
	private String name;
	private String fileUrl;
	private String status = "NOT ANALYZED";
	private Long noOfClasses;
	private Long noOfImports;
	private Long avgLinesOfCode;
}
