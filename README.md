PredictionIO Engine Heroku
--------------------------

Run Locally:

1. Start Postgres
1. Train the app:

        source bin/env.sh && activator runMain TrainApp

1. Start the server:

        source bin/env.sh && activator runMain ServerApp


Run on Heroku:

1. Deploy
1. Attach the eventserver's Postgres:

        heroku addons:attach YOUR-ADDON-ID


1. Configure the Heroku app:

        heroku config:set ACCESS_KEY=<YOUR APP ACCESS KEY> EVENT_SERVER_IP=<YOUR EVENT SERVER HOSTNAME> EVENT_SERVER_PORT=80

1. Train the app:

        heroku run train
        heroku restart


Demo Data:
```
for i in {1..5}; do curl -i -X POST http://localhost:7070/events.json?accessKey=$ACCESS_KEY -H "Content-Type: application/json" -d "{ \"event\" : \"\$set\", \"entityType\" : \"user\", \"entityId\" : \"u$i\" }"; done

for i in {1..50}; do curl -i -X POST http://localhost:7070/events.json?accessKey=$ACCESS_KEY -H "Content-Type: application/json" -d "{ \"event\" : \"\$set\", \"entityType\" : \"item\", \"entityId\" : \"i$i\", \"properties\" : { \"categories\" : [\"c1\", \"c2\"] } }"; done

for i in {1..5}; do curl -i -X POST http://localhost:7070/events.json?accessKey=$ACCESS_KEY -H "Content-Type: application/json" -d "{ \"event\" : \"view\", \"entityType\" : \"user\", \"entityId\" : \"u$i\",  \"targetEntityType\" : \"item\", \"targetEntityId\" : \"i$(( ( RANDOM % 50 )  + 1 ))\" }"; done

> Note: Must be an item that has events
curl -H "Content-Type: application/json" -d '{ "items": ["i11"], "num": 4 }' -k http://localhost:8000/queries.json

```
