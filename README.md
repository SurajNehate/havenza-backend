# Havenza Backend

This is the backend for the Havenza Premium E-commerce platform. It is a monolithic application built on top of Spring Boot 3.2.0, Java 17, and PostgreSQL.

## Prerequisites
- Java 17 LTS
- Maven
- PostgreSQL running on default port `5432`

## Environment Variables
The application requires several environment variables or application properties to run. Ensure you have the following correctly mapped:

| Variable | Description | Default Local Example |
| -------- | ----------- | ----------------------|
| `SPRING_DATASOURCE_URL` | The PostgreSQL connection string | `jdbc:postgresql://localhost:5432/postgres` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | Secret key for JWT encryption | `9a7b8c...your-secret` |
| `JWT_EXPIRATION` | Expiry time (milliseconds) | `86400000` (1 day) |

## Running Locally

1. **Start your PostgreSQL Database**
   Ensure an instance of PostgreSQL is running with the username/password and Database specified in your `application.yml` file.

2. **Run Spring Boot via Maven**
   ```bash
   mvn spring-boot:run
   ```
   The backend will normally start on `http://localhost:8080`. Note that the `DataSeeder.java` script runs automatically on the first run to insert default products and categories if missing.

## Deployment to Render.com

We use a Multi-Stage `Dockerfile` to automatically build and deploy the Spring Boot application on [Render](https://render.com).

### 1. Database Setup
1. Log into your Render Dashboard.
2. Select **New** > **PostgreSQL**.
3. Create the database and wait for it to be active.
4. Copy the **Internal Database URL** string (e.g., `postgres://user:pass@host:5432/dbname`).

### 2. Live Web Service Setup
1. Back on the Dashboard, click **New** > **Web Service**.
2. Select to build from Git and link your `havenza` GitHub repository.
3. If your code is in a specific folder, enter `havenza-backend` in the **Root Directory** field.
4. Render will automatically detect the **Docker** environment because of your `Dockerfile`.

### 3. Configure Variables
Under **Advanced settings** in the Web Service creation panel, add your environment variables to link your database and secret key contexts:
- `SPRING_DATASOURCE_URL`: The JDBC format of your Internal Database URL (Example: `jdbc:postgresql://host:5432/dbname`)
- `SPRING_DATASOURCE_USERNAME`: Render Database User
- `SPRING_DATASOURCE_PASSWORD`: Render Database Password
- `JWT_SECRET`: A very long, secure random string.

### 4. Deploy!
Click **Create Web Service**. Render will automatically begin building the Maven package using the `Dockerfile` instructions and boot up the Spring Boot app instantly afterwards. You'll receive a public URL (e.g. `https://your-service.onrender.com`) that you must put into your Frontend environment!
