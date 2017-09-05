#
# Dev Postgres image
#

FROM postgres:9.5

MAINTAINER Carlos Mecha

ENV POSTGRES_DB=notebooks POSTGRES_USER=notebooks POSTGRES_PASSWORD=mypass

# Add just provision script to the dev image
ADD 00-provision.sql /docker-entrypoint-initdb.d/
ADD 01-users.sql /docker-entrypoint-initdb.d/

