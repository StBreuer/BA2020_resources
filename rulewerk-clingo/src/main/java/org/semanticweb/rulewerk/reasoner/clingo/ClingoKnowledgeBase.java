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

import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;

import java.util.*;

public class ClingoKnowledgeBase {

	//private final Map<Predicate, DataSourceDeclaration> edbPredicates = new HashMap<>();

	//now implemented in 
	//private final Map<DataSourceDeclaration, Predicate> aliasesForEdbPredicates = new HashMap<>();
	//private final Set<Predicate> aliasedEdbPredicates = new HashSet<>();

	//private final Set<Predicate> idbPredicates = new HashSet<>();

	private final Map<Predicate, List<Fact>> facts = new HashMap<>();

	private final Set<Rule> rules = new HashSet<>();
	
	
	// since characters in IRIs such as "<" are not allowed to be predicate names we need aliases for IRIs
	//is now in reasoner
	//private final AliasHandler aliasHandler = new AliasHandler();

	/**
	 * Package-protected constructor, that organizes given {@code knowledgeBase} in
	 * clingo-specific data structures.
	 * @param knowledgeBase
	 */
	ClingoKnowledgeBase(final KnowledgeBase knowledgeBase){
		LoadKbVisitor visitor = this.new LoadKbVisitor();
		//TODO visitor.clearIndexes

		for (final Statement statement: knowledgeBase){
			statement.accept(visitor);
		}
	}

	boolean hasData(){
		return !this.facts.isEmpty();
	}
	
	public boolean hasRules() {
		return !this.rules.isEmpty();
	}

	public Map<Predicate, List<Fact>> getFacts() {
		return this.facts;
	}
	
	

	public Set<Rule> getRules() {
		return this.rules;
	}

	class LoadKbVisitor implements StatementVisitor<Void>{

		@Override
		public Void visit(Fact fact) {
			final Predicate predicate = fact.getPredicate();

			//TODO registerEdbDeclaration

			if(!ClingoKnowledgeBase.this.facts.containsKey(predicate)) {
				final List<Fact> facts = new ArrayList<>();
				facts.add(fact);
				ClingoKnowledgeBase.this.facts.put(predicate, facts);
			} else {
				ClingoKnowledgeBase.this.facts.get(predicate).add(fact);
			}


			return null;
		}

		@Override
		public Void visit(Rule rule) {
		ClingoKnowledgeBase.this.rules.add(rule);
		return null;
		}

		//Not needed since DataSourceDeclarations need first go through Vlog
		@Override
		public Void visit(DataSourceDeclaration statement) {
			return null;
		}
	}
}
