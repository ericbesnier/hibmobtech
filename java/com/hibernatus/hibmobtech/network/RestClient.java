package com.hibernatus.hibmobtech.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.authenticator.AuthenticatorApiService;
import com.hibernatus.hibmobtech.authenticator.AuthenticatorUtils;
import com.hibernatus.hibmobtech.authenticator.User;
import com.hibernatus.hibmobtech.equipment.EquipmentApiService;
import com.hibernatus.hibmobtech.machine.MachineApiService;
import com.hibernatus.hibmobtech.mrorequest.MroRequestApiService;
import com.hibernatus.hibmobtech.network.restError.RestErrorHandler;
import com.hibernatus.hibmobtech.site.SiteApiService;
import com.hibernatus.hibmobtech.sparepart.SparePartApiService;
import com.hibernatus.hibmobtech.tracking.TrackingApiKeyInterceptor;
import com.hibernatus.hibmobtech.tracking.TrackingApiService;
import com.hibernatus.hibmobtech.utils.ItemTypeAdapterFactory;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by tgo on 24/10/15.
 */
public class RestClient {
    private static final String TAG = RestClient.class.getSimpleName();

    private Context context;
    private CookieStore cookieStore;
    private AuthenticatorApiService authService;
    private SiteApiService siteService;
    private EquipmentApiService equipmentService;
    private SparePartApiService sparePartService;
    private TrackingApiService trackingService;
    private MroRequestApiService mroRequestService;
    private MachineApiService machineService;


    public RestClient(Context context, CookieStore cookieStore) {
        this.context = context;
        this.cookieStore = cookieStore;
        initApiService();
    }

    // i n i t A p i S e r v i c e
    // ---------------------------

    public void initApiService() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        // HTTP client
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

        // Manage 401 error by retrying the credentials stored in DataStore
        okHttpClient.setAuthenticator(new RetryAuthenticator());

        okHttpClient.interceptors().add(new LoggingInterceptor());
        okHttpClient.networkInterceptors().add(new SessionRequestInterceptor());
        okHttpClient.networkInterceptors().add(new TrackingApiKeyInterceptor());

        // Manage cookies in the store
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        okHttpClient.setCookieHandler(cookieManager);

        // OkClient is retrofit default client that decorates OkHttpClient
        OkClient serviceClient = new OkClient(okHttpClient);

        String apiUrl = HibmobtechApplication.getConfig().getApiUrl();
        Log.d(TAG, "pictureUrlString read from settings: " + apiUrl);

        RestAdapter retrofit = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(apiUrl)
                .setClient(serviceClient)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new RestErrorHandler(context))
                .build();

        authService = retrofit.create(AuthenticatorApiService.class);
        siteService = retrofit.create(SiteApiService.class);
        equipmentService = retrofit.create(EquipmentApiService.class);
        sparePartService = retrofit.create(SparePartApiService.class);
        trackingService = retrofit.create(TrackingApiService.class);
        mroRequestService = retrofit.create(MroRequestApiService.class);
        machineService = retrofit.create(MachineApiService.class);
    }

    // G e t t e r s   &   s e t t e r s
    // ---------------------------------
    public AuthenticatorApiService getAuthService() {
        return authService;
    }

    public EquipmentApiService getEquipmentService() {
        return equipmentService;
    }

    public SparePartApiService getSparePartService() {
        return sparePartService;
    }

    public SiteApiService getSiteService() {
        return siteService;
    }

    public TrackingApiService getTrackingService() {
        return trackingService;
    }

    public MroRequestApiService getMroRequestService() {
        return mroRequestService;
    }

    public MroRequestApiService putMroRequestService() {
        return mroRequestService;
    }

    public MachineApiService getMachineService() { return machineService; }

    public MachineApiService getService() { return machineService; }


    /**
     * c l a s s   R e t r y A u t h e n t i c a t o r
     * -----------------------------------------------
     *
     * OkHttp can automatically retry unauthenticated requests.
     * When a response is 401 Not Authorized, an Authenticator is asked to supply credentials.
     * Implementations should build a new request that includes the missing credentials.
     * If no credentials are available, return null to skip the retry.
     */
    public static class RetryAuthenticator implements Authenticator {
        private static final String TAG = RetryAuthenticator.class.getSimpleName();
        @Override public Request authenticate(Proxy proxy, Response response) {
            Log.i(TAG, "authenticate: Authenticating for response: " + response);
            Log.i(TAG, "authenticate: Challenges: " + response.challenges());

            //HibbizApp.getDataStore().clearAuthToken();
            HibmobtechApplication.getCookieStore().removeAll();

            String username = HibmobtechApplication.getDataStore().getUserName();
            String pass = HibmobtechApplication.getDataStore().getPass();
            if (username == null || username.isEmpty() ||
                    pass == null || pass.isEmpty()) {
                return null;
            }
            String credential = AuthenticatorUtils.getBasicAuthHeader(username, pass);
            // If we already failed with these credentials, don't retry.
            if (credential.equals(response.request().header(AuthenticatorApiService.BASIC_AUTH_HEADER))) {
                return null;
            }
            // If we've failed 2 times, give up.
            if (responseCount(response) >= 2) {
                return null;
            }

            // Authenticate with synchronous service
            AuthenticatorApiService service = HibmobtechApplication.getRestClient().getAuthService();

            PackageInfo packageInfo = HibmobtechApplication.getPackageInfo();
            String packageName = packageInfo.packageName;
            String versionNumber = packageInfo.versionName;
            String appVersion = packageName + ":" + versionNumber;
            Log.d(TAG, "authenticate: appVersion=" + appVersion);

            String deviceId = HibmobtechApplication.getDataStore().getDeviceId();
            User user = service.authenticate(credential, deviceId, appVersion);

            Log.i(TAG, "authenticate: Successfully authenticated " + user.getName());

            // Now retry request
            return response.request().newBuilder()
                    .build();
        }

        @Override public Request authenticateProxy(Proxy proxy, Response response) {
            Log.i(TAG, "authenticateProxy: Authenticating for response: " + response);
            Log.i(TAG, "authenticateProxy: Challenges: " + response.challenges());
            return null; // Null indicates no attempt to authenticate.
        }

        private int responseCount(Response response) {
            int result = 1;
            while ((response = response.priorResponse()) != null) {
                result++;
            }
            return result;
        }
    }


    /**
     * c l a s s   D a t e D e s e r i a l i z e r
     * -------------------------------------------
     *
     * Deserializer pour des formats date multiples.
     */
    private static class DateDeserializer implements JsonDeserializer<Date> {
        private SimpleDateFormat[] formats = {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.Z"),
                new SimpleDateFormat("yyyy-MM-dd")
        };

        /**
         * L'api retourne des dates en UTC. Set the TZ accordingly.
         */
        public DateDeserializer() {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            for (SimpleDateFormat format : formats) {
                format.setTimeZone(tz);
            }
        }

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String j = json.getAsJsonPrimitive().getAsString();
                return parseDate(j);
            } catch (ParseException e) {
                throw new JsonParseException(e.getMessage(), e);
            }
        }

        /**
         * Try the different date formats defined above
         * @param dateString
         * @return
         * @throws ParseException
         */
        private Date parseDate(String dateString) throws ParseException {
            Date zDate = null;

            if (dateString != null && dateString.trim().length() > 0) {
                for (SimpleDateFormat format : formats) {
                    try {
                        zDate = format.parse(dateString.trim());
                        break;
                    } catch (ParseException pe) {
                        Log.d(TAG, String.format("Format %s does not match date %s", format.toPattern(), dateString));
                    }
                }
            }
            if (zDate == null) {
                Log.e(TAG, "Unrecognised date format for "+dateString);
            }
            return zDate;
        }
    }

    /**
     * c l a s s   D a t e S e r i a l i z e r
     *  --------------------------------------
     *
     * Serializer pour des formats date multiples.
     */
    private static class DateSerializer implements JsonSerializer<Date> {

        public DateSerializer() {}

        @Override
        public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {
            Log.d(TAG, "serialize: date=" + date);
            return date == null ? null : new JsonPrimitive(date.getTime());
        }
    }
}