#!/bin/bash
SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $SCRIPT_DIR

function usage() {
   echo "Usage: create_peer.sh [<host:port>] <user>" >&2
   echo "NOTE: you need to set and export he environment variable GITLAB_TOKEN with your gitlab API token" >&2
   exit 1 
}

if [ -z "$GITLAB_TOKEN" ]; then
   usage
fi

HOST=localhost:9697
if [ $# -eq 2 ]; then 
   HOST=$1
   shift
fi


GITLAB_NAME=$1
if [ -z "$GITLAB_NAME" ]; then
   echo $#
   usage
fi
echo "Looking id for user: $GITLAB_NAME"
GITLAB_ID=$( \
   curl -s -XGET --header "PRIVATE-TOKEN: $GITLAB_TOKEN" "https://gitlab.com/api/v3/users?search=$GITLAB_NAME" \
   | sed -e 's/.*\"id\"\:\([0-9]*\),.*/\1/' \
)
echo "Got id: $GITLAB_ID"
if [ -z "GITLAB_ID" ]; then
   echo "User $GITLAB_NAME does not exists on gitlab.com" >&2
fi
  
PROJECT_DESCRIPTION="$2"

DATA_FILE=$( mktemp "/tmp/scenario4.XXXX" )

cat << EOF > $DATA_FILE 
{
"content": "{ \"id\": \"$GITLAB_ID\", \"skill\": \"java\"}",
"type": "peer",
"subtype": "",
"sender": "",
"conversation": "Scenario4",
"language": "",
"securityToken": ""
}
EOF

curl -v -H "Content-type: application/json" -X POST -d "@$DATA_FILE" "http://$HOST/" && rm $DATA_FILE || echo "Failed! Request can be found in $DATA_FILE" 
