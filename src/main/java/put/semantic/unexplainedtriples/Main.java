package put.semantic.unexplainedtriples;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


class Triple {
    public final RDFNode s, p, o;

    public Triple(Statement stmt) {
        this(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
    }

    public Triple(RDFNode s, RDFNode p, RDFNode o) {
        this.s = s;
        this.p = p;
        this.o = o;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Triple) {
            Triple ot = (Triple) other;
            return Objects.deepEquals(new RDFNode[]{s, p, o}, new RDFNode[]{ot.s, ot.p, ot.o});
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(s, p, o);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s .", s, p, o);
    }
}

public class Main {

    private static final String[] rules;

    static {
        List<String> tmp = new ArrayList<>();
        try {
            for (File f : new File("src/main/resources/rules").listFiles((file, s) -> s.endsWith(".sparql"))) {
                String queryString = new String(Files.readAllBytes(Paths.get(f.getPath())), StandardCharsets.UTF_8);
                tmp.add(queryString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        rules = tmp.toArray(new String[0]);
    }

    private Set<Triple> getTriples(Model model, String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            String[] vars = results.getResultVars().toArray(new String[0]);
            Set<Triple> triples = new HashSet<>();
            while (results.hasNext()) {
                QuerySolution s = results.nextSolution();
                Triple t = new Triple(s.get(vars[0]), s.get(vars[1]), s.get(vars[2]));
                triples.add(t);
            }
            return triples;
        }
    }

    private Set<Triple> getUnexplainedTriples(Model model) {
        Set<Triple> result = new HashSet<>();
        StmtIterator i = model.listStatements();
        while (i.hasNext()) {
            result.add(new Triple(i.nextStatement()));
        }
        for (String rule : rules) {
            Set<Triple> triples = getTriples(model, rule);
            result.removeAll(triples);
        }
        return result;
    }

    public void real_main(String[] args) throws Exception {
        Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
        String fn = "src/main/resources/data.ttl";
        if (args.length >= 1)
            fn = args[0];
        model.read(new File(fn).toURI().toString(), null, "TURTLE");

        Set<Triple> triples = getUnexplainedTriples(model);
        for (Triple t : triples) {
            System.out.println(t);
        }
    }

    public static void main(String[] args) throws Exception {
        new Main().real_main(args);
    }
}
