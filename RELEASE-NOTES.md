Wikidata Toolkit Release Notes
==============================

Version 0.8.0
-------------

New features:
* Compatibility with JDK 9
* Allow to edit labels, descriptions and aliases using the WikibaseDataEditor (this is a work in progress that is likely to change)
* Allow to use the wbEntitySearch API action using WikibaseDataFetcher
* Quantities bounds are now optional following the change in Wikibase
* Add the "id" field to entity id JSON serialization following the change in Wikibase

Bug fixes:
* Do not fail when logging in
* Do not fail when reading redirections in daily XML dumps
* Do not fail when new datatypes are introduced in Wikibase
* Make sure that API warnings are read for all requests
* Do not fail when reading a bz2 compressed dump when a gzip dump was expected
* WikibaseDataFetcher is now able to retrieve more than 50 entities at once
* Switch to the new way of retrieving MediaWiki API tokens

Version 0.7.0
-------------

New features:
* Add a new client action "sqid" that analyses dumps to create the statistics
  JSON files that are the basis for the SQID Wikidata Browser that is found at
  https://tools.wmflabs.org/sqid/

Bug fixes:
* Fix JavaDoc errors to enable build using Java 8 (with doclint)
* Make JSON parser more tolerant towards unknown keys; avoids breaking on recent API changes
* Update Wikimedia dump location to https so that dump downloads work again

Version 0.6.0
-------------

A new stand-alone example project is now showing how to use WDTK as a library:
https://github.com/Wikidata/Wikidata-Toolkit-Examples

New features:
* Support for new Wikidata property type "external identifier"
* Support for new Wikidata property type "math"
* Bots: support maxlag parameter and edit-rate throttling
* Bots: better Wikidata API error handling
* Bots: several real-world bot examples
* New convenience methods for accessing Wikidata Java objects, for simpler code
* full compatibility with Java 8

Bug fixes:
* Fix NullPointerException when trying to establish API connection (issue #217)
* Avoid test failures on some platforms (based on too strict assumptions)


Version 0.5.0
-------------

New features:
* Support for reading and writing live entity data from wikidata.org or any other Wikibase site (issue #162)
* New examples for illustrating read/write API support
* Support for quantities with units of measurement (new feature in Wikibase; still beta)
* New builder classes to simplify construction of EntityDocuments, Statements, and References
* Support processing of local dump files by file name in code and command-line client (issue #136)
* New example WorldMapProcessor that shows the generation of maps from geographic data
* Improved output file naming for examples, taking dump date into account
* RDF export uses property register for fewer Web requests during export
* RDF export supports P1921 URI patterns to create links to external RDF datasets

Bug fixes:
* JSON conversion action of the command-line client was forgetting start of entity list.
* Update URLs to use https instead of http
* Support URLs in sites table that are not protocol-relative (issue #163)

Incompatible changes:
* EntityDocumentProcessorFilter has a modified constructor that requires a filter object
  to be given. The direct set methods to define the filter are no longer available.


Version 0.4.0
-------------

New features:
* Support statements on property documents
* More robust JSON parsing: recover after errors to process remaining file
* Improved JSON serialization + an example program showing how to do it
* Standard (POJO) datamodel implementation now is Serializable
* Deep copy functionality for changing between datamodel implementations (DatamodelConverter)
* Support for filtering data during copying (e.g., to keep only some languages/properties/sites).
* Support arbitrary precision values in globe coordinates
* Dependency on JSON.org has been removed to use the faster Jackson library everywhere 

Bug fixes:
* Support RDF export of Monolingual Text Value data in statements.
* Significant performance improvements in RDF export of taxonomy data.
* Support new Wikimedia Foundation dump file index HTML format (Issue #114)

Incompatible changes:
* The datatype of all values in GlobeCoordinateValue (latitude, longitude, precision) has
  changed from long (fixed precision number) to double (floating point number) to match the JSON.
* The JSON serializer class org.wikidata.wdtk.datamodel.json.JsonSerializer has vanished. It is
  replaced by the org.wikidata.wdtk.datamodel.json.jackson.JsonSerializer (almost same interface).


Version 0.3.0
-------------

New features:
* Added full support for reading data from the API JSON format (now used in all dumps);
  reading JSON dumps also became much faster with this change
* Improved examples (more, faster, easier-to-read programs); documentation on each
  example is now found in the Readme.md file in the example package
* Added iterator access to all statements of an item document, all statements in a statement
  group, all qualifiers in a claim, all snaks in a snak group, and all snaks in a reference
* Dump files are downloaded to temporary files first to prevent incomplete downloads
  from causing errors
* Datamodel objects can now be constructed using the static methods of Datamodel. This makes
  object creation more convenient.

Minor changes:
* ItemIdValue and PropertyIdValue objects now have a "site IRI" that can be retrieved.
  This was called "base IRI" in earlier releases and was only used to construct the full
  IRI. The new concept is that this IRI is actually the identifier for the site that the
  entity comes from. It is important to make it retrievable since it is needed (like in
  previous versions) to construct the object using the factory.
* A new helper package in the datamodel module contains common hashCode(), equals(), and
  toString() methods that can be used by any datamodel implementation.

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

   
