package edu.mimuw.sovaide.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
	private String id;
	private String name;
//	private String fileUrl;
//	private String status = "NOT ANALYZED";
//	private Long noOfClasses;
//	private Long noOfImports;
//	private Long avgLinesOfCode;
	private List<File> files;
}
