### murakami
To take advantage of functionality, one has to start up each of the
two services.

There are two services:
## file-importer
Reads in files and stores data in the data/ directory

# Starting the Import Service
java -Dconfig.file=conf/import.conf -jar dist/file-importer-1.0-SNAPSHOT-shaded.jar

# To Read a File
One can use the importData script in bin/ like so:
> bin/importData "file-importer/importData/importAdvanced.psv"

The input argument is the path of the file to import.

## murakami-core (Query Service)
Queries stored data using a robust query language

# Starting the Query Service
java -Dconfig.file=conf/query.conf -jar dist/murakami-core-1.0-SNAPSHOT-shaded.jar

## To Query the Service
# Available Columns
* STB (String)
* TITLE (String)
* REV (Double)
* VIEW_TIME (Date)
* DATE (Date)
* PROVIDER (String)

# Select Queries
A simple select query will look across all data to get all the specified columns
> bin/query -s $column1,$column2

EXAMPLE
> bin/query -s STB,TITLE