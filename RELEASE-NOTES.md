# Changelog

## [0.17.0](https://github.com/Wikidata/Wikidata-Toolkit/compare/v0.16.1-SNAPSHOT...v0.17.0) (2025-02-19)


### Features

* Expose detailed error messages in MediaWiki API errors ([#911](https://github.com/Wikidata/Wikidata-Toolkit/issues/911)) ([8627004](https://github.com/Wikidata/Wikidata-Toolkit/commit/862700436e8607f9c8ca80eccde49f9fa3b83c52))

Wikidata Toolkit Release Notes
==============================

Version 0.16.0
--------------

New feature:
* Expose detailed error messages in MediaWiki API errors (#911)

Version 0.15.3
--------------

Bug fix:
* Download of online dumps was fixed (#872)

Incompatible change:
* Minimum Java version changed from 8 to 11 (#839)

Versions 0.15.0 to 0.15.2 are skipped because of publishing issues.

Version 0.14.7
--------------

Bug fix:
* Reset CSRF & login tokens when a login error happens (#442)

Version 0.14.6
--------------

Small improvement:
* The new Wikibase editing API returns the id of the revision of the last edit made (#795)

Version 0.14.5
--------------

Bug fixes:
* fetching of MediaInfo entities by title, when they contain a dash (#777)
* clear the CSRF editing token if it is no longer valid (#442)

Version 0.14.4
--------------

Bug fixes:
* fix deserialization of lexemes, workaround for https://phabricator.wikimedia.org/T305660

Version 0.14.3
--------------

Bug fixes:
* fix fetching of Mids from filenames in the case where multiple filenames do not exist (#745)

Version 0.14.2
--------------

Bug fixes:
* add `uselang` as optional parameter for wbsearchentities action (#239)

Version 0.14.1
--------------

Bug fixes:
* fix error handling in newly supported clientLogin method
* fix error in deserialization of properties with unknown datatypes

Deprecations:
* the IRI representation of datatypes in `wdtk-datamodel` is deprecated.
  If you rely on it, then use it in `wdtk-rdf`, since this is specific to the RDF
  serialization of the datamodel. Use the strings found in the JSON serialization
  of properties to identify datatypes instead.

Version 0.14.0
--------------

New feature:
* login to Wikibase via the recommended API for normal login/password login

Bug fixes:
* add explicit dependency to okhttp, to avoid linkage issues
* upgrade to okhttp 4.10.0

Version 0.13.5
--------------

* downgraded okhttp to 4.2.2 to solve a linkage error (issue #600). We anticipate that this will be reverted once a stable version of okhttp 5 is available and a viable way to avoid such a linkage error is found.

Version 0.13.4
--------------

* updated okhttp to 5.0.0-alpha.10 in the hope that it solves a linkage error
* registered the EDTF datatype

Version 0.13.3
--------------

* fixed media type when uploading files to a MediaWiki API endpoint

Version 0.13.2
--------------

* new method to create an EntityDocument independently of its type
* new utility method to execute an authenticated HTTP method which posts files 

Version 0.13.1
--------------

Minor changes to the CI configuration for artifact deployment in Maven Central, no changes in the library itself.

Version 0.13.0
--------------

New features:
* New API to edit Wikibase entities

Bug fixes:
* Fetching of non-existent Mids on Commons
* Support for missing entity types in DatamodelConverter
* Store QuantityValue units as ItemIdValue instead of String.
* Allow lexeme lemma list to be empty.

Version 0.12.1
--------------

Bug fixes:
* Allows empty representation list in `FormDocument` to parse the most recent Wikidata dumps.

Version 0.12.0
--------------

Bug fixes:
* Allows empty gloss list in `SenseDocument` to parse the most recent Wikidata dumps.

New features:
* Allows fetching MediaInfo entities using `WikibaseDataFetcher`.
* `WikibaseRevisionProcessor` now parses and exposes redirections between entities.
* `OAuthApiConnection` to connect to Wikibase API using OAuth.
* Allows to fetch the Wikibase edit lag.
* Dump file compression is automatically guessed from the file name extensions.

Incompatible changes:
* More API client errors are now exposed as exception, allowing the API users to act on them. 
* `OkHTTP` is now used in wikibaseapi-client and big revamp of the client internals with small breaking changes.
* Deprecated methods removal across the codebase.

Dependency upgrades:
* Dropped unused Apache HTTP client dependency
* Bump RDF4J to 3.6.4, Jackson to 2.12.3, Apache Commons IO to 2.8, Apache Commons Lang3 to 3.12.

Version 0.11.1
--------------

Bug fixes:
* Fixes API connection bug due to lower-case set-cookie header sent from Wikidata
* Upgrades dependencies to latest version

Version 0.11.0
--------------

New features:
* Adds basic MediaInfo representation, API retrieval and editing.
* Adds support of tags when editing using the API.
* Adds UnsupportedValue and UnsupportedEntityIdValue to properly represent unsupported values and entity ids.
* RDF: Fixes datatype lookup for entity ids
RDF: Adds support of Quantity and MonolingualText to the SomeValueSnak and NoValueSnak converters.
* Wikibase API: Throw an exception when credentials have expired.
* Updates RDF4J to 2.5.2, Apache Commons Lang to 3.9 and Apache Commons Compress to 1.18.
* Properly deserialize and store the Reference hash.
* Adds edit methods to Lexeme, Form and Sense.
* Adds timeout options to HTTP calls.
* Adds exponential back-off for maxlag errors.

Incompatible changes:
* Removes the wikibase-client package.
* Makes Statement.getBestStatements return null if there are no best statements.
* Makes RDF output format closer to the one used by Wikibase.
* Throw MediaWikiApiErrorException instead of NullPointerException if the edit token is not found.

Bug fixes:
* Removes main snak value serialization from statement serialization.
* Use CSRF token for logout following MediaWiki API change.

Version 0.10.0
--------------

Security fixes:
* Update Jackson to 2.9.9, fixing [vulnerabilities that might lead to remote code execution](https://www.cvedetails.com/vulnerability-list.php?vendor_id=15866&product_id=42991&version_id=238358&page=1&hasexp=0&opdos=0&opec=0&opov=0&opcsrf=0&opgpriv=0&opsqli=0&opxss=0&opdirt=0&opmemc=0&ophttprs=0&opbyp=0&opfileinc=0&opginf=0&cvssscoremin=0&cvssscoremax=0&year=0&cweid=0&order=1&trc=12&sha=1a71cae633886fb92e024fafb20c582c9e5b072d).

New features:
* RDF: Adds support of Quantity and MonolingualText to the SomeValueSnak and NoValueSnak converters.
* Wikibase API: Throw an exception when credentials have expired.
* Updates RDF4J to 2.5.2, Apache Commons Lang to 3.9 and Apache Commons Compress to 1.18.

Incompatible changes:
* Propagate IOException properly in the Wikibase API module.

Version 0.9.0
-------------

New features:
* Compatibility with JDK 10
* Compatibility with Android except for the RDF component. It requires Gradle Android plugin 3.0+.
* Addition of basic support for Wikibase Lexemes (including forms and senses)
* The RDF default output is now the same as query.wikidata.org and specified at https://www.mediawiki.org/wiki/Wikibase/Indexing/RDF_Dump_Format except normalized values that are not supported.
* Migration from Sesame to RDF4J
* Most of DataModel classes has now with* methods to do easily modification of objects while keeping immutability
* parentRevisionId is now provided by the XML dump files reader
* WikimediaLanguageCodes.fixLanguageCodeIfDeprecated allows to fix deprecated language codes
* StatementGroup (resp. SnakGroup) implements Collection<Statement> (resp. Collection<Snak>)
* EntityRedirectDocument object in order to easily represent redirections between entities
* StatementGroup::getBestStatements utility method to retrieve quickly the best statements of the group
* GuidGenerator and an implementation to generate statements uuid easily
* When editing entities, the implementation attempts to use the most granular API call to perform the edit, which makes more informative edit summaries.
* Addition of QuantityValue.getUnitItemId, TimeValue.getCalendarItemId and GlobeCoordinatesValue.getGlobeItemId to get easily ItemIdValue objects for these three fields
* Introduction of DatamodelFilter to split out the filtering capabilities of DatamodelConverter
* ApiConnection was changed to an interface, implemented by BasicApiConnection for normal login and (in the future) OAuthApiConnection for OAuth. BasicApiConnection was made serializable with Jackson so that a connection can be saved and restored.

Bug fixes:
* Retrieval of redirected entities using WbGetEntitiesAction should work
* StatementUpdate avoids to do null edits except if intentionally asked so
* The WikimediaLanguageCodes lists have been updated
* Proper RDF serialization of the Commons GeoShape and Commons Data datatypes


Incompatible changes:
* Support for JDK 7 is dropped.
* The simple data model implementation has been dropped. The Jackson based implementation is now the only one provided by the library. It allows to avoid to maintain two implementations and the cost of conversion between the two representations. The jackson implementation has been moved to the former one package.
* Migration from Sesame to RDF4J in the RDF component interface
* Updates of various dependencies
* The utility classes related to JSON (de)serialization are now private
* SiteLink badges are now ItemIdValue and not String
* The internal encoding of uniteless QuantityValue unit is now "1" and not "" for consistency with Wikibase
* The default value for the "after" parameter of TimeValues is no 0 for compatibility with Wikibase
* DatatypeIdValue is not implementing Value anymore. It improves type safety because Wikibase does not allow to use DatatypeIdValue as snak value.
* The DatamodelConverter class does not do shallow copy anymore. Please use DatamodelFilter for filtering
* The constraint TemplateParser related code have been removed. Constraints are now encoded as statements in Wikidata, making this code only usable in old dumps
* The TimeValue timestamps are now serializing years with at least 4 digits and not 11. This replicates a change in Wikibase and make the output timestamps more similar to the ISO/XSD ones.


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
