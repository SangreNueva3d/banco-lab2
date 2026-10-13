[![CI/CD Pipeline](https://github.com/SangreNueva3d/banco-lab2/actions/workflows/build.yml/badge.svg)](https://github.com/SangreNueva3d/banco-lab2/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sangrenueva3d_banco-lab2&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sangrenueva3d_banco-lab2)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sangrenueva3d_banco-lab2&metric=coverage)](https://sonarcloud.io/summary/new_code?id=sangrenueva3d_banco-lab2)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=sangrenueva3d_banco-lab2&metric=bugs)](https://sonarcloud.io/summary/new_code?id=sangrenueva3d_banco-lab2)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=sangrenueva3d_banco-lab2&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=sangrenueva3d_banco-lab2)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=sangrenueva3d_banco-lab2&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=sangrenueva3d_banco-lab2)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=sangrenueva3d_banco-lab2&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=sangrenueva3d_banco-lab2)

# Banco - API REST con CI/CD

API REST de un banco sencillo hecha con Spring Boot 3, Java 17 y MySQL.
Permite gestionar clientes y hacer transferencias entre cuentas.

Incluye integración con GitHub Actions, SonarCloud (análisis estático y cobertura con JaCoCo),
Docker Hub y despliegue en Render.

## Operaciones

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/customers` | Listar clientes |
| GET | `/api/customers/{id}` | Obtener un cliente |
| POST | `/api/customers` | Crear un cliente |
| PUT | `/api/customers/{id}` | Actualizar un cliente |
| DELETE | `/api/customers/{id}` | Eliminar un cliente |
| POST | `/api/transactions` | Transferir dinero entre dos cuentas |
| GET | `/api/transactions/{accountNumber}` | Historial de una cuenta |
| PUT | `/api/transactions/{id}` | Actualizar una transacción |
| DELETE | `/api/transactions/{id}` | Eliminar una transacción |

## Estructura de carpetas

- `src/main/java`: código principal (controller, service, repository, entity, DTO, mapper).
- `src/test/java`: pruebas unitarias con JUnit 5 y Mockito.
- `.github/workflows/build.yml`: pipeline de CI/CD.
- `Dockerfile`: imagen Docker de la aplicación.

## Cómo ejecutarla en local

Requisitos: JDK 17, Maven (incluido como `mvnw`) y un MySQL local.

```shell
./mvnw spring-boot:run
```

La app usa por defecto `localhost:3306` y el puerto `8088`. Para otra base de datos se definen
las variables de entorno `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` (y `PORT` para el puerto).

## Cómo ejecutar las pruebas y la cobertura

```shell
./mvnw verify
```

El reporte de cobertura queda en `target/site/jacoco/index.html`.

## Pipeline de CI/CD

1. **Unit tests**: ejecuta las pruebas unitarias.
2. **SonarCloud analysis**: análisis estático y cobertura (depende de las pruebas).
3. **Build**: construye el JAR y lo guarda como artefacto.
4. **Docker image**: construye y publica la imagen en Docker Hub (solo desde `main`).
5. **Deploy to Render**: le avisa a Render que descargue la imagen nueva (solo desde `main`).

Las ramas `feature/**` y los pull requests ejecutan los tres primeros pasos, sin publicar ni desplegar.

## Imagen Docker

```shell
docker pull usuariodocker/banco-api:latest
docker run -p 8088:8088 usuariodocker/banco-api
```

## Despliegue en la nube

Disponible en: https://banco-api-latest.onrender.com
