module backend {

	requires org.neo4j.cypherdsl.core;

	requires spring.data.commons;
	requires spring.data.neo4j;

	opens edu.mimuw.sovaide to spring.core;

	exports edu.mimuw.sovaide;
}
