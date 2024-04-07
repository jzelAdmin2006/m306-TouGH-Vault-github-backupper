# m306-TouGH-Vault-github-backupper

TODO use keycloak prod mode

How to run TouGH-Vault with Docker:

```bash
docker network create TouGH-Vault
docker run --name TouGH-Vault-Keycloak -d -p 18080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
    -e PROXY_ADDRESS_FORWARDING=true -e KEYCLOAK_FRONTEND_URL=https://tough-vault-keycloak.jzel.online \
    -e KC_HTTP_ENABLED=false -e KC_HOSTNAME_STRICT_HTTPS=true \
    -e KC_PROXY=edge quay.io/keycloak/keycloak:23.0.7 start-dev
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
