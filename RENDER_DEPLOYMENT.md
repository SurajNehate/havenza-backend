# Render Deployment Instructions for Havenza Backend

Follow these steps to deploy the Spring Boot backend to Render:

1. **Push your code to GitHub/GitLab.**
   Ensure the `havenza-backend` folder is at the root of a repository or configure the root directory in Render.

2. **Create a new Web Service on Render.**
   - Connect your Git repository.
   - **Environment:** `Docker` (Render will automatically detect the `Dockerfile` and build it).
   - **Branch:** `main` (or your preferred branch).

3. **Configure Environment Variables in Render.**
   Add the following variables in the Render dashboard:
   
   | Key | Value (Example) |
   |-----|-----------------|
   | `DATASOURCE_URL` | `jdbc:postgresql://ep-silent-frog-837424.us-east-2.aws.neon.tech/neondb?sslmode=require` |
   | `DATASOURCE_USERNAME` | *(From your Neon dashboard)* |
   | `DATASOURCE_PASSWORD` | *(From your Neon dashboard)* |
   | `JWT_SECRET` | *(Generate a secure base64 string or 256-bit key)* |
   | `CORS_ALLOWED_ORIGINS`| `https://your-frontend-url.netlify.app` |

4. **Deploy.**
   Render will automatically build the web service using the provided multi-stage `Dockerfile`. 
   
5. **Verify.**
   Once deployed, append `/swagger-ui/index.html` or `/v3/api-docs` to your new Render URL to view the live OpenAPI documentation.
