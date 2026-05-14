# World-Quiz

With World Quiz, you can learn the Capital, Maps and Flag from all over the world.

## Deployment

### Cloudflared Tunnel

Create a new Tunnel and map the urls you want to the server and to the client. Add to the environment file from the client the url, you wrote for the server.

```txt
http://caddy:80 -> world-quiz.org
```

Create a `.env` file in the root of the project and add following values to it:

```shell
CLOUDFLARED_TUNNEL_TOKEN=<TunnelToken>
MONGO_URI=<Mongo DB Connection String>
JWT_SECRET=<Jwt Secret>
MAILGUN_API_KEY=<Mailgun Api Key>
MAILGUN_URL=<Mailgun URL>
MAILGUN_SENDER_EMAIL=<Mailgun Sender Email>
```

### JWT Secret Creation

```shell
openssl rand -base64 32
```
