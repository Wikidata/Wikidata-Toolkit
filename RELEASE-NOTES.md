Wikidata Toolkit Release Notes
==============================

Version 0.3.0
-------------

New features:
* Added iterator access to all statements of an itemdocument, all statements in a statement
  group, all qualifiers in a claim, all snaks in a snak group, and all snaks in a reference

Bug fixes:
* Fix grouping of Statements when reading data from dumps (Issue #78)


Version 0.2.0
-------------

New features:
* Support for serializing Wikibase data in RDF (as illustrated in new example);
  see http://korrekt.org/page/Introducing_Wikidata_to_the_Linked_Data_Web for details
* Simplified code for dump file processing: new helper class DumpProcessingController
* Support for resolving site links, based on information from the sites table dump
  (as demonstrated in a new example program)
* Support for SnakGroups (data model updated to group Snaks by property in all lists)
* Support for serializing Wikibase data in JSON (as illustrated in new example)

Bug fixes:
* Support changed Wikimedia dump HTML page format, which caused download to fail (Issue #70)
* Support processing of property documents when parsing dumps (Issue #67)
* Support SomeValueSnak and NoValueSnak in references (Issue #44)
* Use correct site links when importing data from dumps (Issue #37)
* Do not attempt to download unfinished dump files (Issue #63)

Incompatible changes:
* The processing of dumpfiles was simplified, using a new class DumpProcessingController.
  The former method WmfDumpFileManager#processRecentRevisionDumps() was replaced by
  DumpProcessingController#processAllRecentRevisionDumps(). See the examples for example
  code.
* Dump files no longer support the retrieval of the maximal revision id, since this
  information is no longer published for the main dumps on the Wikimedia site.


Version 0.1.0
-------------

New features:
* Initial Java implementation of Wikibase datamodel
* Support for downloading Wikimedia dumpfiles
* Support for parsing MediaWiki XML dumps
* Support for parsing Wikibase dump contents to get entity data
* Example Java program shows how to process Wikidata dump files

Bug fixes:
* not applicable; this is the very first release 

Know issues:
* Entities loaded from dump get wrong base IRI (issue #43)
* URLs for sitelinks are missing (issue #37)

   
