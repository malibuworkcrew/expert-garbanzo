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

The input argument is the path of the file to import. The file must contain
all columns and must have a header, the columns can be in any order.

## murakami-core (Query Service)
Queries stored data using a robust query language

# Starting the Query Service
java -Dconfig.file=conf/query.conf -jar dist/murakami-core-1.0-SNAPSHOT-shaded.jar

## To Query the Service
Any combination of the below query methods can be used to return data.

# Available Columns
* STB (String)
* TITLE (String)
* REV (Double)
* VIEW_TIME (Date)
* DATE (Date)
* PROVIDER (String)

# Select Queries
A simple select query will look across all data to get all the specified columns
> bin/query -s $column1,$column2,...

EXAMPLE
> bin/query -s STB,TITLE

# Ordering Results
Any query can be ordered, simply input any number of columns and the query
will be ordered by those columns starting with the first (each following
column is used to break ties)
> bin/query -s $column1,$column2,... -o $column1,$column3,...

EXAMPLE
> bin/query -s STB,TITLE -o STB

# Advanced Filtering of Results
Queries can be filtered with checks on equality for any column (in the strict format $col="$val"),
also one can combine any number of 'and' or 'or' statements to create a filter.
> bin/query -s $column1,$column2,... -f '$column1=$value1 and ($column2 = $value2 or $column1=$value3) ...'

EXAMPLE
> bin/query -s STB,TITLE -f 'STB="stb1" or (STB="stb2" and TITLE="the hobbit")'

# Grouping and Aggregating
Instead of a raw select, one can choose to aggregate data for each column based on a column
group. To use this functionality one will have to set a group and specify aggregates for
all other columns.

Available Aggregates:
* min - Numerical only, finds the minimum value
* max - Numerical only, finds the maximum value
* sum - Numerical only, adds up all values
* Count - Any, adds up the number of unique values
* Collect - Any, creates a list of all unique values

> bin/query -s $groupColumn,$column1:agg1,$column2:agg2,... -g $groupColumn

EXAMPLE
>  bin/query -s STB,TITLE:collect,REV:sum -g STB

OUTPUT
> stb1,[the game,unbreakable,the matrix],18.5
> stb3,[bruce lee,the matrix],6.0
> stb2,[mighty ducks,the hobbit],16.0