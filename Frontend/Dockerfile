FROM node:18-alpine AS frontend
WORKDIR /app

# Kopiraj package datoteke
COPY package*.json ./

# Namesti odvisnosti
RUN npm install

# Kopiraj source kodo
COPY . .

# Gradi aplikacijo ali kopiraj public v dist
RUN if npm run | grep -q "build"; then \
      npm run build; \
    else \
      mkdir -p dist && cp -r public/* dist/; \
    fi

FROM nginx:alpine
# Kopiraj gradbene datoteke iz build faze
COPY --from=frontend /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
