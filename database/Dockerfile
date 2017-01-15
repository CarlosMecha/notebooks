#
# Dev Postgres image
#

FROM postgres:9.5

MAINTAINER Carlos Mecha

# Add just provision script to the dev image
ADD 00-provision.sql /docker-entrypoint-initdb.d/
ADD 01-users.sql /docker-entrypoint-initdb.d/

