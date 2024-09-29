FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
FROM node:20-alpine
RUN npm install -g http-server
COPY --from=build /app/dist/webpage/browser .
EXPOSE 8080
CMD ["http-server", ".", "-p", "8080"]
