PredictionIO Engine Heroku
--------------------------

Run Locally:

1. Start Postgres
1. Set your PredictionIO app's access key and app name in your env vars:

        export ACCESS_KEY=<YOUR ACCESS KEY>
        export APP_NAME=<YOUR APP NAME>

    Note: These values come from the apps defined in your event server.

1. Train the app:

        source bin/env.sh && ./sbt "runMain TrainApp"

1. Start the server:

        source bin/env.sh && ./sbt "runMain ServerApp"

1. Check the status of your engine:

    http://localhost:8000

1. Check out the recommendations for an item:

    > Note: Must be an item that has events

        curl -H "Content-Type: application/json" -d '{ "items": ["i11"], "num": 4 }' -k http://localhost:8000/queries.json


Run on Heroku:

1. Deploy: [![Deploy on Heroku](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)
1. Attach your PredictionIO Event Server's Postgres:

        heroku addons:attach YOUR-ADDON-ID

    Note: You can find out `<YOUR-ADDON-ID>` by running: `heroku addons -a <YOUR EVENT SERVER HEROKU APP NAME>`

1. Configure the Heroku app:

        heroku config:set ACCESS_KEY=<YOUR APP ACCESS KEY> APP_NAME=<APP NAME> EVENT_SERVER_IP=<YOUR EVENT SERVER HOSTNAME> EVENT_SERVER_PORT=80

1. Train the app:

        heroku run train

1. Restart the app to load the new training data:

        heroku restart
