#
# Dev Postgres image
#

FROM postgres:9.6

ENV POSTGRES_DB=notebooks POSTGRES_USER=notebooks POSTGRES_PASSWORD=mypass

# Add just provision script to the dev image
ADD 00-provision.sql /docker-entrypoint-initdb.d/

