package edu.mimuw.sovaide.service;

import static edu.mimuw.sovaide.constant.Constant.getLocalFilePath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.stereotype.Service;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;

import edu.mimuw.sovaide.domain.model.Entity;
import edu.mimuw.sovaide.domain.model.EntityKind;
import edu.mimuw.sovaide.domain.model.File;
import edu.mimuw.sovaide.domain.model.FileKind;
import edu.mimuw.sovaide.domain.model.Member;
import edu.mimuw.sovaide.domain.model.MemberKind;
import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JarParseService {

	private final ProjectRepository projectRepository;

	public void parse(Project project) {
		String localFilePath = getLocalFilePath(project.getId(), project.getFileUrl());
		List<File> allFiles = new ArrayList<>();

		try (JarFile jarFile = new JarFile(localFilePath)) {
			log.info("Parsing JAR file: {}", localFilePath);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory()) continue;
				String entryName = entry.getName();
				String content;
				try (InputStream is = jarFile.getInputStream(entry)) {
					content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
					FileKind kind = readFileKind(entryName);
					List<Entity> entities = findEntities(entryName, content, kind);
					File file = new File();
					file.setKind(kind);
					file.setPath(entryName);
					file.setContent(content);
					file.setEntities(entities);
					allFiles.add(file);
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		project.setFiles(allFiles);
		projectRepository.save(project);
	}

	private List<Entity> findEntities(String entryName, String content, FileKind kind) {
		if (kind == FileKind.SOURCE_FILE) {
			try {
				final String cont = content;
				CompilationUnit unit = StaticJavaParser.parse(cont);

//				EntityKind entityKind;
//				unit.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> {
//					if (c.isInterface()) {
//						entityKind = EntityKind.INTERFACE;
//					} else {
//						entityKind = EntityKind.CLASS;
//					}
//				});

				List<TypeDeclaration<?>> types = unit.getTypes().stream()
						.filter(TypeDeclaration::isClassOrInterfaceDeclaration)
						.toList();

				return types.stream().map(t -> {
					List<Member> members = t.getMembers().stream().map(member -> {
							MemberKind memberKind;
							if (member.isFieldDeclaration()) {
								memberKind = MemberKind.FIELD;
							} else if (member.isMethodDeclaration()) {
								memberKind = MemberKind.METHOD;
							} else if (member.isConstructorDeclaration()) {
								memberKind = MemberKind.CONSTRUCTOR;
							} else {
								memberKind = MemberKind.OTHER;
							}
							Member memberObj = new Member();
							memberObj.setContent(member.toString());
							memberObj.setKind(memberKind);
							return memberObj;
						}).toList();

					Entity entity = new Entity();
					entity.setKind(EntityKind.CLASS); // todo support different types than classes
					entity.setContent(content);
					entity.setName(t.getNameAsString());
					entity.setMembers(members);
					return entity;
				}).toList();
			} catch (ParseProblemException e) {
				log.error("Error when parsing {}: {}", entryName, e.getMessage());
			}
		}
		return List.of();
	}

	private FileKind readFileKind(String entryName) {
		if (entryName.endsWith(".java")) {
			return FileKind.SOURCE_FILE;
		} else if (entryName.endsWith("Test.java")) {
			return FileKind.TEST_FILE;
		} else if (entryName.startsWith("resources/") ||
				entryName.contains("/resources/") ||
				isResourceFile(entryName)) {
			return FileKind.RESOURCE;
		}
		return FileKind.OTHER;
	}

	private boolean isResourceFile(String fileName) {
		return fileName.matches(".+\\.(properties|xml|yml|yaml|json|txt|css|js|html|jsp|sql|md)$") ||
				fileName.matches("^META-INF/.+") ||
				fileName.matches("^static/.+") ||
				fileName.matches("^templates/.+");
	}
}
