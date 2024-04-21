# m306-TouGH-Vault-github-backupper

How to run TouGH-Vault with Docker:

```bash
docker network create TouGH-Vault
docker volume create kc-postgres
docker run -d \
    --network TouGH-Vault \
    --name Keycloak-postgres \
    -e POSTGRES_PASSWORD=admin \
    -e POSTGRES_DB=postgres \
    -e POSTGRES_USER=postgres \
    -v kc-postgres:/var/lib/postgresql/data \
    -p 5433:5432 \
    postgres
docker run --name TouGH-Vault-Keycloak -d -p 18080:8080 \
    -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
    -e PROXY_ADDRESS_FORWARDING=true \
    -e KEYCLOAK_FRONTEND_URL=https://tough-vault-keycloak.jzel.online \
    -e KC_HTTP_ENABLED=false -e KC_HOSTNAME_STRICT_HTTPS=true \
    -e KC_PROXY=edge \
    -e KC_HOSTNAME=tough-vault-keycloak.jzel.online \
    -e DB_VENDOR=postgres \
    -e DB_ADDR=TouGH-Vault-postgres \
    -e DB_PORT=5433 \
    -e DB_DATABASE=keycloak \
    -e DB_USER=keycloak \
    -e DB_PASSWORD=admin \
    --network TouGH-Vault \
    quay.io/keycloak/keycloak:23.0.7 start
docker volume create postgres
docker run -d \
    --network TouGH-Vault \
	--name TouGH-Vault-postgres \
	-e POSTGRES_PASSWORD=admin \
	-e PGDATA=/var/lib/postgresql/data/pgdata \
	-v postgres:/var/lib/postgresql/data \
	-p 5432:5432 \
	postgres
docker run --network TouGH-Vault --name TouGH-Vault -d --env-file ./.env -p 8080:8080 jzeladmin2006/tough-vault
docker run --name TouGH-Vault-frontend -d -p 4200:8080 jzeladmin2006/tough-vault-frontend
```

How to remove TouGH-Vault entirely:

```bash
docker stop TouGH-Vault-Keycloak
docker stop TouGH-Vault-postgres
docker stop TouGH-Vault
docker stop TouGH-Vault-frontend
docker remove TouGH-Vault-Keycloak
docker remove TouGH-Vault-postgres
docker remove TouGH-Vault
docker remove TouGH-Vault-frontend
docker network remove TouGH-Vault
docker volume remove postgres
```

TODO: fix Keycloak persistence by doing something like
this (
using https://www.decomposerize.com/): https://keycloak.discourse.group/t/setup-keycloak-version-21-0-2-with-postgres-using-docker/21345/2
