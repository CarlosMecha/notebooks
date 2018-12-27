# Notebooks

## Requirements
- Java 1.8
- Postgres 9.5

## Run

```bash
mvn clean package
java -jar notebooks-<version>.jar  > `date +%Y-%m-%d-%H-%M`.log
```

### Docker database
 
```bash
docker build --rm -t carlosmecha/notebooks-db:latest .
docker run --name database -p '5432:5432' -e POSTGRES_PASSWORD=mypass -e POSTGRES_USER=notebooks -e POSTGRES_DB=notebooks carlosmecha/notebooks-db
``` 
