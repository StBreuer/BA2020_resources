package org.semanticweb.rulewerk.examples.clingo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.owlapi.OwlToRulesConverter;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.clingo.ClingoReasoner;
import org.semanticweb.rulewerk.reasoner.clingo.CSVloader;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

import com.google.common.base.Stopwatch;

public class ClingoVsVlog {
	public static void main(String[] args) throws OWLOntologyCreationException, IOException {
		Map<String, List<Long>> timesMap = new HashMap<String, List<Long>>();
			runVLogBenchmark1(timesMap, "VLog1 ");
			runVLogBenchmark2(timesMap, "VLog2 ");
			runClingoBenchmark1(timesMap, "Clingo1 " );
			runClingoBenchmark2(timesMap, "Clingo2 ");
			runVLogBenchmark1InMemory(timesMap, "VLog1 InMemory");
			runVLogBenchmark2InMemory(timesMap, "VLog2 InMemory");
		timesMap.forEach((k,v)->{
			System.out.println("--------");
			System.out.println("KEY");
			System.out.println(k);
			System.out.println("Values");
			for(Long time : v) {
				System.out.println(time);
			}
			System.out.println("--------");

		});
	}
	
	private static void runClingoBenchmark1(Map<String, List<Long>> timesMap, String run) throws IOException, OWLOntologyCreationException {
		Stopwatch loadfile = Stopwatch.createUnstarted();
		Stopwatch load = Stopwatch.createUnstarted();
		Stopwatch reason = Stopwatch.createUnstarted();
		Stopwatch query = Stopwatch.createUnstarted();
		Stopwatch holeRun = Stopwatch.createStarted();
		System.out.println("Start hole run");
		
		KnowledgeBase kb = new KnowledgeBase();

		loadfile.start();
		// add CSV Facts
		CSVloader loader = new CSVloader(kb);
		loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1/");
		System.out.println("lubm-S1 loaded");
		//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2/");
		//System.out.println("lubm-S2 loaded");
		
		
		//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S3-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S3/");
		//System.out.println("lubm-S3 loaded");
		//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S4-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S4/");
		//System.out.println("lubm-S4 loaded");
		
		/*
		// add TTL Rules
		final URL turtleURL = new URL(LoadRDF.INPUT_FOLDER + "TBox/");
		*/
		
		//add OWL Rules
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(new File(ExamplesUtils.INPUT_FOLDER + "TBox/lubm.owl"));
		
		final OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter();
		owlToRulesConverter.addOntology(ontology);
		
		loadfile.stop();
		ClingoReasoner reasoner = new ClingoReasoner(kb);
		// useless now reasoner.load();
		
		//load
		System.out.println("load time started");
		load.start();
		reasoner.load();
		load.stop();
		
		//reason
		System.out.println("reason time started");
		reason.start();
		reasoner.runClingo();
		reason.stop();
		
		//quering
		System.out.println("quering started");
		query.start();
		List<PositiveLiteral> literals = reasoner.getPosLitToQuery();
		
		for(PositiveLiteral literal : literals) {
			QueryResultIterator iterator = reasoner.answerQuery(literal, false);
			
			while(iterator.hasNext()) {
				iterator.next();
			}
			iterator.close();
		}
		query.stop();
		
		
		holeRun.stop();
		System.out.println();
		reasoner.close();
		System.out.println(run);
		System.out.println("Hole time elapsed: " + holeRun.elapsed(TimeUnit.MILLISECONDS));
		System.out.println("load time elapsed: " + load.elapsed(TimeUnit.MILLISECONDS));
		System.out.println("reasoning Time elapsed: " + reason.elapsed(TimeUnit.MILLISECONDS));
		System.out.println("query time elapsed: " + query.elapsed(TimeUnit.MILLISECONDS));
		List<Long> times = new ArrayList<Long>();
		times.add(holeRun.elapsed(TimeUnit.MILLISECONDS));
		times.add(loadfile.elapsed(TimeUnit.MILLISECONDS));
		times.add(load.elapsed(TimeUnit.MILLISECONDS));
		times.add(reason.elapsed(TimeUnit.MILLISECONDS));
		times.add(query.elapsed(TimeUnit.MILLISECONDS));
		timesMap.put(run, times);
		System.out.println("ready");
		
		
	}
private static void runClingoBenchmark2(Map<String, List<Long>> timesMap, String run) throws IOException, OWLOntologyCreationException {
		Stopwatch loadfile = Stopwatch.createUnstarted();
		Stopwatch load = Stopwatch.createUnstarted();
		Stopwatch reason = Stopwatch.createUnstarted();
		Stopwatch query = Stopwatch.createUnstarted();
		Stopwatch holeRun = Stopwatch.createStarted();
		System.out.println("Start hole run");
		
		KnowledgeBase kb = new KnowledgeBase();

		loadfile.start();
		// add CSV Facts
		CSVloader loader = new CSVloader(kb);
		//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1/");
		//System.out.println("lubm-S1 loaded");
		loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2/");
		System.out.println("lubm-S2 loaded");
		
		
		//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S3-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S3/");
		//System.out.println("lubm-S3 loaded");
		//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S4-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S4/");
		//System.out.println("lubm-S4 loaded");
		
		/*
		// add TTL Rulesrea
		final URL turtleURL = new URL(LoadRDF.INPUT_FOLDER + "TBox/");
		*/
		
		//add OWL Rules
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(new File(ExamplesUtils.INPUT_FOLDER + "TBox/lubm.owl"));
		
		final OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter();
		owlToRulesConverter.addOntology(ontology);
		
		kb.addStatements(new ArrayList<>(owlToRulesConverter.getRules()));
		
		
		loadfile.stop();
		ClingoReasoner reasoner = new ClingoReasoner(kb);
		// useless now reasoner.load();
		
		//load
		System.out.println("load time started");
		load.start();
		reasoner.load();
		load.stop();
		
		//reason
		System.out.println("reason time started");
		reason.start();
		reasoner.runClingo();
		reason.stop();
		
		//quering
		System.out.println("quering started");
		query.start();
		List<PositiveLiteral> literals = reasoner.getPosLitToQuery();
		
		for(PositiveLiteral literal : literals) {
			QueryResultIterator iterator = reasoner.answerQuery(literal, false);
			
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
		holeRun.stop();
		System.out.println(run);

		List<Long> times = new ArrayList<Long>();
		times.add(holeRun.elapsed(TimeUnit.MILLISECONDS));
		times.add(loadfile.elapsed(TimeUnit.MILLISECONDS));
		times.add(load.elapsed(TimeUnit.MILLISECONDS));
		times.add(reason.elapsed(TimeUnit.MILLISECONDS));
		times.add(query.elapsed(TimeUnit.MILLISECONDS));
		System.out.println("ready");
		timesMap.put(run, times);
		
	}
	

private static void runVLogBenchmark1(Map<String, List<Long>> timesMap, String run)throws OWLOntologyCreationException, IOException {
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
	//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2/");
	//System.out.println("lubm-S2 loaded");
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
	reasoner.setLogFile(null);
	reasoner.setLogLevel(LogLevel.INFO);
	//reasoner.reason();
	//load
	System.out.println("loading started");
	load.start();
	reasoner.load();
	load.stop();
	
	//reason
	System.out.println("reasoning started");
	reason.start();
	reasoner.runChase();
	reason.stop();
	
	//reasoner.reason();
	//quering
	System.out.println("quering started");
	query.start();
	
	Set<Predicate> predicates = reasoner.getKnowledgeBasePredicates();

	for(Predicate predicate : predicates) {
		PositiveLiteral literal = reasoner.getQueryAtom(predicate);
		QueryResultIterator iterator = reasoner.answerQuery(literal, false);					
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
	holeRun.stop();
	reasoner.close();
	System.out.println(run);
	List<Long> times = new ArrayList<Long>();
	times.add(holeRun.elapsed(TimeUnit.MILLISECONDS));
	times.add(load.elapsed(TimeUnit.MILLISECONDS));
	times.add(reason.elapsed(TimeUnit.MILLISECONDS));
	times.add(query.elapsed(TimeUnit.MILLISECONDS));
	times.add(loadfile.elapsed(TimeUnit.MILLISECONDS));
	System.out.println("ready");
	timesMap.put(run, times);
	
}

private static void runVLogBenchmark2(Map<String, List<Long>> timesMap, String run)throws OWLOntologyCreationException, IOException {
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
	//loader.loadCSVsfromFolder(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1/");
	//System.out.println("lubm-S1 loaded");
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
	reasoner.setLogFile(null);
	reasoner.setLogLevel(LogLevel.INFO);
	System.out.println("loading started");
	load.start();
	reasoner.load();
	load.stop();
	
	//reason
	System.out.println("reasoning started");
	reason.start();
	reasoner.runChase();
	reason.stop();
	
	//quering
	System.out.println("quering started");
	query.start();
	
	Set<Predicate> predicates = reasoner.getKnowledgeBasePredicates();	
	for(Predicate predicate : predicates) {
		PositiveLiteral literal = reasoner.getQueryAtom(predicate);
		QueryResultIterator iterator = reasoner.answerQuery(literal, false);					
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
	holeRun.stop();
	System.out.println(run);
	List<Long> times = new ArrayList<Long>();
	times.add(holeRun.elapsed(TimeUnit.MILLISECONDS));
	times.add(load.elapsed(TimeUnit.MILLISECONDS));
	times.add(reason.elapsed(TimeUnit.MILLISECONDS));
	times.add(query.elapsed(TimeUnit.MILLISECONDS));
	times.add(loadfile.elapsed(TimeUnit.MILLISECONDS));
	System.out.println("ready");
	timesMap.put(run, times);
	
}

private static void runVLogBenchmark1InMemory(Map<String, List<Long>> timesMap, String run)throws OWLOntologyCreationException, IOException {
	
	Stopwatch load = Stopwatch.createUnstarted();
	Stopwatch reason = Stopwatch.createUnstarted();
	Stopwatch query = Stopwatch.createUnstarted();
	
	
	System.out.println("Start hole run");
	Stopwatch holeRun = Stopwatch.createStarted();
	System.out.println("loading started");
	load.start();
	KnowledgeBase uselesskb = new KnowledgeBase();
	CSVloader loader = new CSVloader(uselesskb);
	VLogReasoner reasoner = loader.loadCSVsfromFolderTest(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S1/");
	
	KnowledgeBase kb = reasoner.getKnowledgeBase();
	//add OWL Rules
	final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
	final OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(new File(ExamplesUtils.INPUT_FOLDER + "TBox/lubm.owl"));		final OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter();
	owlToRulesConverter.addOntology(ontology);
	kb.addStatements(new ArrayList<>(owlToRulesConverter.getRules()));
	
	System.out.println(kb.getRules().size());
	System.out.println(kb.getFacts().size());
	System.out.println(kb.getDataSourceDeclarations().size());
	reasoner.setLogFile(null);
	reasoner.setLogLevel(LogLevel.INFO);
	System.out.println("lubm-S1 loaded");
	
	reasoner.load();
	load.stop();
	
	
	//reason
		System.out.println("reasoning started");
		reason.start();
		//reasoner.reason();
		reasoner.runChase();
		reason.stop();
		
		//quering
		System.out.println("quering started");
		query.start();
		
		Set<Predicate> predicates = reasoner.getKnowledgeBasePredicates();
		System.out.println(predicates.size());
		for(Predicate predicate : predicates) {
			PositiveLiteral literal = reasoner.getQueryAtom(predicate);
			QueryResultIterator iterator = reasoner.answerQuery(literal, false);					
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
		holeRun.stop();
		System.out.println(run);
		List<Long> times = new ArrayList<Long>();
		times.add(holeRun.elapsed(TimeUnit.MILLISECONDS));
		times.add(load.elapsed(TimeUnit.MILLISECONDS));
		times.add(reason.elapsed(TimeUnit.MILLISECONDS));
		times.add(query.elapsed(TimeUnit.MILLISECONDS));
		//times.add(loadfile.elapsed(TimeUnit.MILLISECONDS));
		System.out.println("ready");
		timesMap.put(run, times);
	
	
		
}
private static void runVLogBenchmark2InMemory(Map<String, List<Long>> timesMap, String run)throws OWLOntologyCreationException, IOException {
	
	Stopwatch load = Stopwatch.createUnstarted();
	Stopwatch reason = Stopwatch.createUnstarted();
	Stopwatch query = Stopwatch.createUnstarted();
	
	
	System.out.println("Start hole run");
	Stopwatch holeRun = Stopwatch.createStarted();
	System.out.println("loading started");
	load.start();
	KnowledgeBase uselesskb = new KnowledgeBase();
	CSVloader loader = new CSVloader(uselesskb);
	VLogReasoner reasoner = loader.loadCSVsfromFolderTest(ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2-edb-config-info.txt", ExamplesUtils.INPUT_FOLDER + "ABox/lubm/lubm-S2/");
	System.out.println("lubm-S2 loaded");
	
	KnowledgeBase kb = reasoner.getKnowledgeBase();
	//add OWL Rules
	final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
	final OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(new File(ExamplesUtils.INPUT_FOLDER + "TBox/lubm.owl"));		final OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter();
	owlToRulesConverter.addOntology(ontology);
	kb.addStatements(new ArrayList<>(owlToRulesConverter.getRules()));
	
	System.out.println(kb.getRules().size());
	System.out.println(kb.getFacts().size());
	System.out.println(kb.getDataSourceDeclarations().size());
	reasoner.setLogFile(null);
	reasoner.setLogLevel(LogLevel.INFO);
	System.out.println("lubm-S1 loaded");
	
	reasoner.load();
	load.stop();
	
	
	//reason
		System.out.println("reasoning started");
		reason.start();
		//reasoner.reason();
		reasoner.runChase();
		reason.stop();
		
		//quering
		System.out.println("quering started");
		query.start();
		
		Set<Predicate> predicates = reasoner.getKnowledgeBasePredicates();
		System.out.println(predicates.size());
		for(Predicate predicate : predicates) {
			PositiveLiteral literal = reasoner.getQueryAtom(predicate);
			QueryResultIterator iterator = reasoner.answerQuery(literal, false);					
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
		holeRun.stop();
		System.out.println(run);
		List<Long> times = new ArrayList<Long>();
		times.add(holeRun.elapsed(TimeUnit.MILLISECONDS));
		times.add(load.elapsed(TimeUnit.MILLISECONDS));
		times.add(reason.elapsed(TimeUnit.MILLISECONDS));
		times.add(query.elapsed(TimeUnit.MILLISECONDS));
		//times.add(loadfile.elapsed(TimeUnit.MILLISECONDS));
		System.out.println("ready");
		timesMap.put(run, times);
	
	
		
}

}
