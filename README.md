# Digital Signature API

Una API REST simple que permite a los usuarios firmar digitalmente documentos y verificar dichas firmas. La API permite generar un par de claves (pública y privada), usar la clave privada para firmar documentos y la clave pública para verificar la autenticidad de la firma.

## Requisitos

- Docker y Docker Compose
- JDK 17
- Maven

## Instalación y Ejecución

1. Clona el repositorio:
    ```bash
    git clone https://github.com/alvaro-freire/digital-signature-api.git
    cd digital-signature-api
    ```

2. Copia el archivo `.env.dist` a `.env`:

    ```bash
    ENCRYPTION_PASSWORD=CHANGE_ME
    API_PASSWORD=CHANGE_ME
    JWT_SECRET=CHANGE_ME
    ```

3. Configura las variables de entorno en el archivo `.env`:

    ```bash
    ENCRYPTION_PASSWORD=your_encryption_password
    API_PASSWORD=your_api_password
    JWT_SECRET=your_jwt_secret
    ```

4. Construye el proyecto:
    ```bash
    ./mvnw clean package
    ```

> Los tests se ejecutan con `./mvnw clean test`

5. Ejecuta la aplicación con Docker Compose:
    ```bash
    docker-compose up --build
    ```

La aplicación estará disponible en `http://localhost:8080`.

## Endpoints

### Autenticación

#### Obtener Token JWT

- **URL**: `/api/authenticate`

- **Método**: `POST`

- **Headers**:
    - `Authorization`: `Bearer your_api_password`

- **Query Params**:
    - `userId`: `your_user_id`
  
- **Respuesta**:
    ```json
    {
        "token": "your_jwt_token"
    }
    ```

#### Ejemplo con `curl`:
```bash
curl -X POST "http://localhost:8080/api/authenticate?userId=your_user_id" -H "Authorization: Bearer your_api_password" -H "Content-Type: application/json" -d '{"userId":"your_user_id"}'
```

### Generar Par de Claves

- **URL**: `/api/keys/generate`

- **Método**: `POST`

- **Headers**:
  - `Authorization`: `Bearer your_jwt_token`

- **Query Params**:
  - `userId`: `your_user_id`

- **Respuesta**:

    ```json
    {
        "id": 1,
        "userId": "your_user_id",
        "publicKey": "base64_encoded_public_key",
        "privateKey": "base64_encoded_private_key"
    }
    ```

#### Ejemplo con `curl`:
```bash
curl -X POST "http://localhost:8080/api/keys/generate?userId=your_user_id" -H "Authorization: Bearer your_jwt_token"
```

### Obtener Par de Claves

- **URL**: `/api/keys/{userId}`

- **Método**: `GET`

- **Headers**:
    - `Authorization`: `Bearer your_jwt_token`

- **Respuesta**:

    ```json
    {
        "id": 1,
        "userId": "your_user_id",
        "publicKey": "base64_encoded_public_key",
        "privateKey": "base64_encoded_private_key"
    }
    ```

#### Ejemplo con `curl`:
```bash
curl -X GET "http://localhost:8080/api/keys/your_user_id" -H "Authorization: Bearer your_jwt_token"
```

### Firmar Documento

- **URL**: `/api/keys/sign`

- **Método**: `POST`

- **Headers**:
    - `Authorization`: `Bearer your_jwt_token`

- **Query Params**:
    - `userId`: `your_user_id`

- **Body**:

    ```json
    {
        "document": "base64_encoded_document"
    }
    ```

- **Respuesta**:

    ```json
    {
        "signature": "base64_encoded_signature"
    }
    ```

#### Ejemplo con `curl`:
```bash
curl -X POST "http://localhost:8080/api/keys/sign?userId=your_user_id" -H "Authorization: Bearer your_jwt_token" -H "Content-Type: application/json" -d '{"document":"base64_encoded_document"}'
```

### Verificar Firma

- **URL**: `/api/keys/verify`

- **Método**: `POST`

- **Headers**:
    - `Authorization`: `Bearer your_jwt_token`

- **Query Params**:
    - `userId`: `your_user_id`

- **Body**:

    ```json
    {
        "document": "base64_encoded_document",
        "signature": "base64_encoded_signature"
    }
    ```

- **Respuesta**:

    ```json
    {
        "valid": true
    }
    ```

#### Ejemplo con `curl`:
```bash
curl -X POST "http://localhost:8080/api/keys/verify?userId=your_user_id" -H "Authorization: Bearer your_jwt_token" -H "Content-Type: application/json" -d '{"document":"base64_encoded_document", "signature":"base64_encoded_signature"}'
```

## Licencia

Este proyecto está bajo la licencia [GPL v3](LICENSE).