#!/bin/bash
SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $SCRIPT_DIR


function usage() {
   echo "Usage: submit.sh [<host:port>] [<group>/]<projectName> <description>" >&2
   echo "NOTE: you need to set the environment variable GITLAB_TOKEN with your gitlab API token" >&2
   exit 1 
}

if [ -z "$GITLAB_TOKEN" -o $# -lt 2 ]; then
   usage
fi

HOST=localhost:9697
if [ $# -eq 3 ]; then 
   HOST=$1
   shift
fi

GROUP_AND_PROJECT=$1
PROJECT_NAME=${GROUP_AND_PROJECT#*/}
if [ "$PROJECT_NAME" != "$GROUP_AND_PROJECT" ]; then
   PROJECT_GROUP=${GROUP_AND_PROJECT%/*}
fi

PROJECT_DESCRIPTION="$2"
echo "Creating project $PROJECT_GROUP/$PROJECT_NAME: $PROJECT_DESCRIPTION"

DATA_FILE=$( mktemp "/tmp/scenario4.XXXX" )

cat << EOF > $DATA_FILE
{
"content": "{ \"required_skill\": \"java\", \"project_name\": \"$PROJECT_NAME\", \"group\": \"$PROJECT_GROUP\", \"coverage\": \"0.90\", \"gitlab_token\": \"$GITLAB_TOKEN\", \"description\": \"$PROJECT_DESCRIPTION\"}",
"type": "submit",
"subtype": "",
"sender": "",
"conversation": "Scenario4",
"language": "",
"securityToken": ""
}
EOF

curl -v -H "Content-type: application/json" -X POST -d "@$DATA_FILE" "http://$HOST/" && rm $DATA_FILE || echo "Failed! Request can be found in $DATA_FILE"
