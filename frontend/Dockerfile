FROM node:18 AS build
WORKDIR /workspace
COPY frontend/package*.json ./
RUN npm install --no-audit --no-fund
COPY frontend/ ./
ARG VITE_API_BASE
# Provide a default if build arg not supplied. Vite will inline VITE_API_BASE during build.
ENV VITE_API_BASE=${VITE_API_BASE:-http://localhost:8080/api}
RUN npm run build

FROM nginx:stable-alpine
COPY --from=build /workspace/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
