package org.wikidata.wdtk.datamodel.interfaces;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
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

/**
 * The id of a Wikibase MediaInfo. Objects implementing this interface always return
 * {@link EntityIdValue#ET_MEDIA_INFO} for {@link EntityIdValue#getEntityType()
 * getEntityType}.
 *
 * @author Thomas Pellissier Tanon
 *
 */
public interface MediaInfoIdValue extends EntityIdValue {

	/**
	 * Fixed {@link MediaInfoIdValue} that refers to a non-existing item. Can be used
	 * as a placeholder object in situations where the entity id is irrelevant.
	 */
	MediaInfoIdValue NULL = new MediaInfoIdValue() {

		@Override
		public String getIri() {
			return getSiteIri() + getId();
		}

		@Override
		public <T> T accept(ValueVisitor<T> valueVisitor) {
			return valueVisitor.visit(this);
		}

		@Override
		public String getEntityType() {
			return ET_MEDIA_INFO;
		}

		@Override
		public String getId() {
			return "M0";
		}

		@Override
		public String getSiteIri() {
			return EntityIdValue.SITE_LOCAL;
		}
		
		@Override
		public boolean equals(Object other) {
			return Equality.equalsEntityIdValue(this, other);
		}
		
		@Override
		public int hashCode() {
			return Hash.hashCode(this);
		}

	};

}
