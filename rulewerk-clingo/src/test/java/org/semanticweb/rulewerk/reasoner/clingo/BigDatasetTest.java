package org.semanticweb.rulewerk.reasoner.clingo;

/*-
 * #%L
 * Rulewerk Clingo Reasoner Support
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import org.junit.Test;
import com.google.common.base.Stopwatch;
import org.openrdf.model.Model;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.core.*;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.rulewerk.owlapi.OwlToRulesConverter;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class BigDatasetTest {

	@Test
	public void VlogChemblS1() throws IOException, ParsingException {

		
		
		LoadRDF.configureLogging();
		//LOAD ABox
		final String chembl_S1_Class0 = ""
		+ "@source objProperty0[2] : load-csv(\"" + LoadRDF.INPUT_FOLDER + "ABox/chembl/chembl-S1/ObjProperty0.csv.gz\") .";

		final KnowledgeBase kb = new KnowledgeBase();

		try(final VLogReasoner reasoner =  new VLogReasoner(kb)){
			RuleParser.parseInto(kb, chembl_S1_Class0);
			reasoner.reason();
			LoadRDF.printOutQueryAnswers("objProperty0(?X, ?Y)", reasoner);
			
			
			


		}


		//final Model model = LoadRDF.parseFile(new File(LoadRDF.INPUT_FOLDER + ""));

	}
	
	@Test
	public void directoryLoad() throws IOException {
		KnowledgeBase kb = new KnowledgeBase();
		CSVloader loader = new CSVloader(kb);
		String[] files = null;
		try {
			files = loader.getFilesOfFolder(LoadRDF.INPUT_FOLDER + "ABox/chembl/chembl-S1/");
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i]);
		}
	}
	
	@Test
	public void loadChembkS1ABox() throws IOException, OWLOntologyCreationException {
		
		Stopwatch holeRun = Stopwatch.createStarted();
			
		
		KnowledgeBase kb = new KnowledgeBase();

		
		// add CSV Facts
		CSVloader loader = new CSVloader(kb);
		loader.loadCSVsfromFolder(LoadRDF.INPUT_FOLDER + "ABox/lubm/lubm-S1-edb-config-info.txt", LoadRDF.INPUT_FOLDER + "ABox/lubm/lubm-S1/");
		System.out.println("lubm-S1 loaded");
		loader.loadCSVsfromFolder(LoadRDF.INPUT_FOLDER + "ABox/lubm/lubm-S2-edb-config-info.txt", LoadRDF.INPUT_FOLDER + "ABox/lubm/lubm-S2/");
		System.out.println("lubm-S2 loaded");
		loader.loadCSVsfromFolder(LoadRDF.INPUT_FOLDER + "ABox/lubm/lubm-S3-edb-config-info.txt", LoadRDF.INPUT_FOLDER + "ABox/lubm/lubm-S3/");
		System.out.println("lubm-S3 loaded");
		loader.loadCSVsfromFolder(LoadRDF.INPUT_FOLDER + "ABox/lubm/lubm-S4-edb-config-info.txt", LoadRDF.INPUT_FOLDER + "ABox/lubm/lubm-S4/");
		System.out.println("lubm-S4 loaded");
		
		/*
		// add TTL Rules
		final URL turtleURL = new URL(LoadRDF.INPUT_FOLDER + "TBox/");
		*/
		
		//add OWL Rules
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(new File(LoadRDF.INPUT_FOLDER + "TBox/lubm.owl"));
		
		final OwlToRulesConverter owlToRulesConverter = new OwlToRulesConverter();
		owlToRulesConverter.addOntology(ontology);
		
		kb.addStatements(new ArrayList<>(owlToRulesConverter.getRules()));
		
		ClingoReasoner reasoner = new ClingoReasoner(kb);
		reasoner.load();
		
		Stopwatch reason = Stopwatch.createStarted();
		reasoner.runClingo();
		reason.stop();
		
		reasoner.close();
		
		reasoner.printAliase();
		
		holeRun.stop();
		
		System.out.println("Hole time elapsed: " + holeRun.elapsed(TimeUnit.MICROSECONDS));
		System.out.println("reasoning Time elapsed: " + reason.elapsed(TimeUnit.MICROSECONDS));
		System.out.println("ready");
		
	}

}
