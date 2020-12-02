package org.semanticweb.rulewerk.examples.clingo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.owlapi.OwlToRulesConverter;
import org.semanticweb.rulewerk.reasoner.clingo.CSVloader;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

import com.google.common.base.Stopwatch;


public class VLogTest {
	public static void main(String[] args) throws OWLOntologyCreationException, IOException{
		Stopwatch loadfile = Stopwatch.createUnstarted();
		Stopwatch load = Stopwatch.createUnstarted();
		Stopwatch reason = Stopwatch.createUnstarted();
		Stopwatch query = Stopwatch.createUnstarted();
		
		
		System.out.println("Start hole run");
		Stopwatch holeRun = Stopwatch.createStarted();
		
		KnowledgeBase kb = new KnowledgeBase();

		
		//loadFiles
		System.out.println("load files started");
		loadfile.start();
		// add CSV Facts
		CSVloader loader = new CSVloader(kb);
		loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1/");
		System.out.println("lubm-S1 loaded");
		loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2/");
		System.out.println("lubm-S2 loaded");
		//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S3-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S3/");
		//System.out.println("lubm-S3 loaded");
		//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S4-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S4/");
		//System.out.println("lubm-S4 loaded");
		
		
		//add OWL Rules
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(new File(ExamplesUtils.INPUT_FOLDER + "TBox/lubm.owl"));
		final OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter();
		owlToRulesConverter.addOntology(ontology);
		kb.addStatements(new ArrayList<>(owlToRulesConverter.getRules()));
		loadfile.stop();
		
		
		//Reasoning
		VLogReasoner reasoner = new VLogReasoner(kb);
		//reasoner.reason();
		
		System.out.println("loading started");
		load.start();
		reasoner.load();
		load.stop();
		
		System.out.println("reasoning started");
		reason.start();
		reasoner.runChase();
		reason.stop();
		//quering
		System.out.println("quering started");
		query.start();
		List<Fact> facts = reasoner.getKnowledgeBase().getFacts();		
		for(Fact fact : facts) {
			QueryResultIterator iterator = reasoner.answerQuery(fact, false);					
			while(iterator.hasNext()) {
				iterator.next();
						/*
						QueryResult result = iterator.next();
						List<Term> terms = result.getTerms();
						for(Term term : terms) {
							term.getName();
						}
						*/
			}
			iterator.close();
			}
		query.stop();
		reasoner.close();
	}

}
