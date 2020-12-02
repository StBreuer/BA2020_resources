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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.reasoner.implementation.QueryResultImpl;
import org.semanticweb.rulewerk.core.model.api.Term;


public class ClingoToModelConverter {

	/*static QueryResult toQueryResult(rulewerk.clingo.Term[] clingoQueryResult, AliasHandler aliasHandler){
		//final List<Term> terms = new ArrayList<>(clingoQueryResult.length);
		final List<Term> terms = toTermList(clingoQueryResult);
		
		return new QueryResultImpl(terms);
	}*/
	
	public static QueryResult toQueryResult(rulewerk.clingo.Term[] clingoQueryResult, AliasHandler aliasHandler){
		
		final List<Term> terms = toTermList(clingoQueryResult);
		return new QueryResultImpl(aliasHandler.clingoToRulewerkTerms(terms));
	}

	private static List<Term> toTermList(rulewerk.clingo.Term[] clingoTerms){
		final List<Term> terms = new ArrayList<>(clingoTerms.length);
		for (rulewerk.clingo.Term term : clingoTerms) {
			terms.add(toTerm(term));
		}
		return terms;
	}

	private static Term toTerm(rulewerk.clingo.Term clingoTerm){
			String name = clingoTerm.getName();	
		switch (clingoTerm.getTermType()) {
			case CONSTANT:
				
				return toConstant(name);
			case BLANK:
				return new NamedNullImpl(name);
			case VARIABLE:
				throw new IllegalArgumentException(
					"Clingo variables cannot be converted without knowing if they are universally or existentially quantified.");
			default:
				throw new IllegalArgumentException("Unexpected Clingo term type: " + clingoTerm.getTermType());
		}
	}

	private static Constant toConstant(String clingoConstantName){
		//TODO deal with iri's and other Problems
		return new AbstractConstantImpl(clingoConstantName);
	}


}
