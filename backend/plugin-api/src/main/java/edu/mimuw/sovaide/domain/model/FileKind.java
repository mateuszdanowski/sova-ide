package edu.mimuw.sovaide.domain.model;

/**
 * Specifies the supported kinds of files in a project, such as source, test, or resource files.
 */
public enum FileKind {
	SOURCE_FILE,
	TEST_FILE,
	RESOURCE,
	LOG_FILE,
	OTHER
}
