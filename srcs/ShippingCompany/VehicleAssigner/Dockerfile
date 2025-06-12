FROM node:lts-alpine

WORKDIR /vehicle_assigner

COPY . .

RUN npm install

ENV PORT=3000
EXPOSE 3000

CMD [ "npm", "run", "dev" ]
