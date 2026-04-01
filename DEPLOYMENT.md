# Deploy Advanced MiniGit on Render 🚀

This guide will walk you through deploying your Advanced MiniGit application on Render.com.

## Prerequisites

1. **Render Account**: Sign up at [render.com](https://render.com)
2. **GitHub Repository**: Your code must be on GitHub
3. **Render Plan**: Free plan is sufficient for basic deployment

## Step 1: Prepare Your Repository

1. Push your code to GitHub (including the new `render.yaml` and `Dockerfile.backend`)
2. Ensure your repository is public or connect Render to your private GitHub

## Step 2: Deploy on Render

### Method 1: Using render.yaml (Recommended)

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New"** → **"Web Service"**
3. Connect your GitHub repository
4. Render will automatically detect your `render.yaml` configuration
5. Click **"Create Web Service"**

### Method 2: Manual Setup

#### Backend Service
1. **New Web Service** → Connect GitHub repo
2. **Name**: `advanced-minigit-backend`
3. **Environment**: `Docker`
4. **Dockerfile Path**: `./Dockerfile.backend`
5. **Plan**: Free
6. **Add Environment Variable**:
   - Key: `PORT`
   - Value: `8080`

#### Frontend Service
1. **New Web Service** → Connect GitHub repo
2. **Name**: `advanced-minigit-frontend`
3. **Environment**: `Static Site`
4. **Build Command**: `cd frontend && npm ci && npm run build`
5. **Publish Directory**: `frontend/build`
6. **Plan**: Free
7. **Add Environment Variable**:
   - Key: `REACT_APP_API_URL`
   - Value: `https://your-backend-url.onrender.com`

## Step 3: Configure Environment Variables

### Backend Environment Variables
- `PORT`: 8080 (default)
- `NODE_ENV`: production

### Frontend Environment Variables
- `REACT_APP_API_URL`: Your backend URL (e.g., `https://advanced-minigit-backend.onrender.com`)

## Step 4: Verify Deployment

1. **Backend Health Check**: Visit `https://your-backend-url.onrender.com/api/health`
2. **Frontend**: Visit `https://your-frontend-url.onrender.com`

## Step 5: Custom Domain (Optional)

1. Go to your service settings in Render
2. Click **"Custom Domains"**
3. Add your domain name
4. Update DNS records as instructed by Render

## Troubleshooting

### Common Issues

#### Backend Not Starting
- Check Dockerfile syntax
- Verify port exposure (8080)
- Check Render logs for build errors

#### Frontend Build Fails
- Ensure all dependencies are in package.json
- Check build command: `npm ci && npm run build`
- Verify static files path: `frontend/build`

#### CORS Issues
- Backend should include `Access-Control-Allow-Origin: *`
- Frontend API URL should be correctly set

#### Database Connection
- If using database, ensure proper connection string
- Check database service is running

### Debugging Tips

1. **Check Render Logs**: Go to service → Logs tab
2. **Local Testing**: Test Docker containers locally first
3. **Environment Variables**: Double-check all env vars are set
4. **Network Issues**: Verify service URLs are accessible

## Production Optimizations

### Backend
- Implement proper REST API endpoints
- Add authentication
- Use PostgreSQL/Redis for data persistence
- Implement proper error handling

### Frontend
- Enable production optimizations
- Implement proper error boundaries
- Add loading states
- Optimize bundle size

## Monitoring

### Render Built-in Monitoring
- Service health checks
- Response time metrics
- Error logs
- Resource usage

### Recommended Tools
- **Uptime monitoring**: UptimeRobot, Pingdom
- **Error tracking**: Sentry
- **Performance**: Lighthouse, Web Vitals

## Scaling

### When to Upgrade
- High traffic (>1000 requests/minute)
- Slow response times
- Memory/CPU usage > 80%

### Scaling Options
1. **Upgrade Render Plan**: More resources, higher limits
2. **Horizontal Scaling**: Multiple instances
3. **CDN**: CloudFlare for static assets
4. **Database**: Upgrade to paid PostgreSQL

## Security Best Practices

1. **Environment Variables**: Never commit secrets
2. **HTTPS**: Render provides automatic SSL
3. **Authentication**: Implement proper auth system
4. **Rate Limiting**: Prevent abuse
5. **Input Validation**: Sanitize all inputs

## Support

- **Render Documentation**: [render.com/docs](https://render.com/docs)
- **Community**: [Render Community Forum](https://community.render.com)
- **Support**: support@render.com

---

**Note**: This deployment uses a basic HTTP server for the Java backend. For production, consider implementing a proper REST API using Spring Boot or another framework.
