FROM node:22

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 3000

ENV HOST=0.0.0.0
ENV CHOKIDAR_USEPOLLING=true

CMD ["npm", "start"]
