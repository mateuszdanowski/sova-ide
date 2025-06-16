package edu.mimuw.sovaide.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Node("Project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(ALWAYS)
public class Project {
	@Id
	@GeneratedValue
	private String id;
	private String name;
	private String fileUrl;
	private String status = "NOT ANALYZED";
	private Long noOfClasses;
	private Long noOfImports;
	private Long avgLinesOfCode;
}
