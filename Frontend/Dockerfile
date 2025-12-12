FROM node:18-alpine AS frontend
WORKDIR /app

# Kopiraj package datoteke
COPY package*.json ./

# Namesti odvisnosti
RUN npm install

# Kopiraj source kodo
COPY . .

# Gradi aplikacijo (če build script obstaja) ali kopiraj public v dist
RUN npm run build || (mkdir -p dist && cp -r public/* dist/)

FROM nginx:alpine
# Kopiraj gradbene datoteke iz build faze (podobno kot originalni Dockerfile)
COPY --from=frontend /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
