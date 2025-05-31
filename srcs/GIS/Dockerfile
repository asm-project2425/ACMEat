FROM node:lts-alpine

WORKDIR /gis

COPY . .

RUN npm install

ENV PORT=6002
EXPOSE 6002

CMD [ "npm", "run", "dev" ]
