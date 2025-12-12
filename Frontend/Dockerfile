FROM node:18-alpine AS frontend
WORKDIR /app

# Kopiraj package datoteke
COPY package*.json ./

# Namesti odvisnosti
RUN npm install

# Kopiraj source kodo
COPY . .

# Določi ali naj se builda ali kopira public (nastavi z --build-arg SKIP_BUILD=true)
ARG SKIP_BUILD=false
RUN if [ "$SKIP_BUILD" = "true" ]; then \
      mkdir -p dist && cp -r public/* dist/; \
    else \
      npm run build || (mkdir -p dist && cp -r public/* dist/); \
    fi

FROM nginx:alpine
# Kopiraj gradbene datoteke iz build faze
COPY --from=frontend /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
