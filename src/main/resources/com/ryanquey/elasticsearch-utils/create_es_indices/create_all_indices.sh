# NOTE add your tables in here. Then add corresponding json file (see example) in same dir
# https://stackoverflow.com/a/8880633/6952495
declare -a tables=(
	# "podcasts_by_language" 
)

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
## now loop through the above array
printf "\n\n**********************"
printf "\n***NOW CREATING ALL ES INDICES***"
for i in "${tables[@]}"
do
  printf "\n\nCreating index for: $i"
  printf "\ncalling: curl -XPUT -H 'Content-Type: application/json' \"http://localhost:9200/$i\" -d @./$i.json\n"
  echo "---"
	curl -XPUT -H 'Content-Type: application/json' "http://localhost:9200/$i" -d @$parent_path/$i.json
   # or do whatever with individual element of the array
done

# You can access them using printf "${arr[0]}", "${arr[1]}" also
