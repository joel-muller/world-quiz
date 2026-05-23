# Server World Quiz

## Check and apply formatting

```shell
mvn spotless:check
mvn spotless:apply
```

## Run Project Locally

Start the docker compose in `local/compose.yml` and export the environment variables (see also readme from Project). Add then following `.env` file into the `/server/` directory:

```shell
JWT_SECRET=<Jwt Secret>
MAILGUN_API_KEY=<Mailgun Api Key>
MAILGUN_URL=<Mailgun URL>
MAILGUN_SENDER_EMAIL=<Mailgun Sender Email>
MONGO_INITDB_ROOT_USERNAME=root
MONGO_INITDB_ROOT_PASSWORD=example
MONGO_URI=mongodb://${MONGO_INITDB_ROOT_USERNAME}:${MONGO_INITDB_ROOT_PASSWORD}@localhost:27017/world-quiz?authSource=admin
ME_CONFIG_BASICAUTH_USERNAME=Mongo
ME_CONFIG_BASICAUTH_PASSWORD=Express
MAILGUN_LIMIT_PER_DAY=80
MAILGUN_LIMIT_PER_DAY_PER_USER=4
```

Export it:

```shell
export $(cat .env | xargs)
```

Start Springboot with maven

```shell
mvn spring-boot:run
```
