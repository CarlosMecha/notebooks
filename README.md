# Notebooks

## Requirements
- Java 1.8
- Postgres 9.6

## Run in development

The `SECURITY_DISABLE=true` environment flag removes the authentication check and injects the default user. Use only for development.
```bash
mvn clean package
SECURITY_DISABLE=true java -jar notebooks-<version>.jar
```

## SSSO
[SSSO](https://github.com/CarlosMecha/ssso) provides authentication for Notebooks. See how you can deploy it behind a Nginx server in
the [docker-compose](/docker-compose.yml) configuration.

### Docker database
 
```bash
docker build --rm -t carlosmecha/notebooks-db:latest .
docker run --name database -p '5432:5432' -e POSTGRES_PASSWORD=mypass -e POSTGRES_USER=notebooks -e POSTGRES_DB=notebooks carlosmecha/notebooks-db
``` 
