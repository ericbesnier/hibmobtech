package com.hibernatus.hibmobtech.network.retrofitError;


import android.util.Log;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Eric on 02/03/2017.
 */
/*
----------------------------------------------------------------------------------------------------
Hypertext Transfer Protocol -- HTTP/1.1 Status Code Definitions:
        https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
        https://fr.wikipedia.org/wiki/Liste_des_codes_HTTP
        http://www.codeshttp.com/
----------------------------------------------------------------------------------------------------
Informational 1xx: Information
----------------------------------------------------------------------------------------------------
        This class of status code indicates a provisional response, consisting only of the Status-Line
        and optional headers, and is terminated by an empty line. There are no required headers
        for this class of status code. Since HTTP/1.0 did not define any 1xx status codes,
        servers MUST NOT send a 1xx response to an HTTP/1.0 client except under experimental conditions.
        A client MUST be prepared to accept one or more 1xx status responses prior to a regular response,
        even if the client does not expect a 100 (Continue) status englishMessage.
        Unexpected 1xx status responses
        MAY be ignored by a user agent.
        Proxies MUST forward 1xx responses, unless the connection between the proxy and its client has
        been closed, or unless the proxy itself requested the generation of the 1xx response.
        (For example, if a proxy adds a "Expect: 100-continue" field when it forwards a request,
        then it need not forward the corresponding 100 (Continue) response(s).)

        Cette classe de réponse est actuellement réservée pour de futures applications,
        et consiste en des messages avec une ligne d'état, des champs d'en-têtes éventuels,
        et terminés par une ligne vide (CRLF,CRLF). HTTP/1.0 ne définit actuellement aucun de ces codes,
        lesquels ne constituent pas une réponse valide à des requêtes HTTP/1.0.
        Il restent cependant exploitables à titre expérimental,
        et dépassent le contexte des présentes spécifications.
100 Continue: Attente de la suite de la requête.
        The client SHOULD continue with its request. This interim response is used to
        inform the client that the initial part of the request has been received and
        has not yet been rejected by the server. The client SHOULD continue by sending
        the remainder of the request or, if the request has already been completed,
        ignore this response. The server MUST send a final response after the request
        has been completed. See section 8.2.3 for detailed discussion of the use and
        handling of this status code.
101 Switching Protocols: Acceptation du changement de protocole.
        The server understands and is willing to comply with the client's request,
        via the Upgrade englishMessage header field (section 14.42), for a change in the
        application protocol being used on this connection. The server will switch
        protocols to those defined by the response's Upgrade header field immediately
        after the empty line which terminates the 101 response.
        The protocol SHOULD be switched only when it is advantageous to do so.
        For example, switching to a newer version of HTTP is advantageous over older
        versions, and switching to a real-time, synchronous protocol might be advantageous
        when delivering resources that use such features.
102 Processing: WebDAV : Traitement en cours (évite que le client dépasse le temps d’attente limite).
------------------------------------------------------------------------------------------------------------------------
Successful 2xx: Succès
------------------------------------------------------------------------------------------------------------------------
        This class of status code indicates that the client's request was successfully received,
        understood, and accepted.

        La requête a abouti. L'information retournée en réponse dépend de la requête émise, comme suit:
        GET    : Une entité correspondant à l'URI visée par la requête est renvoyée au client
        HEAD  : La réponse au client ne doit contenir que les champs d'en-tête à l'exclusion de tout corps d'entité
        POST  : Une entité décrivant le résultat de l'action entreprise.
200 OK: Requête traitée avec succès.
        The request has succeeded. The information returned with the response is dependent
        on the method used in the request, for example:
        GET an entity corresponding to the requested resource is sent in the response;
        HEAD the entity-header fields corresponding to the requested resource are
        sent in the response without any englishMessage-body;
        POST an entity describing or containing the result of the action;
        TRACE an entity containing the request englishMessage as received by the end server.
201 Created: Requête traitée avec succès et création d’un document.
        The request has been fulfilled and resulted in a new resource being created.
        The newly created resource can be referenced by the URI(s) returned in the
        entity of the response, with the most specific URI for the resource given
        by a Location header field. The response SHOULD include an entity containing
        a list of resource characteristics and location(s) from which the user or
        user agent can choose the one most appropriate. The entity format is specified
        by the media type given in the Content-MroType header field. The origin server MUST
        create the resource before returning the 201 status code. If the action cannot be
        carried out immediately, the server SHOULD respond with 202 (Accepted) response instead.
        A 201 response MAY contain an ETag response header field indicating the current value
        of the entity tag for the requested variant just created, see section 14.19.
202 Accepted: Requête traitée, mais sans garantie de résultat.
        The request has been accepted for processing, but the processing has not been completed.
        The request might or might not eventually be acted upon, as it might be disallowed when
        processing actually takes place. There is no facility for re-sending a status code from
        an asynchronous operation such as this.
        The 202 response is intentionally non-committal. Its purpose is to allow a server to
        accept a request for some other process (perhaps a batch-oriented process that is only
        run once per day) without requiring that the user agent's connection to the server
        persist until the process is completed. The entity returned with this response SHOULD
        include an indication of the request's current status and either a pointer to a status
        monitor or some estimate of when the user can expect the request to be fulfilled.
203 Non-Authoritative Information: Information retournée, mais générée par une source non certifiée.
        The returned metainformation in the entity-header is not the definitive set as available
        from the origin server, but is gathered from a local or a third-party copy. The set presented
        MAY be a subset or superset of the original version. For example, including local annotation
        information about the resource might result in a superset of the metainformation known by the
        origin server. Use of this response code is not required and is only appropriate when the response would otherwise be 200 (OK).
204 No Content: Requête traitée avec succès mais pas d’information à renvoyer.
        The server has fulfilled the request but does not need to return an entity-body, and might
        want to return updated metainformation. The response MAY include new or updated metainformation
        in the form of entity-headers, which if present SHOULD be associated with the requested variant.
        If the client is a user agent, it SHOULD NOT change its document view from that which caused
        the request to be sent. This response is primarily intended to allow input for actions to take
        place without causing a change to the user agent's active document view, although any new or
        updated metainformation SHOULD be applied to the document currently in the user agent's active view.
        The 204 response MUST NOT include a englishMessage-body, and thus is always terminated by the first
        empty line after the header fields.
205 Reset Content: Requête traitée avec succès, la page courante peut être effacée.
        The server has fulfilled the request and the user agent SHOULD reset the document view which
        caused the request to be sent. This response is primarily intended to allow input for actions
        to take place via user input, followed by a clearing of the form in which the input is given
        so that the user can easily initiate another input action. The response MUST NOT include
        an entity.
206 Partial Content: Une partie seulement de la ressource a été transmise.
        The server has fulfilled the partial GET request for the resource. The request MUST have
        included a Range header field (section 14.35) indicating the desired range, and MAY have
        included an If-Range header field (section 14.27) to make the request conditional.
        The response MUST include the following header fields:
        - Either a Content-Range header field (section 14.16) indicating
        the range included with this response, or a multipart/byteranges
        Content-MroType including Content-Range fields for each part. If a
        Content-Length header field is present in the response, its
        value MUST match the actual number of OCTETs transmitted in the
        englishMessage-body.
        - Date
        - ETag and/or Content-Location, if the header would have been sent
        in a 200 response to the same request
        - Expires, Cache-Control, and/or Vary, if the field-value might
        differ from that sent in any previous response for the same
        variant
        If the 206 response is the result of an If-Range request that used a strong cache validator
        (see section 13.3.3), the response SHOULD NOT include other entity-headers. If the response
        is the result of an If-Range request that used a weak validator, the response MUST NOT
        include other entity-headers; this prevents inconsistencies between cached entity-bodies
        and updated headers. Otherwise, the response MUST include all of the entity-headers that
        would have been returned with a 200 (OK) response to the same request.
        A cache MUST NOT combine a 206 response with other previously cached content if the ETag
        or Last-Modified headers do not match exactly, see 13.5.4.
        A cache that does not support the Range and Content-Range headers MUST NOT cache 206
        (Partial) responses.
        207	Multi-Status	WebDAV : Réponse multiple.
210	Content Different:	WebDAV : La copie de la ressource côté client diffère de celle du
        serveur (contenu ou propriétés).
226	IM Used:	RFC 32293 : Le serveur a accompli la requête pour la ressource,
        et la réponse est une représentation du résultat d'une ou plusieurs manipulations
        d'instances appliquées à l'instance actuelle.
------------------------------------------------------------------------------------------------------------------------
Redirection 3xx: Redirection
------------------------------------------------------------------------------------------------------------------------
        This class of status code indicates that further action needs to be taken by the user
        agent in order to fulfill the request. The action required MAY be carried out by the
        user agent without interaction with the user if and only if the method used in the
        second request is GET or HEAD. A client SHOULD detect infinite redirection loops,
        since such loops generate network traffic for each redirection.
        Note: previous versions of this specification recommended a
        maximum of five redirections. Content developers should be aware
        that there might be clients that implement such a fixed
        limitation.

        Cette classe de messages précise que le client doit provoquer une action complémentaire
        pour que la requête puisse être conduite jusqu'à sa résolution finale.
        L'action peut être déclenchée par l'utilisateur final si et seulement si la méthode
        invoquée était GET ou HEAD. Un client ne peut automatiquement rediriger une requête
        plus de 5 fois. Il est supposé, si cela arrive, que la re-direction s'effectue
        selon une boucle infinie.
300 Multiple Choices: L’URI demandée se rapporte à plusieurs ressources.
        The requested resource corresponds to any one of a set of representations, each with its
        own specific location, and agent- driven negotiation information (section 12) is being
        provided so that the user (or user agent) can select a preferred representation and
        redirect its request to that location.
        Unless it was a HEAD request, the response SHOULD include an entity containing a list
        of resource characteristics and location(s) from which the user or user agent can choose
        the one most appropriate. The entity format is specified by the media type given in the
        Content- MroType header field. Depending upon the format and the capabilities of
        the user agent, selection of the most appropriate choice MAY be performed automatically.
        However, this specification does not define any standard for such automatic selection.
        If the server has a preferred choice of representation, it SHOULD include the specific
        URI for that representation in the Location field; user agents MAY use the Location field
        value for automatic redirection. This response is cacheable unless indicated otherwise.
301 Moved Permanently: Document déplacé de façon permanente.
        The requested resource has been assigned a new permanent URI and any future references to
        this resource SHOULD use one of the returned URIs. Clients with link editing capabilities
        ought to automatically re-link references to the Request-URI to one or more of the new
        references returned by the server, where possible. This response is cacheable unless
        indicated otherwise.
        The new permanent URI SHOULD be given by the Location field in the response. Unless
        the request method was HEAD, the entity of the response SHOULD contain a short hypertext
        note with a hyperlink to the new URI(s).
        If the 301 status code is received in response to a request other than GET or HEAD,
        the user agent MUST NOT automatically redirect the request unless it can be confirmed by
        the user, since this might change the conditions under which the request was issued.
        Note: When automatically redirecting a POST request after
        receiving a 301 status code, some existing HTTP/1.0 user agents
        will erroneously change it into a GET request.
302 Found/Moved Temporarily: Document déplacé de façon temporaire.
        The requested resource resides temporarily under a different URI. Since the redirection
        might be altered on occasion, the client SHOULD continue to use the Request-URI for future
        requests. This response is only cacheable if indicated by a Cache-Control or Expires header
        field.
        The temporary URI SHOULD be given by the Location field in the response. Unless the request
        method was HEAD, the entity of the response SHOULD contain a short hypertext note with a
        hyperlink to the new URI(s).
        If the 302 status code is received in response to a request other than GET or HEAD, the
        user agent MUST NOT automatically redirect the request unless it can be confirmed by the
        user, since this might change the conditions under which the request was issued.
        Note: RFC 1945 and RFC 2068 specify that the client is not allowed
        to change the method on the redirected request.  However, most
        existing user agent implementations treat 302 as if it were a 303
        response, performing a GET on the Location field-value regardless
        of the original request method. The status codes 303 and 307 have
        been added for servers that wish to make unambiguously clear which
        kind of reaction is expected of the client.
303 See Other: La réponse à cette requête est ailleurs.
        The response to the request can be found under a different URI and SHOULD be retrieved
        using a GET method on that resource. This method exists primarily to allow the output
        of a POST-activated script to redirect the user agent to a selected resource. The new
        URI is not a substitute reference for the originally requested resource. The 303 response
        MUST NOT be cached, but the response to the second (redirected) request might be cacheable.
        The different URI SHOULD be given by the Location field in the response. Unless the request
        method was HEAD, the entity of the response SHOULD contain a short hypertext note with a
        hyperlink to the new URI(s).
        Note: Many pre-HTTP/1.1 user agents do not understand the 303
        status. When interoperability with such clients is a concern, the
        302 status code may be used instead, since most user agents react
        to a 302 response as described here for 303.
304 Not Modified: Document non modifié depuis la dernière requête.
        If the client has performed a conditional GET request and access is allowed, but the document
        has not been modified, the server SHOULD respond with this status code. The 304 response MUST
        NOT contain a englishMessage-body, and thus is always terminated by the first empty line after the
        header fields.
        The response MUST include the following header fields:
        - Date, unless its omission is required by section 14.18.1
        If a clockless origin server obeys these rules, and proxies and clients add their own Date
        to any response received without one (as already specified by [RFC 2068], section 14.19),
        caches will operate correctly.
        - ETag and/or Content-Location, if the header would have been sent
        in a 200 response to the same request
        - Expires, Cache-Control, and/or Vary, if the field-value might
        differ from that sent in any previous response for the same
        variant
        If the conditional GET used a strong cache validator (see section 13.3.3), the response
        SHOULD NOT include other entity-headers. Otherwise (i.e., the conditional GET used a weak
        validator), the response MUST NOT include other entity-headers; this prevents
        inconsistencies between cached entity-bodies and updated headers.
        If a 304 response indicates an entity not currently cached, then the cache MUST disregard
        the response and repeat the request without the conditional.
        If a cache uses a received 304 response to update a cache entry, the cache MUST update
        the entry to reflect any new field values given in the response.
305 Use Proxy: La requête doit être ré-adressée au proxy.
        The requested resource MUST be accessed through the proxy given by the Location field.
        The Location field gives the URI of the proxy. The recipient is expected to repeat this
        single request via the proxy. 305 responses MUST only be generated by origin servers.
        Note: RFC 2068 was not clear that 305 was intended to redirect a
        single request, and to be generated by origin servers only.  Not
        observing these limitations has significant security consequences.
306 (Unused): Code utilisé par une ancienne version de la RFC 26164, à présent réservé.
        The 306 status code was used in a previous version of the specification, is no longer
        used, and the code is reserved.
307 Temporary Redirect: La requête doit être redirigée temporairement vers l’URI spécifiée.
        The requested resource resides temporarily under a different URI. Since the redirection
        MAY be altered on occasion, the client SHOULD continue to use the Request-URI for future
        requests. This response is only cacheable if indicated by a Cache-Control or Expires header
        field.
        The temporary URI SHOULD be given by the Location field in the response. Unless the request
        method was HEAD, the entity of the response SHOULD contain a short hypertext note with a
        hyperlink to the new URI(s) , since many pre-HTTP/1.1 user agents do not understand the 307
        status. Therefore, the note SHOULD contain the information necessary for a user to repeat
        the original request on the new URI.
        If the 307 status code is received in response to a request other than GET or HEAD, the
        user agent MUST NOT automatically redirect the request unless it can be confirmed by the
        user, since this might change the conditions under which the request was issued.
        10.4 Client Error 4xx
        The 4xx class of status code is intended for cases in which the client seems to have erred.
        Except when responding to a HEAD request, the server SHOULD include an entity containing an
        explanation of the error situation, and whether it is a temporary or permanent condition.
        These status codes are applicable to any request method. User agents SHOULD display any
        included entity to the user.
        If the client is sending data, a server implementation using TCP SHOULD be careful to ensure
        that the client acknowledges receipt of the packet(s) containing the response, before the
        server closes the input connection. If the client continues sending data to the server after
        the close, the server's TCP stack will send a reset packet to the client, which may erase the
        client's unacknowledged input buffers before they can be read and interpreted by the HTTP application.
308 Permanent Redirect: La requête doit être redirigée définitivement vers l’URI spécifiée.
310 Too many Redirects: La requête doit être redirigée de trop nombreuses fois,
        ou est victime d’une boucle de redirection.
*/


public class RetrofitErrorHandler {
    public static final String TAG = RetrofitErrorHandler.class.getSimpleName();

    static String CRL = " \r\n\r\n";
    static String CR = " \r";

    protected String englishMessage;
    protected String frenchMessage;
    protected String completeMessage;
    protected int retrofitErrorStatus;



    /*
        An occurred while communicating to the server.
    */
    public void onNetwork(RetrofitError retrofitError){
        setMessages(retrofitError, "Une erreur s'est produite lors de la communication avec le serveur.");
    }

    /*
        An exception was thrown while (de)serializing a body.
    */
    public void onConversion(RetrofitError retrofitError){
        setMessages(retrofitError, "Une exception a été levée lors de la (de)sérialisation du corps du message.");
    }

    /*
        An internal error occurred while attempting to execute a request.
    */
    public void onUnexpected(RetrofitError retrofitError){
        setMessages(retrofitError, "Une erreur interne s'est produite lors de la tentative d'exécution d'une requête.");
    }


    /*  --------------------------------------------------------------------------------------------
        Client Error 4xx: Erreur du client web
        --------------------------------------------------------------------------------------------
        The 4xx class of status code is intended for cases in which the client seems to have erred.
        Except when responding to a HEAD request, the server SHOULD include an entity containing an
        explanation of the error situation, and whether it is a temporary or permanent condition.
        These status codes are applicable to any request method. User agents SHOULD display any
        included entity to the user. If the client is sending data, a server implementation using
        TCP SHOULD be careful to ensure that the client acknowledges receipt of the packet(s)
        containing the response, before the server closes the input connection. If the client
        continues sending data to the server after the close, the server's TCP stack will send a
        reset packet to the client, which may erase the client's unacknowledged input buffers
        before they can be read and interpreted by the HTTP application.

        La classe 4xx de codes d'état est définie pour répondre au cas où il semble que le client
        ait commis une erreur. Si le client n'a pas encore achevé la transmission de sa requête
        lorsqu'il reçoit le code 4xx, alors il doit cesser toute transmission.
        Excepté lorsque ce code répond à une requête de type HEAD, le serveur pourra y inclure
        une entité explicitant la nature de l'erreur, et indiquant s'il s'agit d'une condition
        d'erreur temporaire ou permanente. Ces codes sont valides pour tous les types de requête.
        */

    /*
        400 Bad Request
        The request could not be understood by the server due to malformed syntax. The client SHOULD
        NOT repeat the request without modifications.
    */
    public void on400(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur mauvaise requête:"
                + CRL + "la requête n'a pas pu être comprise par le serveur en raison d'une erreur syntaxe.");
    }

    /*
        401 Unauthorized
        The request requires user authentication. The response MUST include a WWW-Authenticate header
        field (section 14.47) containing a challenge applicable to the requested resource. The client
        MAY repeat the request with a suitable Authorization header field (section 14.8). If the
        request already included Authorization credentials, then the 401 response indicates that
        authorization has been refused for those credentials. If the 401 response contains the same
        challenge as the prior response, and the user agent has already attempted authentication at
        least once, then the user SHOULD be presented the entity that was given in the response,
        since that entity might include relevant diagnostic information. HTTP access authentication
        is explained in "HTTP Authentication: Basic and Digest Access Authentication" [43].
    */
    public void on401(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur d'authentification:"
                + CRL + "la requête nécessite l'authentification des utilisateurs");
    }

    /*
        402 Payment Required
        This code is reserved for future use.
    */
    public void on402(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur de paiement:"
                + CRL +"paiement Requis (réservé pour une utilisation future).");
    }

    /*
        403 Forbidden
        The server understood the request, but is refusing to fulfill it. Authorization will not
        help and the request SHOULD NOT be repeated. If the request method was not HEAD and the
        server wishes to make public why the request has not been fulfilled, it SHOULD describe the
        reason for the refusal in the entity. If the server does not wish to make this information
        available to the client, the status code 404 (Not Found) can be used instead.
    */
    public void on403(RetrofitError retrofitError){
        setMessages(retrofitError, "Requête interdite:"
                + CRL + "le serveur a compris la requête, mais refuse de l'honorer.");
    }

    /*   404 Not Found
        The server has not found anything matching the Request-URI. No indication is given of whether
        the condition is temporary or permanent. The 410 (Gone) status code SHOULD be used if the
        server knows, through some internally configurable mechanism, that an old resource is
        permanently unavailable and has no forwarding address. This status code is commonly
        used when the server does not wish to reveal exactly why the request has been refused,
        or when no other response is applicable.
    */
    public void on404(RetrofitError retrofitError){
        setMessages(retrofitError, "Requête non trouvée:"
                + CRL + "le serveur n'a trouvé aucun élément correspondant à l'URL de requête.");
    }

    /*
        405 Method Not Allowed
        The method specified in the Request-Line is not allowed for the resource identified by the
        Request-URI. The response MUST include an Allow header containing a list of valid methods
        for the requested resource.
    */
    public void on405(RetrofitError retrofitError){
        setMessages(retrofitError, "Méthode non autorisée:"
                + CRL + "la méthode spécifiée dans l'URL n'est pas autorisée pour la ressource identifiée.");
    }

    /*
        406 Not Acceptable
        The resource identified by the request is only capable of generating response entities
        which have content characteristics not acceptable according to the accept headers sent in the request.
        Unless it was a HEAD request, the response SHOULD include an entity containing a list of
        available entity characteristics and location(s) from which the user or user agent can
        choose the one most appropriate. The entity format is specified by the media type given
        in the Content-MroType header field. Depending upon the format and the capabilities of the user
        agent, selection of the most appropriate choice MAY be performed automatically. However,
        this specification does not define any standard for such automatic selection.
        Note: HTTP/1.1 servers are allowed to return responses which are
        not acceptable according to the accept headers sent in the
        request. In some cases, this may even be preferable to sending a
        406 response. User agents are encouraged to inspect the headers of
        an incoming response to determine if it is acceptable.
        If the response could be unacceptable, a user agent SHOULD temporarily stop receipt of more
        data and query the user for a decision on further actions.
    */
    public void on406(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur requête non acceptable:"
                + CRL + "La ressource identifiée par la requête est seulement capable de générer des réponses qui ont des caractéristiques de contenu non acceptables selon la requête.");
    }

    /*
        407 Proxy Authentication Required
        This code is similar to 401 (Unauthorized), but indicates that the client must first authenticate
        itself with the proxy. The proxy MUST return a Proxy-Authenticate header field (section 14.33)
        containing a challenge applicable to the proxy for the requested resource. The client MAY
        repeat the request with a suitable Proxy-Authorization header field (section 14.34).
        HTTP access authentication is explained in "HTTP Authentication: Basic and Digest Access
        Authentication" [43].
        */
    public void on407(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur authentification proxy requise:"
                + CRL + "la requête nécessite l'authentification préalable des utilisateurs via le proxy.");
    }

    /*
        408 Request Timeout
        The client did not produce a request within the time that the server was prepared to wait.
        The client MAY repeat the request without modifications at any later time.
    */
    public void on408(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur expiration du délai de requête:"
                + CRL + "le client n'a pas produit une demande dans le délai que le serveur était prêt à attendre.");
    }

    /*
        409 Conflict
        The request could not be completed due to a conflict with the current state of the resource.
        This code is only allowed in situations where it is expected that the user might be able to
        resolve the conflict and resubmit the request. The response body SHOULD include enough
        information for the user to recognize the source of the conflict. Ideally, the response
        entity would include enough information for the user or user agent to fix the problem;
        however, that might not be possible and is not required.
        Conflicts are most likely to occur in response to a PUT request. For example, if versioning
        were being used and the entity being PUT included changes to a resource which conflict with
        those made by an earlier (third-party) request, the server might use the 409 response to
        indicate that it can't complete the request. In this case, the response entity would likely
        contain a list of the differences between the two versions in a format defined by the response
        Content-MroType
    */
    public void on409(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur conflit de données:"
                + CRL + "La demande n'a pas pu être terminée en raison d'un conflit avec l'état actuel de la ressource.");
    }

    /*
        410 Gone
        The requested resource is no longer available at the server and no forwarding address is known.
        This condition is expected to be considered permanent. Clients with link editing capabilities
        SHOULD delete references to the Request-URI after user approval. If the server does not know,
        or has no facility to determine, whether or not the condition is permanent, the status
        code 404 (Not Found) SHOULD be used instead. This response is cacheable unless indicated otherwise.
                The 410 response is primarily intended to assist the task of web maintenance by notifying
        he recipient that the resource is intentionally unavailable and that the server owners
        desire that remote links to that resource be removed. Such an event is common for limited-time,
        promotional services and for resources belonging to individuals no longer working at the
        server's site. It is not necessary to mark all permanently unavailable resources as "gone"
        or to keep the mark for any length of time -- that is left to the discretion of the server owner.
    */
    public void on410(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur ressource indisponible:"
                + CRL + "la ressource demandée n'est plus disponible sur le serveur et aucune adresse de redirection n'est connue.");
    }

    /*
        411 Length Required
        The server refuses to accept the request without a defined Content- Length. The client MAY
        repeat the request if it adds a valid Content-Length header field containing the length of
        the englishMessage-body in the request englishMessage.
    */
    public void on411(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur longueur requise:"
                + CRL + "le serveur refuse d'accepter la requête sans une longueur de contenu défini.");
    }

    /*
        412 Precondition Failed
        The precondition given in one or more of the request-header fields evaluated to false when
        it was tested on the server. This response code allows the client to place preconditions on
        the current resource metainformation (header field data) and thus prevent the requested method
        from being applied to a resource other than the one intended.
    */
    public void on412(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur précondition erronnée:"
                + CRL + "la condition préalable donnée dans un ou plusieurs des champs d'en-tête de requête a été évaluée à fausse.");
    }

    /*
        413 Request Entity Too Large
        The server is refusing to process a request because the request entity is larger than the server
        is willing or able to process. The server MAY close the connection to prevent the client from
        continuing the request.
        If the condition is temporary, the server SHOULD include a Retry- After header field to
        indicate that it is temporary and after what time the client MAY try again.
    */
    public void on413(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur données de requête trop grande:"
                + CRL + "le serveur refuse de traiter une requête parce que l'entité de requête est plus grande que le serveur est disposé ou capable de traiter.");
    }

    /*
        414 Request-URI Too Long
        The server is refusing to service the request because the Request-URI is longer than the
        server is willing to interpret. This rare condition is only likely to occur when a client
        has improperly converted a POST request to a GET request with long query information,
        when the client has descended into a URI "black hole" of redirection
        (e.g., a redirected URI prefix that points to a suffix of itself),
        or when the server is under attack by a client attempting to exploit security
        holes present in some servers using fixed-length buffers for reading or
        manipulating the Request-URI.
    */
    public void on414(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur URL trop longue:"
                + CRL + "le serveur refuse de traiter la requête parce que l'URL de requête est plus longue que la requête serveur est prêt à interpréter.");
    }

    /*
        415 Unsupported Media MroType
        The server is refusing to service the request because the entity of the request is in a
        format not supported by the requested resource for the requested method.
    */
    public void on415(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur type de média non supporté:"
                + CRL + "le serveur refuse de traiter la demande parce que l'entité de la requête est dans un format non pris en charge par la ressource demandée pour la méthode demandée.");
    }

    /*
        416 Requested Range Not Satisfiable
        A server SHOULD return a response with this status code if a request included a Range
        request-header field (section 14.35), and none of the range-specifier values in this
        field overlap the current extent of the selected resource, and the request did not
        include an If-Range request-header field. (For byte-ranges, this means that the first- byte-pos
        of all of the byte-range-spec values were greater than the current length of the selected resource.)
        When this status code is returned for a byte-range request, the response SHOULD include a
        Content-Range entity-header field specifying the current length of the selected resource
        (see section 14.16). This response MUST NOT use the multipart/byteranges content- type.
    */
    public void on416(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur plage de requête non satisfaisante.");
    }

    /*
        417 Expectation Failed
        The expectation given in an Expect request-header field (see section 14.20) could not be met
        by this server, or, if the server is a proxy, the server has unambiguous evidence that
        the request could not be met by the next-hop server.
    */
    public void on417(RetrofitError retrofitError){
        setMessages(retrofitError, " Erreur échec de l'attente:"
                + CRL + "les durées d'attente indiquées dans un champ d'en-tête de requête n'ont pas pu être satisfaites.");
    }

    /*
        --------------------------------------------------------------------------------------------
        Server Error 5xx: Erreur du serveur / du serveur d'application
        --------------------------------------------------------------------------------------------
        Response status codes beginning with the digit "5" indicate cases in which the server is aware
        that it has erred or is incapable of performing the request. Except when responding to a HEAD
        request, the server SHOULD include an entity containing an explanation of the error situation,
        and whether it is a temporary or permanent condition. User agents SHOULD display any included
        entity to the user. These response codes are applicable to any request method.

        Les réponses de code d'état 5xx indiquent une situation dans laquelle le serveur sait
        qu'il est la cause de l'erreur, ou est incapable de fournir le service demandé,
        bien que la requête ait été correctement formulée. Si le client reçoit cette réponse alors
        qu'il n'a pas encore terminé d'envoyer des données, il doit cesser immédiatement toute
        émission vers le serveur. Excepté lorsque la requête invoquée est de type HEAD, le serveur
        peut inclure une entité décrivant les causes de l'erreur, et s'il s'agit d'une condition
        permanente ou temporaire. Ces réponses s'appliquent quelque soit la requête,
        et ne nécessitent pas de champs d'en-tête particuliers.
    */

    /*
        500 Internal Server Error
        The server encountered an unexpected condition which prevented it from fulfilling the request.
    */
    public void on500(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur interne du serveur:"
                + CRL + "le serveur a rencontré une condition inattendue qui l'a empêché de satifaire la requête.");
    }
    /*
        501 Not Implemented
        The server does not support the functionality required to fulfill the request. This is the
        appropriate response when the server does not recognize the request method and is not capable
        of supporting it for any resource.
    */
    public void on501(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur non implémenté:"
                + CRL + "le serveur ne prend pas en charge la fonctionnalité requise pour répondre à la requête.");
    }

    /*
        502 Bad Gateway
        The server, while acting as a gateway or proxy, received an invalid response from the upstream
        server it accessed in attempting to fulfill the request.
    */
    public void on502(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur mauvaise passerelle:"
                + CRL + "le serveur, en tant que passerelle ou proxy, a reçu une réponse non valide du serveur en amont auquel il a accédé en essayant de répondre à la requête.");
    }

    /*
        503 Service Unavailable
        The server is currently unable to handle the request due to a temporary overloading or
        maintenance of the server. The implication is that this is a temporary condition which
        will be alleviated after some delay. If known, the length of the delay MAY be indicated
        in a Retry-After header. If no Retry-After is given, the client SHOULD handle the response
        as it would for a 500 response.
                Note: The existence of the 503 status code does not imply that a
        server must use it when becoming overloaded. Some servers may wish
        to simply refuse the connection.
    */
    public void on503(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur service indisponible:"
                + CRL + "le serveur est actuellement incapable de traiter la requête en raison d'une surcharge temporaire ou de la maintenance du serveur.");
    }

    /*
        504 Gateway Timeout
        The server, while acting as a gateway or proxy, did not receive a timely response from the
        upstream server specified by the URI (e.g. HTTP, FTP, LDAP) or some other auxiliary
        server (e.g. DNS) it needed to access in attempting to complete the request.
        Note: Note to implementors: some deployed proxies are known to
        return 400 or 500 when DNS lookups time out.
    */
    public void on504(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur délai passerelle:"
                + CRL + "le serveur, agissant en tant que passerelle, n'a pas reçu de réponse du serveur en amont dont il avait besoin pour accéder à la requête.");
    }

    /*
        505 HTTP Version Not Supported
        The server does not support, or refuses to support, the HTTP protocol version that was used
        in the request englishMessage. The server is indicating that it is unable or unwilling to complete
        the request using the same major version as the client, as described in section 3.1, other
        than with this error englishMessage. The response SHOULD contain an entity describing why that
        version is not supported and what other protocols are supported by that server.
    */
    public void on505(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur version HTTP non supportée:"
                + CRL + "le serveur ne prend pas en charge, ou refuse de prendre en charge, la version de protocole HTTP utilisée dans la requête.");
    }

    public void onNoResponse(RetrofitError retrofitError){
        setMessages(retrofitError, "Erreur:"
                + CRL + "Pas de réponse du réseau.");
    }

    public void onDefault(RetrofitError retrofitError){
        setMessages(retrofitError, "");
    }

    public void manageRetrofitError(RetrofitError retrofitError) {
        Log.e(TAG, "manageRetrofitError");
        if(retrofitError == null) return;
        Response response = retrofitError.getResponse();
        if (response != null) {
            Log.e(TAG, "manageRetrofitError: retrofitError=" + retrofitError
                    + " retrofitError.getKind()=" + retrofitError.getKind()
                    + " response.getStatus()=" + response.getStatus());
            retrofitErrorStatus = retrofitError.getResponse().getStatus();
        }
        else{
            Log.e(TAG, "manageRetrofitError: retrofitError=" + retrofitError
                    + " retrofitError.getKind()=" + retrofitError.getKind()
                    + " ERREUR sans numero d'erreur !");
            retrofitErrorStatus = 0;
        }
        switch (retrofitError.getKind()) {
            case NETWORK:
                onNetwork(retrofitError);
                break;
            case UNEXPECTED:
                onUnexpected(retrofitError);
                break;
            case CONVERSION:
                onConversion(retrofitError);
                break;
            case HTTP:
                manageHttpResponse(retrofitError);
                break;
            default:
                throw new IllegalStateException("Unknown error kind: " + retrofitError.getKind(), retrofitError);
        }

        Log.e(TAG, "manageRetrofitError: completeMessage=" + completeMessage);
    }


    public void setMessages(RetrofitError retrofitError, String frenchMessage) {
        setEnglishMessage(retrofitError);
        setFrenchMessage(frenchMessage);
        setCompleteMessage(englishMessage + CR + frenchMessage);
        Log.e(TAG, completeMessage);
    }

    public void setEnglishMessage(RetrofitError retrofitError){
        if(retrofitErrorStatus != 0)
            englishMessage = retrofitError.getKind() + " ERROR " + retrofitErrorStatus + " " + retrofitError.toString();
        else
            englishMessage = retrofitError.getKind() + " ERROR " + retrofitError.toString();
    }

    private void manageHttpResponse(RetrofitError retrofitError) {
        Log.d(TAG, "manageHttpResponse: retrofitError=" + retrofitError);
        Response response = retrofitError.getResponse();
        if (response != null) {
            Log.e(TAG, "manageHttpResponse: response.getStatus()=" + response.getStatus());

            switch (response.getStatus()) {
                case 400:
                    on400(retrofitError);
                    break;
                case 401:
                    on401(retrofitError);
                    break;
                case 402:
                    on402(retrofitError);
                    break;
                case 403:
                    on403(retrofitError);
                    break;
                case 404:
                    on404(retrofitError);
                    break;
                case 405:
                    on405(retrofitError);
                    break;
                case 406:
                    on406(retrofitError);
                    break;
                case 407:
                    on407(retrofitError);
                    break;
                case 408:
                    on408(retrofitError);
                    break;
                case 409:
                    on409(retrofitError);
                    break;
                case 410:
                    on410(retrofitError);
                    break;
                case 411:
                    on411(retrofitError);
                    break;
                case 412:
                    on412(retrofitError);
                    break;
                case 413:
                    on413(retrofitError);
                    break;
                case 414:
                    on414(retrofitError);
                    break;
                case 415:
                    on415(retrofitError);
                    break;
                case 416:
                    on416(retrofitError);
                    break;
                case 417:
                    on417(retrofitError);
                    break;
                case 500:
                    on500(retrofitError);
                    break;
                case 501:
                    on501(retrofitError);
                    break;
                case 502:
                    on502(retrofitError);
                    break;
                case 503:
                    on503(retrofitError);
                    break;
                case 504:
                    on504(retrofitError);
                    break;
                case 505:
                    on505(retrofitError);
                    break;
                default:
                    onDefault(retrofitError);
            }
        }
        else{
            onNoResponse(retrofitError);
        }
    }

    public String getEnglishMessage() {
        return englishMessage;
    }

    public void setEnglishMessage(String englishMessage) {
        this.englishMessage = englishMessage;
    }

    public String getFrenchMessage() {
        return frenchMessage;
    }

    public void setFrenchMessage(String frenchMessage) {
        this.frenchMessage = frenchMessage;
    }

    public String getCompleteMessage() {
        return completeMessage;
    }

    public void setCompleteMessage(String completeMessage) {
        this.completeMessage = completeMessage;
    }
}
