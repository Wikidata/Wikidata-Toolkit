Wikidata Toolkit
================

![Build status](https://github.com/Wikidata/Wikidata-Toolkit/workflows/Java%20CI/badge.svg)
[![Coverage status](https://codecov.io/gh/Wikidata/Wikidata-Toolkit/branch/master/graph/badge.svg?token=QtTNJdTAbO)](https://codecov.io/gh/Wikidata/Wikidata-Toolkit)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.wikidata.wdtk/wdtk-parent/badge.svg)](http://search.maven.org/#search|ga|1|g%3A%22org.wikidata.wdtk%22)
[![Project Stats](https://www.openhub.net/p/Wikidata-Toolkit/widgets/project_thin_badge.gif)](https://www.openhub.net/p/Wikidata-Toolkit)

Wikidata Toolkit is a Java library for accessing Wikidata and other Wikibase installations. It can be used to create bots, to perform data extraction tasks (e.g., convert all data in Wikidata to a new format), and to do large-scale analyses that are too complex for using a simple SPARQL query service.

Documentation
-------------

* [Wikidata Toolkit homepage](https://www.mediawiki.org/wiki/Wikidata_Toolkit): project homepage with basic user documentation, including guidelines on how to setup your Java IDE for using Maven and git.
* [Wikidata Toolkit examples](https://github.com/Wikidata/Wikidata-Toolkit-Examples): stand-alone Java project that shows how to use Wikidata Toolkit as a library for your own code.
* [Wikidata Toolkit Javadocs](http://wikidata.github.io/Wikidata-Toolkit/): API documentation

License and Credits
-------------------

Authors: [Markus Kroetzsch](http://korrekt.org), [Julian Mendez](https://julianmendez.github.io/), [Fredo Erxleben](https://github.com/fer-rum), [Michael Guenther](https://github.com/guenthermi), [Markus Damm](https://github.com/mardam), [Antonin Delpeuch](http://antonin.delpeuch.eu/), [Thomas Pellissier Tanon](https://thomas.pellissier-tanon.fr/) and [other contributors](https://github.com/Wikidata/Wikidata-Toolkit/graphs/contributors)

License: [Apache 2.0](LICENSE.txt)

The development of Wikidata Toolkit has been partially funded by the Wikimedia Foundation under the [Wikibase Toolkit Individual Engagement Grant](https://meta.wikimedia.org/wiki/Grants:IEG/Wikidata_Toolkit), and by the German Research Foundation (DFG) under [Emmy Noether grant KR 4381/1-1 "DIAMOND"](https://ddll.inf.tu-dresden.de/web/DIAMOND/en).

How to make a release
---------------------

During development, the version number in the `pom.xml` files should be the next version number assuming that the next version is a patch release, followed by `-SNAPSHOT`. For instance, if the last version to have been released was `1.2.3`, then the `pom.xml` files should contain `<version>1.2.4-SNAPSHOT</version>`.

1. Pick the version number for the new release you want to publish, following SemVer. If this is going to be a patch release, it should be the version currently in `pom.xml` without the `-SNAPSHOT` suffix. In the following steps, we will assume this new version is `1.2.4`.
2. Write the new version number in the `pom.xml` files with `mvn versions:set -DnewVersion=1.2.4`
3. Commit the changes: `git commit -am "Set version to 1.2.4"`
4. Add a tag for the version: `git tag -a v1.2.4 -m "Version 1.2.4"`
5. Write the next version number in the `pom.xml` file, by incrementing the patch release number: `mvn versions:set -DnewVersion=1.2.5-SNAPSHOT`
6. Commit the changes: `git commit -am "Set version to 1.2.5-SNAPSHOT"`
7. Push commits and tags: `git push --tags master`
8. In GitHub's UI, create a release by going to https://github.com/Wikidata/Wikidata-Toolkit/releases/new. Pick the tag you just created, give a title to the release and quickly describe the changes since the previous release (see existing releases for examples).
9. Update the version number mentioned in https://www.mediawiki.org/wiki/Wikidata_Toolkit
