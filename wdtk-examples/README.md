Wikidata Toolkit Examples
=========================

This module contains example programs that show some of the features
of Wikidata Toolkit.

Overview and Settings
---------------------

A detailed guide to each of the examples is given below. Many examples process data
dumps exported by Wikidata. In most cases, the example only contains the actual
processing code that does something interesting. The code for downloading dumps and
iterating over them is in the ExampleHelpers.java class, which is used in many examples
for common tasks.

You can edit the static members in ExampleHelpers to select which dumps should be
used (the data is available in several formats which may be more or less recent
and more or less comprehensive). You can also switch to offline mode there: then
only the files downloaded previously will be used. This is convenient for testing
to avoid downloading new files when you don't really need absolutely current data.
By default, the code will fetch the most recent JSON dumps from the Web.

Some examples write their output to files. These files are put into the subdirectory
"results" under the directory from where the application is run. Files in CSV
format can be loaded in any spreadsheet tool to make diagrams, for example.

Guide to the Available Examples
-------------------------------

Ordered roughly from basic to advanced/specific.

#### EntityStatisticsProcessor.java ####

This program processes entities (items and properties) to collect some basic
statistics. It counts how many items and properties there are, the number of labels,
descriptions, and aliases, and the number of statements. This code might be useful
to get to know the basic data structures where these things are stored. The example
also counts the usage of each property in more details: its use in the main part
of statements, in qualifiers, and in references is counted separately. The results
for this are written into a CSV file in the end.

#### FetchOnlineDataExample.java ####

This program shows how to fetch live data from wikidata.org via the Web API. This can
be used with any other Wikibase site as well. It is not practical to fetch all data
in this way, but it can be very convenient to get some data directly even when processing
a dump (since the dump can only be read in sequence).

#### EditOnlineDataExample.java ####

This program shows how to create and modify live data on test.wikidata.org via the Web API.
This can be used with any other Wikibase site as well. The example first creates a new item
with some starting data, then adds some additional statements, and finally modifies and 
deletes existing statements. All data modifications automatically use revision ids to make
sure that no edit conflicts occur (and we don't modify/delete data that is different from
what we expect).

#### LocalDumpFileExample.java ####

This program shows how to process a data dump that is available in a local file, rather
than being automatically downloaded (and possibly cached) from the Wikimedia site.

#### GreatestNumberProcessor.java ####

This simple program looks at all values of a number property to find the item with the
greatest value. It will print the result to the console. In most cases, the item with
the greatest number is fairly early in the data export, so watching the program work is
not too exciting, but it shows how to read a single property value to do something with
it. The property that is used is defined by a constant in the code and can be changed to
see some other greatest values.

#### LifeExpectancyProcessor.java ####

This program processes items to compute the average life expectancy of people on
Wikidata. It shows how to get details (here: year numbers) of specific statement values
for specific properties (here we use Wikidata's P569 "birth date" and P570 "death date").
The results are stored in a CSV file that shows average life expectancy by year of
birth. The overall average is also printed to the output.

#### WorldMapProcessor.java ####

This program generates images of world maps based on the locations of Wikidata items,
and stores the result in PNG files. The example builds several maps, for Wikidata as
a whole and for several big Wikipedias (counting only items with an article in there).
The code offers easy-to-adjust parameters for the size of the output images, the
Wikimedia projects to consider, and the scale of the color values.

[Wikidata world maps for June 2015](https://ddll.inf.tu-dresden.de/web/Wikidata/Maps-06-2015/en)

#### GenderRatioProcessor.java ####

This program uses Wikidata to analyse the number of articles that exist on certain
topics in different Wikimedia projects (esp. in Wikipedias). In particular, it counts
the number of articles about humans and humans of a specific gender (female, male, etc.).
Can be used to estimate the gender balance of various Wikipedias. The results are stored
in a CSV file (all projects x all genders), but for the largest projects they are also
printed to the output. This example is inspired by Max Klein's work on this topic.

[Related blog post by Max Klein](http://notconfusing.com/sex-ratios-in-wikidata-part-iii/)

#### JsonSerializationProcessor.java ####

This program creates a JSON file that contains English language terms, birthdate, occupation,
and image for all people on Wikidata who were born in Dresden (the code can easily be
modified to make a different selection). The example shows how to serialize Wikidata Toolkit
objects in JSON, how to select item documents by a property, and how to filter documents to
ignore some of the data. The resulting file is small (less than 1M).

#### SitelinksExample.java ####

This program shows how to get information about the site links that are used in Wikidata
dumps. The links to Wikimedia projects use keys like "enwiki" for English Wikipedia or
"hewikivoyage" for Hebrew WikiVoyage. To find out the meaning of these codes, and to
create URLs for the articles on these projects, Wikidata Toolkit includes some simple
functions that download and process the site links information for a given project.
This example shows how to use this functionality.

#### ClassPropertyUsageExample.java ####

This advanced program analyses the use of properties and classes on Wikidata, and creates
output that can be used in the [Miga data browser](http://migadv.com/). You can see the
result online at http://tools.wmflabs.org/wikidata-exports/miga/. The program is slightly
more complex, involving several processing steps and additional code for formatting output
for CSV files.

#### RdfSerializationExample.java ####

This program creates an RDF export. You can also do this directly using the command line
client. The purpose of this program is just to show how this could be done in code, e.g.,
to implement additional pre-processing before the RDF serialisation.


Other Helper Code
-----------------

#### ExampleHelpers.java ####

This class provides static helper methods to iterate through dumps, to configure the
desired logging behaviour, and to write files to the "results" directory. It also allows
you to change some global settings that will affect most examples. The code is of interest
if you want to find out how to build a standalone application that includes all aspects
without relying on the example module.

#### EntityTimerProcessor.java ####

This is a helper class that is used in all examples to print basic timer information and
to provide support for having a timeout (cleanly abort processing after a fixed time, even
if the dump would take much longer to complete; useful for testing). It should not be of
primary interest for learning how to use Wikidata Toolkit, but you can have a look to find
out how to use our Timer class.

Additional Resources
--------------------

* [Wikidata Toolkit homepage](https://www.mediawiki.org/wiki/Wikidata_Toolkit)
* [Wikidata Toolkit Javadocs](http://wikidata.github.io/Wikidata-Toolkit/)
