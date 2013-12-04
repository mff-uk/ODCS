#!/bin/bash

script=`readlink -f $0`
basedir=`dirname $script`

dumpdir="${basedir}/../virtuoso/rdbms"
config="${HOME}/.odcs/config.properties"

# parse out login info from configuration
dbuser=`grep "^virtuoso.rdbms.user" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbpass=`grep "^virtuoso.rdbms.password" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbhost=`grep "^virtuoso.rdbms.hostname" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbport=`grep "^virtuoso.rdbms.port" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`

# recreate schema and import data
cat "${dumpdir}/schema.sql" "${dumpdir}/data.sql" "${dumpdir}/sequences.sql" | \
	isql-v "${dbhost}:${dbport}" "$dbuser" "$dbpass"

