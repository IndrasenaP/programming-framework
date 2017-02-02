##Sending a request
As in other scenarios we expect a request made through the SmartCom protocol. The application creates a
REST input adapter at the port 9697 and it expects messages with the following format:

    {
     "content": <submission_string>,
     "type": "Scenario3",
     "subtype": "submit",
     "sender": <peerId>,
     "conversation": <conversation_id>,
    }

Where `<submission_string>` is a string representing a json with the following schema:
    
 
    { 
     "password": <password>,
     "request": { ... } 
    }


containing the `password` (the password should be valid for the `peerId` when authenticated against
the peer manager), and the json of the request, as described in (API)[https://gitlab.com/smartsociety/orchestration/wikis/SmartSharing/API#ride-requests-post-v090-era].

`peerId` is the peerId of the sender (as known by the peer manager).

`conversationId` is a randomly generated id that will be used as reference for following communications to the peer.
 
 
##Communication
The application will send two communication to peer through SmartCom, they will both keep the same
conversation and type:

1. subtype: `RequestSubmitted` with the request id (as returned by the orchestration manager) as content;
2. subtype: `PlanReady` with the agreed plan id (as returned by the orchestration manaager) as content. 

##Environment variables

The following environment variables are required:
 
* **ORCHESTRATOR_URL**: The base url to the Ridesharing Orchestrator Manager;
* **ORCHESTRATOR_SECRET**: Secret for authentication to the Ridesharing Orchestrator Manager;
* **ORCHESTRATOR_KEY**: Key for authenticatio to the Ridesharing Orchestrator Manager;

The following is instead optional
* **ORCHESTRATOR_POLLING_MS**: Pausing time between pollings, in milliseconds (default: 60000, 1 minute)