FROM node:lts-alpine

WORKDIR /shipping_management

COPY . .

RUN npm install

ENV PORT=3000
EXPOSE 3000

CMD [ "npm", "run", "dev" ]
