package org.wikidata.wdtk.dumpfiles.constraint.processor;

import org.wikidata.wdtk.dumpfiles.MwRevision;

/**
 * An object of this class is a mock mediawiki revision used for testing. In
 * particular, this revision has some of the content of the talk page of
 * <i>instance of (P31)</i>.
 * 
 * @author Julian Mendez
 *
 */
public class MockMwRevision implements MwRevision {

	@Override
	public String getPrefixedTitle() {
		return "Property talk:P31";
	}

	@Override
	public String getTitle() {
		return "P31";
	}

	@Override
	public int getNamespace() {
		return 121;
	}

	@Override
	public int getPageId() {
		return 3926387;
	}

	@Override
	public long getRevisionId() {
		return 194063938;
	}

	@Override
	public String getTimeStamp() {
		return "2014-12-31T12:02:18Z";
	}

	@Override
	public String getText() {
		return "\n{{Constraint:Target required claim|property=P279|exceptions ={{Q|35120}}}}"
				+ "\n{{Constraint:Conflicts with|list={{P|31}}: {{Q|8441}}, {{Q|467}}, {{Q|6581097}}, {{Q|6581072}}|mandatory=true}}\n      ";
	}

	@Override
	public String getModel() {
		return "wikitext";
	}

	@Override
	public String getFormat() {
		return "text/x-wiki";
	}

	@Override
	public String getComment() {
		return "Comment";
	}

	@Override
	public String getContributor() {
		return "Username";
	}

	@Override
	public int getContributorId() {
		return 1;
	}

	@Override
	public boolean hasRegisteredContributor() {
		return true;
	}

}
