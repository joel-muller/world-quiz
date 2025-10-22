# World-Quiz

> [!IMPORTANT]
> This application is still under development

With World Quiz, you can learn the Capital, Maps and Flag from all over the world. 

## Deployment

### Cloudflared Tunnel

Create a new Tunnel and map the urls you want to the server and to the client. Add to the environment file from the client the url, you wrote for the server.

```txt
urlserver -> http://server:8080
urlclient -> http://client:8080
```

Create a `.env` file in the root of the project and add following values to it:

```shell
CLOUDFLARED_TUNNEL_TOKEN=<TunnelToken>
```
