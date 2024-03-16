# m306-TouGH-Vault-github-backupper

How to run PostgreSQL in a docker container:

```bash
docker volume create postgres
docker run -d \
	--name TouGH-Vault-postgres \
	-e POSTGRES_PASSWORD=admin \
	-e PGDATA=/var/lib/postgresql/data/pgdata \
	-v postgres:/var/lib/postgresql/data \
	-p 5432:5432 \
	postgres
```
