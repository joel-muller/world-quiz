# Server World Quiz

## Check and apply formatting

```shell
mvn spotless:check
mvn spotless:apply
```

## Run Project Locally

Start the docker compose in `local/compose.yml` and export the environment variables (see also readme from Project)

```shell
export $(cat .env | xargs)
```

Start Springboot with maven

```shell
mvn spring-boot:run
```
