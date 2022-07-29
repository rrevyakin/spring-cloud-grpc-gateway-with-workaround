**Workaround description**

When preparing a response in the netty implementation, the endStream http/2 flag is set - it depends on what type the nettyResponse variable will be in HttpServerOperations: endStream=true is set if nettyResponse instanceof LastHttpContent. After contacting the server, HttpClientResponse res contains the correct response. It can be DefaultFullHttpResponse (implements LastHttpContent) or DefaultHttpResponse (does not implement LastHttpContent). 
The problem is that when creating HttpServerOperations, the nettyResponse field is instantiated by DefaultHttpResponse (it is then populated with data from HttpClientResponse res).
That is, even if HttpClientResponse was DefaultFullHttpResponse in the nettyResponse field, HttpServerOperations will still have DefaultHttpResponse, which leads to the fact that when sending a response from gateway to the client, the response instanceOf LastHttpContent check is always false, which in turn leads to setting endStream=false. That is, in the process of preparing a response from the server to be sent to the client, endStream=true is always lost.

CustomNettyRoutingFilter is a copy of org.springframework.cloud.gateway.filter.NettyRoutingFilter with additional method - copyResponse. Its purpose is to set the nettyResponse value from HttpClientResponse res to the nettyResponse of the HttpServerOperations object. Respectively, when the server responds with an error, a DefaultFullHttpResponse is generated, passed to HttpServerOperations, because of this, the check for LastHttpContent passes and endStream=true is set in the response to the client. As a result of the implementation of the described workaround solution, gateway began to correctly respond not only to successful requests, 
but also to requests that lead to errors on the server.

com.example.gateway.CustomNettyRoutingFilter.copyResponse:

```
private static void copyResponse(HttpClientResponse clientResponse, ServerHttpResponse serverResponse) {
    HttpResponse realHttpResponse = clientResponse.nettyResponse();
    ((HttpServerOperations) ((AbstractServerHttpResponse) serverResponse).getNativeResponse())
            .setNettyResponse(realHttpResponse);
}
```
