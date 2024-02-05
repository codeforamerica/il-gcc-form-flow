# script for generating html files based on flow-config yaml.
# $ brew install yq
# $ chmod u+x scripts/create-templates.sh
# $ ./scripts/create-templates.sh
flowName='gcc'
templates=`ls -1 src/main/resources/templates/${flowName}/*.html | cut -d'/' -f6-`
screens=( $(yq '[select(.name == "'${flowName}'") | .flow | map(key)] | flatten' src/main/resources/flows-config.yaml) )
screenNames=( $( printf '%s\n' ${screens[@]} | egrep -v '^(---|-|null|\[*\])' ) )
for i in "${!screenNames[@]}"; do
  if grep -q "${screenNames[$i]}.html" <<< "${templates[*]}"; then
    echo "found ${screenNames[$i]} in templates"
  else
    echo "${screenNames[$i]} not in templates; creating one"
    cp scripts/template.html "src/main/resources/templates/${flowName}/${screenNames[$i]}.html"
  fi
done


