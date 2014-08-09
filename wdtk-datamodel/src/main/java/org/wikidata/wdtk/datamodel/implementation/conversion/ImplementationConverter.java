package org.wikidata.wdtk.datamodel.implementation.conversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ItemDocumentImpl;

/**
 * This class is dedicated to converting different implementations of this data
 * model between another. All implementations have in common that they must obey
 * the data model, but nothing more is guaranteed.
 * 
 * @author Fredo Erxleben
 *
 */
public class ImplementationConverter {

	private static DataObjectFactory factory = new DataObjectFactoryImpl();
	
	/**
	 * 
	 * @param source
	 * @param toImplementation
	 * @return an instance of the requested implementation or null
	 */
	public static ItemDocument convertItemDocument(ItemDocument source,
			ImplementationDenomination toImplementation) {

		switch (toImplementation) {
		case IMPL_JACKSON:
			return new ItemDocumentImpl(source);
		case IMPL_STORAGE: 
			return toStorageItemDocument(source);
		}
		return null;
	}

	public static PropertyDocument convertPropertyDocument(
			PropertyDocument source, ImplementationDenomination toImplementation) {
		// TODO
		return null;
	}

	/**
	 * Uses the DataObjectFactory to create an ItemDocument implementation for
	 * the storage data model.
	 * 
	 * @param source
	 * @return
	 */
	private static ItemDocument toStorageItemDocument(ItemDocument source) {

		// TODO double check this to avoid unpleasant surprises with typing
		
		ItemIdValue sourceItemId = source.getItemId();
		ItemIdValue itemIdValue = factory.getItemIdValue(sourceItemId.getId(), sourceItemId.getIri());
		
		List<MonolingualTextValue> labels = new ArrayList<>();
		labels.addAll(source.getLabels().values());
		
		List<MonolingualTextValue> descriptions = new ArrayList<>();
		descriptions.addAll(source.getDescriptions().values());
		
		List<MonolingualTextValue> aliases = new ArrayList<>();
		for(List<MonolingualTextValue> values : source.getAliases().values()){
			aliases.addAll(values);
		}
		
		List<StatementGroup> statementGroups = source.getStatementGroups();
		
		
		Map<String, SiteLink> siteLinks = source.getSiteLinks();
		return factory.getItemDocument(itemIdValue, labels, descriptions, aliases, statementGroups, siteLinks);
	}
}
