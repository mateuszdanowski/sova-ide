package edu.mimuw.sovaide.domain.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(ALWAYS)
public class Project {
	private String id;
	private String name;
	private String fileUrl;
	private String status = "NOT ANALYZED";
	private Long noOfClasses;
	private Long noOfImports;
	private Long avgLinesOfCode;
}
