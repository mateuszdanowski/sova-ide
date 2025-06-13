package edu.mimuw.sovaide.service;

import static edu.mimuw.sovaide.constant.Constant.getLocalFilePath;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.computer.ComputerResult;
import org.apache.tinkerpop.gremlin.process.computer.ranking.pagerank.PageRankVertexProgram;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.stereotype.Service;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

import edu.mimuw.sovaide.domain.JavaClass;
import edu.mimuw.sovaide.domain.Project;
import edu.mimuw.sovaide.domain.graph.EdgeDTO;
import edu.mimuw.sovaide.domain.graph.GraphDTO;
import edu.mimuw.sovaide.domain.graph.VertexDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// todo separate to a plugin
@Service
@Slf4j
@RequiredArgsConstructor
public class GraphBuildingService {

	private final Map<String, Graph> graphs = new HashMap<>();

	public GraphDTO getGraph(String projectId) {
		Graph graph = graphs.get(projectId);
		if (graph == null) {
			throw new IllegalArgumentException("Graph not found for project: " + projectId);
		}

		GraphTraversalSource g = graph.traversal();
		Set<VertexDTO> vertices = g.V().has("kind", "class").toList().stream()
				.map(v -> new VertexDTO(
						v.property("name").value().toString(),
						((Number) v.property("metricLinesOfCode").value()).intValue(),
						((Number) v.property("page-rank").value()).doubleValue()
				))
						.collect(Collectors.toUnmodifiableSet());

		Set<EdgeDTO> edges = g.E().has("kind", "class").hasLabel("imports").toList().stream().map(v -> new EdgeDTO(v.inVertex().value("name"), v.outVertex().value("name"), "imports")).collect(
				Collectors.toUnmodifiableSet());

		return new GraphDTO(vertices, edges);
	}

	public GraphDTO getGraphPackages(String projectId) {
		Graph graph = graphs.get(projectId);
		if (graph == null) {
			throw new IllegalArgumentException("Graph not found for project: " + projectId);
		}

		GraphTraversalSource g = graph.traversal();
		Set<VertexDTO> vertices = g.V().has("kind", "package").values("name").toList().stream().map(v -> new VertexDTO(v.toString(), 0, -1.0))
				.collect(Collectors.toUnmodifiableSet());

		Set<EdgeDTO> edgesInPackage = new HashSet<>(
				g.E().has("kind", "package").hasLabel("in-package").toList().stream()
						.map(v -> new EdgeDTO(v.inVertex().value("name"), v.outVertex().value("name"), "in-package"))
						.collect(Collectors.toUnmodifiableSet()));

		Set<EdgeDTO> edgesPackageImports = g.E().has("kind", "package").hasLabel("package-imports").toList().stream().map(v -> new EdgeDTO(v.inVertex().value("name"), v.outVertex().value("name"), "package-imports"))
				.collect(Collectors.toUnmodifiableSet());

		edgesInPackage.addAll(edgesPackageImports);
		return new GraphDTO(vertices, edgesInPackage);
	}

	public GraphDTO getGraphPackageImports(String projectId) {
		Graph graph = graphs.get(projectId);
		if (graph == null) {
			throw new IllegalArgumentException("Graph not found for project: " + projectId);
		}

		GraphTraversalSource g = graph.traversal();
		Set<VertexDTO> vertices = g.V().has("kind", "package").values("name").toList().stream().map(v -> new VertexDTO(v.toString(), 0, -1.0)).collect(
				Collectors.toUnmodifiableSet());

		Set<EdgeDTO> edges = g.E().has("kind", "package").hasLabel("package-imports").toList().stream().map(v -> new EdgeDTO(v.inVertex().value("name"), v.outVertex().value("name"), "package-imports")).collect(
				Collectors.toUnmodifiableSet());

		return new GraphDTO(vertices, edges);
	}

	public List<JavaClass> fetchClasses(Project project) {
		String localFilePath = getLocalFilePath(project.getId(), project.getFileUrl());
		List<JavaClass> allClasses = new ArrayList<>();

		try (JarFile jarFile = new JarFile(localFilePath)) {
			log.info("Analysing JAR file: {}", localFilePath);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				// read entire entry into a String
				String content = ""; // tbd
				try (InputStream is = jarFile.getInputStream(entry)) {
					content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
					// 2) Decide if it's “Java”
					if (isJavaFile(entryName)) {
//						try (InputStream inputStream = jarFile.getInputStream(entry)) {
							try {
								final String cont = content;
								CompilationUnit unit = StaticJavaParser.parse(content);
								List<String> classNames = getClassNames(unit);
								List<String> imports = getImports(unit, new HashSet<>(classNames));
								String prefix = getPackagePrefix(unit);

								List<JavaClass> classes = classNames.stream()
										.map(className ->
												new JavaClass(
														prefix.isEmpty() ? className : prefix + "." + className,
														imports,
														cont))
										.toList();
								allClasses.addAll(classes);
							} catch (ParseProblemException e) {
								log.error("Error when parsing {}: {}", entryName, e.getMessage());
							}
//						}
					}
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return allClasses;
	}

	private String getPackagePrefix(CompilationUnit unit) {
		if (unit.getPackageDeclaration().isPresent()) {
			return unit.getPackageDeclaration().get().getNameAsString();
		} else {
			return "";
		}
	}

	private List<String> getImports(CompilationUnit unit, Set<String> classNames) {
		// unit.getImports()
		//		.forEach(s -> log.info("import: {}", s.getNameAsString()));
		return unit.getImports().stream()
				.filter(i -> !i.isStatic() && !i.isAsterisk())
				.map(NodeWithName::getNameAsString)
				.toList();
	}

	private List<String> getClassNames(CompilationUnit unit) {
		return unit.getTypes().stream()
				.filter(t -> t.getName() != null)
				.map(NodeWithSimpleName::getNameAsString)
				.toList();
	}

	private boolean isJavaFile(String entryName) {
		return entryName.endsWith(".java") && !entryName.endsWith("Test.java");
	}

	// there is probably some issue with closing this input stream by this method
	private String inputStreamToString(InputStream is) throws IOException {
		var builder = new StringBuilder();
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			try {
				var line = rd.readLine();
				while (line != null) {
					builder.append(line).append("\n");
					line = rd.readLine();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return builder.toString();
	}

	public void buildGraph(String projectId, List<JavaClass> classes) {
		Graph graph = TinkerGraph.open();
		GraphTraversalSource g = traversal().withEmbedded(graph);
		addClasses(g, classes);
		addImports(g, classes);
		addPackages(g);
		graphs.put(projectId, graph);
	}

	private void addPackages(GraphTraversalSource g) {
		List<Vertex> classes = getClasses(g);
		Set<String> packageNames = packagesFrom(classes);
		Map<String, Vertex> packageByName = addPackageVertices(g, packageNames);
		addPackageEdges(g, packageByName);
		addClassPackageEdges(g, classes, packageByName);
		addPackageImports(g);
		addPageRank(g);
	}

	private void addPageRank(GraphTraversalSource g) {
		 try {
			 ComputerResult result = g.getGraph().compute()
					 .program(PageRankVertexProgram.build()
							 .iterations(20)
							 .property("page-rank")
							 .create())
					 .submit()
					 .get();

			 // Copy page-rank property from computed graph back to original graph
			 result.graph().vertices().forEachRemaining(v -> {
				 Vertex original = g.V(v.id()).next();
				 Object pageRank = v.property("page-rank").value();
				 original.property("page-rank", pageRank);
				 // log.info("Vertex {}: PageRank = {}", original.value("name"), pageRank);
			 });
		 } catch (Exception e) {
			 log.error("Error during PageRank computation: {}", e.getMessage());
		 }
	}

	private void addPackageImports(GraphTraversalSource g) {
		// list of packages, which contain some class
		List<Vertex> packages = g.V().has("kind", "class")
				.out("in-package")
				.toList();

		packages.forEach(pkg -> {
			List<Vertex> importsPkg = g.V(pkg.id())
					.in("in-package")
					.out("imports")
					.out("in-package")
					.toList();
			// log.info("importsPackages for package {}: {}", pkg.value("name"), importsPkg);
			importsPkg.forEach(importsP -> {
				g.addE("package-imports")
						.property("kind", "package")
						.from(pkg)
						.to(importsP)
						.next();
			});
		});
	}

	private void addClassPackageEdges(GraphTraversalSource g, List<Vertex> classes, Map<String, Vertex> packageByName) {
		classes.forEach(cls -> {
			Vertex pkgVertex = packageByName.get(getPackageName(cls.value("name").toString()));
			g.addE("in-package")
					.property("kind", "class-package")
					.from(cls)
					.to(pkgVertex)
					.next();
		});
	}

	private void addPackageEdges(GraphTraversalSource g, Map<String, Vertex> packageByName) {
			packageByName.forEach((pkgName, pkgVertex) -> {
				if (!pkgName.isEmpty()) {
					var outerPackage = packageByName.get(getPackageName(pkgName));
					if (outerPackage == null) {
						log.error("Outer package not found for: {}", pkgName);
						return;
					}
					g.addE("in-package")
							.property("kind", "package")
							.from(pkgVertex)
							.to(outerPackage)
							.next();
				}
			});
	}
	private String getPackageName(String name) {
		if (name.contains(".")) {
			return name.substring(0, name.lastIndexOf('.'));
		} else {
			return "";
		}
	}

	private Map<String, Vertex> addPackageVertices(GraphTraversalSource g, Set<String> packageNames) {
		return packageNames.stream()
				.collect(Collectors.toMap(
						pkgName -> pkgName,
						pkgName -> {
							Vertex pkg = g.addV("package")
									.property("kind", "package")
									.property("name", pkgName)
									.next();
							return pkg;
						}
				));
	}

	public Set<String> packagesFrom(Iterable<Vertex> classes) {
		Set<String> packageNames = new HashSet<>();

		classes.forEach(cls -> {
			String clsName = cls.value("name").toString();

			// Split the class name by '.'
			List<String> parts = Arrays.asList(clsName.split("\\."));

			// Generate package names using initial segments
			for (int i = parts.size() - 1; i > 0; i--) {
				String pkgName = String.join(".", parts.subList(0, i));
				packageNames.add(pkgName);
			}
		});
		return packageNames;
	}

	private List<Vertex> getClasses(GraphTraversalSource g) {
		return g.V().has("kind", "class").toList();
	}

	private void addImports(GraphTraversalSource g, List<JavaClass> classes) {
		classes.forEach(javaClass -> {
			// log.info("class: {}", javaClass.name());
			// log.info("imports: {}", javaClass.imports());
			Vertex inV = g.V().has("class", "name", javaClass.name()).toList().getFirst();
			List<Vertex> outV = g.V().has("class", "name", P.within(javaClass.imports())).toList();

			// log.info("inV: {}", inV);
			// log.info("outV: {}", outV);

			outV.forEach(out -> g.addE("imports")
					.from(inV)
					.to(out)
					.property("kind", "class")
					.next());
		});
	}

	private void addClasses(GraphTraversalSource g, List<JavaClass> classes) {
		classes.forEach(javaClass -> {
			g.addV("class")
					.property("kind", "class")
					.property("name", javaClass.name())
					.property("source-code", javaClass.content())
					.property("metricLinesOfCode", javaClass.content().split("\n").length)
					.next();
		});
	}

	public void printGraphs() {
		graphs.forEach((name, graph) -> {
			log.info("Graph: {}", graph);
			graph.vertices().forEachRemaining(vertex -> {
				log.info("Vertex: {}", vertex.property("name").value());
			});
			graph.edges().forEachRemaining(edge -> {
				log.info("Edge from {} to {}", edge.inVertex().value("name"), edge.outVertex().value("name"));
			});
		});
	}
}
