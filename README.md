# My Bank

## Requirements
- Java 1.8
- Postgres 9.5

## Run

```bash
java -jar notebooks-<version>.jar --server.ssl.key-store-password=mypass > `date +%Y-%m-%d-%H-%M`.log
```
### Self-signed certificate

Make sure that the certificate keystore is located in the same folder than the jar file.

```bash
 keytool -genkey -alias cloud -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
```

### Docker database
 
```bash
docker build --rm -t carlosmecha/notebooks-db:latest .
docker run --name database -p '5432:5432' -e POSTGRES_PASSWORD=mypass -e POSTGRES_USER=notebooks -e POSTGRES_DB=notebooks carlosmecha/notebooks-db
``` 

## Backups
 
### Cron
 
```cron
0 10 * * * /bin/bash backup.sh
0 0 * * 1 /bin/bash compress.sh
```
