# UnexplainedTriples
Discovering RDF triples unexplained by an ontology

# Compilation and usage

mvn package
java -jar target/UnexplainedTriples-1.0-SNAPSHOT.jar data.ttl

data.ttl is an ontology along with instances (i.e., TBox and ABox), which is to be used. There is a sample in [src/main/resources/data.ttl](src/main/resources/data.ttl)

# Rules

The SPARQL inference rules are in [src/main/resources/rules](src/main/resources/rules). They are based on the rules defined in the OWL 2 RL specification https://www.w3.org/TR/owl2-profiles/#Reasoning_in_OWL_2_RL_and_RDF_Graphs_using_Rules and most of them is straightforward except:

* [cls-int1.sparql](src/main/resources/rules/cls-int1.sparql) and [prp-key.sparql](src/main/resources/rules/prp-key.sparql), which use aggregate functions to deal with premises of variable size
* [prp-spo2.sparql](src/main/resources/rules/prp-spo2.sparql) which supports property chains up to 5 properties - adding more is straightforward, and it should be generated in the Java code depending on the property chains in the ontology at hand.
