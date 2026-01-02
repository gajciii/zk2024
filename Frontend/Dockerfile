FROM node:18-alpine AS frontend
WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

ARG SKIP_BUILD=false
RUN if [ "$SKIP_BUILD" = "true" ]; then \
      mkdir -p dist && cp -r public/* dist/; \
    else \
      npm run build || (mkdir -p dist && cp -r public/* dist/); \
    fi

FROM nginx:alpine
COPY --from=frontend /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
