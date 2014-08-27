#!/bin/bash
alias tawk='awk -F"\t"'
#cat allCountries.txt  | tawk '{if ($7=="A") print $0}' | grep PCLI | tawk '{print $2"\t"$4"\t"$9"\tCountry\t"$15}' > countries_v3.csv
cat allCountries.txt| grep PPL | tawk '{if( $(NF-4) >= 30000) print $0}' | tawk '{print $2"\t"$4"\t"$9"\tCity\t"$15}' > cities_v3.csv
