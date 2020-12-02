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

import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import rulewerk.clingo.Term;
import rulewerk.clingo.TermQueryResultIterator;

public class ClingoQueryResultIterator implements QueryResultIterator {

	private final TermQueryResultIterator clingoTermQueryResultIterator;
	private final AliasHandler aliasHandler;

	public ClingoQueryResultIterator(TermQueryResultIterator termQuery, AliasHandler aliasHandler) {
		this.clingoTermQueryResultIterator = termQuery;
		this.aliasHandler = aliasHandler;
	}



	//TODO implement this
	@Override
	public Correctness getCorrectness() {
		return null;
	}

	@Override
	public void close() {
		this.clingoTermQueryResultIterator.close();
	}

	@Override
	public boolean hasNext() {
		return this.clingoTermQueryResultIterator.hasNext();
	}

	@Override
	public QueryResult next() {
		final Term[] clingoQueryResult = this.clingoTermQueryResultIterator.next();
		return ClingoToModelConverter.toQueryResult(clingoQueryResult, this.aliasHandler);
	}
}
